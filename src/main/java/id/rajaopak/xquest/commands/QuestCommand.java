package id.rajaopak.xquest.commands;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class QuestCommand extends CommandManager {

    public QuestCommand() {
        super(XQuest.getInstance(), "xquest", List.of("quest", "xq"), "xquest.use",
                null,
                sender -> Utils.sendMessage(sender, "&cYou don't have permission to do that!"),
                sender -> Utils.sendMessage(sender, "&cNo subcommand has been found"),
                null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }
}
