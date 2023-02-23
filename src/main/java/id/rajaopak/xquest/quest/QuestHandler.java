package id.rajaopak.xquest.quest;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.util.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class QuestHandler {

    private final int id;
    private final File file;
    private final YamlConfiguration configuration;

    private QuestObject questObject;
    private QuestRewards questRewards;
    private QuestDialog questDialog;

    public QuestHandler(int id) {
        this(id, new File(XQuest.getInstance().getSaveFolder() + File.separator + id));
    }

    public QuestHandler(File file) {
        this(Integer.parseInt(file.getName().split("\\.")[0]), file);
    }

    public QuestHandler(int id, File file) {
        this.id = id;
        this.file = file;
        this.questObject = new QuestObject(this);
        this.questRewards = new QuestRewards(this);
        this.questDialog = new QuestDialog(this);
        this.configuration = YamlConfiguration.loadConfiguration(file);

        this.questObject.loadObject();
        this.questRewards.loadRewards();
        this.questDialog.loadDialog();
    }

    public static QuestHandler loadFromFile(File file) {
        try {
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.load(file);
            return checkIfExist(file, configuration);
        } catch (Exception e) {
            Utils.logSevere("An error occurred while load quest file " + file.getName().toUpperCase() + ".", e);
            return null;
        }
    }

    public static QuestHandler checkIfExist(File file, ConfigurationSection sec) {
        if (!sec.contains("id")) {
            Utils.logSevere("Quest doesn't have an id.");
            return null;
        }

        QuestHandler q = new QuestHandler(sec.getInt("id"), file);

        if (QuestObject.checkIfExist(q, sec) != null) {
            q.questObject = QuestObject.checkIfExist(q, sec);
        }

        if (QuestRewards.checkIfExist(q, sec) != null) {
            q.questRewards = QuestRewards.checkIfExist(q, sec);
        }

        if (QuestDialog.checkIfExist(q, sec) != null) {
            q.questDialog = QuestDialog.checkIfExist(q, sec);
        }

        return q;
    }

    public int getId() {
        return this.id;
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public QuestObject getObject() {
        return this.questObject;
    }

    public QuestRewards getQuestRewards() {
        return questRewards;
    }

    public QuestDialog getQuestDialog() {
        return questDialog;
    }
}
