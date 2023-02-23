package id.rajaopak.xquest;

import id.rajaopak.xquest.api.XQuestAPI;
import id.rajaopak.xquest.commands.QuestAdminCommand;
import id.rajaopak.xquest.commands.QuestCommand;
import id.rajaopak.xquest.file.CacheFile;
import id.rajaopak.xquest.file.ConfigFile;
import id.rajaopak.xquest.file.FileBuilder;
import id.rajaopak.xquest.file.MessageFile;
import id.rajaopak.xquest.mechanics.QuestCreateMec;
import id.rajaopak.xquest.player.PlayerHandler;
import id.rajaopak.xquest.util.ChatSession;
import id.rajaopak.xquest.util.GuiBuilderManager;
import id.rajaopak.xquest.listener.QuestListener;
import id.rajaopak.xquest.quest.QuestHandler;
import id.rajaopak.xquest.quest.QuestManagerImpl;
import id.rajaopak.xquest.redis.RedisManager;
import id.rajaopak.xquest.util.Utils;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public final class XQuest extends XQuestAPI {

    private static XQuest instance;

    private QuestManagerImpl questManager;

    private QuestCreateMec questCreateMec;

    private ChatSession chatSession;

    private ConfigFile config;
    private MessageFile message;
    private CacheFile cacheFile;

    private RedisManager redisManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        this.config = new ConfigFile("config.yml");
        this.message = new MessageFile("message.yml");
        this.cacheFile = new CacheFile("cache.yml");

        if (!this.getSaveFolder().exists()) {
            this.getSaveFolder().mkdirs();
        }

        this.questManager = new QuestManagerImpl(XQuest.getInstance(), this.getSaveFolder());
        this.questCreateMec = new QuestCreateMec();

        this.chatSession = new ChatSession();

        this.redisManager = new RedisManager(this);

        if (this.redisManager.connect(this.config.getRedisHost(),
                this.config.getRedisPort(),
                this.config.getRedisPassword(),
                this.config.getRedisChannel())) {
            Utils.info("&aSuccessfully connecting to the redis server!");
        } else {
            Utils.info("&cFailed connecting to the redis server!");
        }

        GuiBuilderManager.register(this);
        this.getServer().getPluginManager().registerEvents(new QuestListener(), this);

        new QuestCommand().register();
        new QuestAdminCommand().register();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getQuestCreateMec().clear();
        this.getQuestManager().clearCache();
        this.redisManager.close();
        HandlerList.unregisterAll();
    }

    @Override
    public PlayerHandler getPlayer(String uuid) {
        return new PlayerHandler(UUID.fromString(uuid));
    }

    @Override
    public QuestHandler getQuest(int id) {
        return new QuestHandler(id);
    }

    public File getSaveFolder() {
        return new File(this.getDataFolder() + File.separator + "quest");
    }

    public static XQuest getInstance() {
        return instance;
    }

    public @NotNull FileBuilder getConfigs() {
        return config;
    }

    public FileBuilder getMessage() {
        return message;
    }

    public QuestCreateMec getQuestCreateMec() {
        return questCreateMec;
    }

    @Override
    public QuestManagerImpl getQuestManager() {
        return questManager;
    }

    public ChatSession getChatSession() {
        return chatSession;
    }

    public CacheFile getCacheFile() {
        return cacheFile;
    }
}
