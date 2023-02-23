package id.rajaopak.xquest.api;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.api.quest.QuestManager;
import id.rajaopak.xquest.player.PlayerHandler;
import id.rajaopak.xquest.quest.QuestHandler;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class XQuestAPI extends JavaPlugin {

    public PlayerHandler getPlayer(String uuid) {
        return XQuest.getInstance().getPlayer(uuid);
    }

    public QuestHandler getQuest(int id) {
        return XQuest.getInstance().getQuest(id);
    }

    public QuestManager getQuestManager() {
        return XQuest.getInstance().getQuestManager();
    }

}
