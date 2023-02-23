package id.rajaopak.xquest.util;

import id.rajaopak.xquest.XQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChatSession implements Listener {

    private Player player;
    private XQuest plugin;
    private Function<Complete, Action> completeActionFunction;
    private Consumer<Player> closeListener;

    public ChatSession plugin(XQuest plugin) {
        this.plugin = plugin;
        return this;
    }

    public void open(Player player) {
        this.player = player;

        Bukkit.getPluginManager().registerEvents(this, this.plugin);

        Utils.sendMessage(player, "&eType \"--close\" to close/cancel the session.");
        Utils.sendMessage(player, "&eEnter your value in the chat.");
    }

    public ChatSession onComplete(Function<Complete, Action> completeActionFunction) {
        this.completeActionFunction = completeActionFunction;
        return this;
    }

    public ChatSession onClose(Consumer<Player> closeListener) {
        this.closeListener = closeListener;
        return this;
    }

    private void close(Player player) {
        this.player = null;
        HandlerList.unregisterAll(this);
        if (this.closeListener != null) {
            this.closeListener.accept(player);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer() == this.player) {
            e.setCancelled(true);
            if (e.getMessage().equalsIgnoreCase("--close")) {
                this.close(e.getPlayer());
                Utils.sendMessage(e.getPlayer(), "&eYou have close the session.");
                return;
            }

            Action actions = this.completeActionFunction.apply(new Complete(e.getPlayer(), e.getFormat(), e.getMessage()));

            actions.accept(e.getPlayer(), e.getMessage());
            this.close(e.getPlayer());
            Utils.sendMessage(e.getPlayer(), "&aSuccessfully set the value.");
        }
    }

    public interface Action extends BiConsumer<Player, String> {
        static Action run(Runnable runnable) {
            return (player1, s) -> {
                System.out.println(player1.getName() + " " + s);
                runnable.run();
            };
        }
    }

    public record Complete(Player player, String format, String message) {
    }
}
