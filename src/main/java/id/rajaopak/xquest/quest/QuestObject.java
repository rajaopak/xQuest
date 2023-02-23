package id.rajaopak.xquest.quest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Objects;

public class QuestObject {

    private final QuestHandler questHandler;
    private final YamlConfiguration configuration;
    private final HashMap<QuestType, Object> objects;

    public QuestObject(QuestHandler questHandler) {
        this.questHandler = questHandler;
        this.configuration = questHandler.getConfiguration();
        this.objects = new HashMap<>();
    }

    public void loadObject() {
        if (this.configuration.getConfigurationSection("object.objects") == null) {
            return;
        }

        this.configuration.getConfigurationSection("object.objects").getKeys(false).forEach(s -> {
            String key = "object.objects." + s;

            this.objects.put(QuestType.parseString(this.configuration.getString(key + ".type")),
                    Objects.requireNonNull(this.configuration.get(key + ".value-1")).toString().toUpperCase()
                            + "=" + (this.configuration.get(key + ".amount") != null ? this.configuration.get(key + ".amount") : ""));
        });
    }

    public HashMap<QuestType, Object> getObjects() {
        return this.objects;
    }

    public static QuestObject checkIfExist(QuestHandler handler, ConfigurationSection section) {
        QuestObject questBranch = new QuestObject(handler);

        if (section.getConfigurationSection("object") == null) {
            return null;
        }

        return questBranch;
    }

}
