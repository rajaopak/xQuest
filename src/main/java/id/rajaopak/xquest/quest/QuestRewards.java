package id.rajaopak.xquest.quest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class QuestRewards {

    private final QuestHandler questHandler;
    private final YamlConfiguration configuration;
    private final HashMap<Object, Object> startRewards;
    private final HashMap<Object, Object> endRewards;

    public QuestRewards(QuestHandler questHandler) {
        this.questHandler = questHandler;
        this.configuration = questHandler.getConfiguration();
        this.startRewards = new HashMap<>();
        this.endRewards = new HashMap<>();
    }

    public void loadRewards() {
        if (this.configuration.getConfigurationSection("rewards.startrewards") == null ||
                this.configuration.getConfigurationSection("rewards.endrewards") == null ) {
            return;
        }

        this.configuration.getConfigurationSection("rewards.startrewards").getKeys(false).forEach(s -> {
            String key = "rewards.startrewards." + s;

            this.startRewards.put(this.configuration.get(key + ".value-1"),this.configuration.get(key + ".value-2"));
        });

        this.configuration.getConfigurationSection("rewards.endrewards").getKeys(false).forEach(s -> {
            String key = "rewards.endrewards." + s;

            this.endRewards.put(this.configuration.get(key + ".value-1"),this.configuration.get(key + ".value-2"));
        });
    }

    public static QuestRewards checkIfExist(QuestHandler handler, ConfigurationSection section) {
        QuestRewards questBranch = new QuestRewards(handler);

        if (section.getConfigurationSection("rewards") == null) {
            return null;
        }

        return questBranch;
    }
}
