package id.rajaopak.xquest.commands;

import id.rajaopak.xquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class CommandManager extends Command implements Listener {

    protected final Map<String, SubCommand> subCommandMap = new HashMap<>();
    protected final Map<Integer, Map.Entry<List<String>, @Nullable String>> tabComplete = new HashMap<>();

    protected final JavaPlugin plugin;
    protected final @NotNull String COMMAND_NAME;
    protected final @NotNull List<String> COMMAND_ALIASES;
    protected final @Nullable String COMMAND_PERMISSION;
    /*protected final @Nullable BiConsumer<CommandSender, String[]> execute;*/
    protected final Consumer<CommandSender> onNoArgs;
    protected final Consumer<CommandSender> onNoPermission;
    protected final Consumer<CommandSender> onNoSubcommand;


    public CommandManager(JavaPlugin plugin, @NotNull String name, @NotNull List<String> aliases, @Nullable String COMMAND_PERMISSION,
                       Consumer<CommandSender> onNoArgs,
                       Consumer<CommandSender> onNoPermission, Consumer<CommandSender> onNoSubcommand,
                       /*@Nullable BiConsumer<CommandSender, String[]> execute,*/
                       @Nullable SubCommand... subCommands) {
        super(name, "xQuest commands", "/" + name, aliases);
        this.plugin = plugin;
        this.COMMAND_NAME = name;
        this.COMMAND_ALIASES = aliases;
        this.COMMAND_PERMISSION = COMMAND_PERMISSION;
        this.onNoArgs = onNoArgs;
        this.onNoPermission = onNoPermission;
        this.onNoSubcommand = onNoSubcommand;
        /*this.execute = execute;*/
        this.setPermission(COMMAND_PERMISSION);
        this.setAliases(COMMAND_ALIASES);
        this.setName(COMMAND_NAME);
        this.setLabel(COMMAND_NAME);

        if (subCommands != null) {
            for (SubCommand subCommand : subCommands) {
                this.subCommandMap.put(subCommand.getName().toLowerCase(), subCommand);
                this.setUsage("/" + COMMAND_NAME + " " + subCommand.getUsage());
            }
        } else {
            this.setUsage("/" + COMMAND_NAME);
        }
    }

    public void register() {
        String version = Utils.getNmsVersion();
        switch (version) {
            case "v1_8_R3", "v1_9_R1", "v1_9_R2", "v1_10_R1", "v1_11_R1", "v1_12_R2" -> {}
            default -> Bukkit.getPluginManager().registerEvents(this, plugin);
        }

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(getLabel(), this);
            commandMapField.setAccessible(false);
        } catch (Exception e) {
            Utils.logSevere("&cFailed to register command! [" + getName() + "]");
            e.printStackTrace();
        }
    }

    public abstract void execute(CommandSender sender, String[] args);

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(COMMAND_PERMISSION != null && !sender.hasPermission(COMMAND_PERMISSION)){
            onNoPermission.accept(sender);
            return false;
        }

        if (args.length == 0 && onNoArgs != null) {
            onNoArgs.accept(sender);
            return false;
        } else if (args.length == 0) {
            execute(sender, args);
            return true;
        }

        /*if (execute != null) {
            execute.accept(sender, args);
            return true;
        }*/

        SubCommand subCommand = this.subCommandMap.get(args[0].toLowerCase());
        if (subCommand == null) {
            onNoSubcommand.accept(sender);
            return false;
        }

        if (!args[0].equals(subCommand.getName())) {
            execute(sender, args);
            return true;
        }

        if (subCommand.getPermission() != null &&
                !sender.hasPermission(subCommand.getPermission())) {
            onNoPermission.accept(sender);
            return false;
        }

        subCommand.execute(plugin, sender, args);
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Set<Integer> a = tabComplete.keySet();

        for (Integer b : a) {
            Map.Entry<List<String>, String> c = tabComplete.get(b);

            if (args.length == b) {
                if (c.getValue() == null) {
                    return c.getKey();
                }

                if (sender.hasPermission(c.getValue())) {
                    return c.getKey();
                }

                return Collections.emptyList();
            }
        }

        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            for (SubCommand subCommand : subCommandMap.values()) {
                if (subCommand.getPermission() == null) {
                    suggestions.add(subCommand.getName());
                    continue;
                }
                if (sender.hasPermission(subCommand.getPermission())) {
                    suggestions.add(subCommand.getName());
                }
            }
            return suggestions;
        }
        if (args.length >= 2) {
            SubCommand subCommand = subCommandMap.get(args[0].toLowerCase());
            if (subCommand == null) {
                return new ArrayList<>();
            }
            if (subCommand.getPermission() == null ||
                    sender.hasPermission(subCommand.getPermission())) {
                return Objects.requireNonNull(subCommand.parseTabCompletions(plugin, sender, args));
            }
        }

        return new ArrayList<>();
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandSendEvent event) {
        Player p = event.getPlayer();

        if (event.getCommands().stream().anyMatch(command -> command.startsWith(COMMAND_NAME))) {
            if (COMMAND_PERMISSION != null && !p.hasPermission(COMMAND_PERMISSION)) {
                event.getCommands().remove(COMMAND_NAME);
            } else {
                event.getCommands().add(COMMAND_NAME);
            }
        }
    }

    public void addTabComplete(int args, List<String> list, @Nullable String permission) {
        tabComplete.put(args, new AbstractMap.SimpleEntry<>(list, permission));
    }

}
