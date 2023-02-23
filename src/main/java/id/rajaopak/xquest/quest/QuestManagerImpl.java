package id.rajaopak.xquest.quest;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.api.quest.QuestManager;
import id.rajaopak.xquest.util.Utils;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class QuestManagerImpl implements QuestManager {

    private final List<QuestHandler> questHandlers = new ArrayList<>();

    private final XQuest plugin;
    private final File saveFolder;

    public QuestManagerImpl(XQuest plugin, File saveFolder) {
        this.plugin = plugin;
        this.saveFolder = saveFolder;

        try {
            this.loadAllQuest();
        } catch (IOException e) {
            Utils.logSevere("An error occurred while loading quests.", e);
        }
    }

    public void loadAllQuest() throws IOException {
        try (Stream<Path> files = Files.walk(this.saveFolder.toPath(), Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS)) {
            files.filter(Files::isRegularFile).filter(path -> path.toString().endsWith("yml")).forEach(path -> {
                try {
                    File file = path.toFile();
                    QuestHandler quest = QuestHandler.loadFromFile(file);
                    addQuest(quest);
                } catch (Exception e) {
                    Utils.logSevere("An error occurred while loading quest file " + path.getFileName(), e);
                }
            });
        }
    }

    public QuestHandler getQuest(int id) {
        return questHandlers.stream().filter(questHandler -> questHandler.getId() == id).findAny().orElse(null);
    }

    public void addQuest(QuestHandler handler) {
        if (questHandlers.contains(handler)) return;
        questHandlers.add(handler);
    }

    public void deleteQuest(QuestHandler handler) {
        questHandlers.remove(handler);
        handler.getFile().delete();
    }

    public boolean checkIfPlayerInQuest(Player player) {
        AtomicInteger id = new AtomicInteger();

        XQuest.getInstance().getCacheFile().getConfiguration().getConfigurationSection("").getKeys(false).forEach(s -> {
            if (s.equalsIgnoreCase(player.getUniqueId().toString())) {
                id.set(XQuest.getInstance().getCacheFile().getConfiguration().getInt(s + "id"));
            }
        });

        return questHandlers.stream().map(questHandler -> questHandler.getId() == id.get()).findAny().orElse(false);
    }

    public QuestHandler getPlayerProgressionQuest(Player player) {
        AtomicInteger id = new AtomicInteger();

        XQuest.getInstance().getCacheFile().getConfiguration().getConfigurationSection("").getKeys(false).forEach(s -> {
            if (s.equalsIgnoreCase(player.getUniqueId().toString())) {
                id.set(XQuest.getInstance().getCacheFile().getConfiguration().getInt(s + "id"));
            }
        });

        return questHandlers.stream().filter(questHandler -> questHandler.getId() == id.get()).findAny().orElse(null);
    }

    public void clearCache() {
        this.questHandlers.clear();
    }

}
