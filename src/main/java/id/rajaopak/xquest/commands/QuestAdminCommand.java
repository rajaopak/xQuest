package id.rajaopak.xquest.commands;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.commands.subcommands.CreateQuest;
import id.rajaopak.xquest.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class QuestAdminCommand extends CommandManager {
    public QuestAdminCommand() {
        super(XQuest.getInstance(), "xquestadmin", List.of("questadmin", "xqa"), "xquest.admin",
                null,
                sender -> Utils.sendMessage(sender, "&cYou don't have permission to do that!"),
                sender -> Utils.sendMessage(sender, "&cNo subcommand has been found"),
                new CreateQuest());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }
}
