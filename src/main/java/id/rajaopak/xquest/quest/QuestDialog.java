package id.rajaopak.xquest.quest;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestDialog {

    private final QuestHandler questHandler;
    private final YamlConfiguration configuration;
    private final HashMap<Integer, String> dialog;
    private final List<Player> dialogPlayer;

    public QuestDialog(QuestHandler questHandler) {
        this.questHandler = questHandler;
        this.configuration = questHandler.getConfiguration();
        this.dialog = new HashMap<>();
        this.dialogPlayer = new ArrayList<>();
    }

    public static QuestDialog checkIfExist(QuestHandler handler, ConfigurationSection section) {
        QuestDialog questBranch = new QuestDialog(handler);

        if (section.getConfigurationSection("dialog") == null) {
            return null;
        }

        return questBranch;
    }

    public void loadDialog() {
        if (this.configuration.getConfigurationSection("dialog") == null) {
            return;
        }

        this.configuration.getConfigurationSection("dialog").getKeys(false).forEach(s -> {
            String path = "dialog." + s;
            Integer id = this.configuration.getInt(path + ".id");
            String text = this.configuration.getString(path + ".text");

            this.dialog.put(id, text);
        });
    }

    public boolean isInDialog(Player player) {
        return this.getDialogPlayer().contains(player);
    }

    public void playDialog(Player player) {
        List<Integer> id = dialog.keySet().stream().sorted().toList();

        this.dialogPlayer.add(player);
        Utils.sendMessage(player, dialog.get(id.stream().findFirst().orElse(0)));
        for (Integer integer : id) {
            Bukkit.getScheduler().runTaskLater(XQuest.getInstance(), () -> {
                if (dialog.get(integer + 1) != null) {
                    Utils.sendMessage(player, dialog.get(integer + 1));
                }
            }, calculateDelay(dialog.get(integer)));

            if ((dialog.size() - 1) == integer) {
                this.dialogPlayer.remove(player);
            }
        }
    }

    private int calculateDelay(String chatText) {
        int numCharacters = chatText.replaceAll("\\s", "").length(); // Remove white space and count characters
        return (int) Math.ceil((double) numCharacters / 25.0 * 20.0);
    }

    public HashMap<Integer, String> getDialog() {
        return dialog;
    }

    public List<Player> getDialogPlayer() {
        return dialogPlayer;
    }
}
