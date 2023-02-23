package id.rajaopak.xquest.commands.subcommands;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.commands.SubCommand;
import id.rajaopak.xquest.gui.QuestCreatorGui;
import id.rajaopak.xquest.quest.QuestCreator;
import id.rajaopak.xquest.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CreateQuest extends SubCommand {
    @Override
    public @NotNull String getName() {
        return "create";
    }

    @Override
    public @Nullable String getUsage() {
        return "create <name>";
    }

    @Override
    public @Nullable String getPermission() {
        return "xquest.create";
    }

    @Override
    public @Nullable List<String> parseTabCompletions(JavaPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void execute(JavaPlugin plugin, CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            Utils.sendMessage(sender, "&cYou can't do that");
            return;
        }

        if (XQuest.getInstance().getQuestCreateMec().isCreator(p)) {
            XQuest.getInstance().getQuestCreateMec().getCreator(p).keySet().forEach(QuestCreatorGui::open);
        } else {
            QuestCreatorGui gui = new QuestCreatorGui(p, new QuestCreator());
            if (args.length == 2) {
                gui.getCreator().setName(args[1]);
            }

            gui.open();
            XQuest.getInstance().getQuestCreateMec().addCreator(gui, gui.getCreator());
        }
    }
}
