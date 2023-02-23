package id.rajaopak.xquest.util;

import id.rajaopak.xquest.XQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manager for SimpleInventory listeners.
 *
 * @author MrMicky, (edited by) rajaopak
 */
public class GuiBuilderManager {

    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

    private GuiBuilderManager() {
        throw new UnsupportedOperationException();
    }

    /**
     * Register listeners for SimpleInventory.
     *
     * @param plugin plugin to register
     * @throws NullPointerException if plugin is null
     * @throws IllegalStateException if SimpleInventory is already registered
     */
    public static void register(XQuest plugin) {
        if (REGISTERED.getAndSet(true)) {
            throw new IllegalStateException("SimpleInventory is already registered");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
    }

    /**
     * Close all open SimpleInventory inventories.
     */
    public static void closeAll() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getOpenInventory().getTopInventory().getHolder() instanceof GuiBuilder)
                .forEach(Player::closeInventory);
    }

    public static final class InventoryListener implements Listener {

        private final XQuest plugin;

        public InventoryListener(XQuest plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (e.getInventory().getHolder() instanceof GuiBuilder inv && e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) {

                if (inv.isTakeAble()) {
                    inv.handleClick(e);
                    return;
                }

                boolean wasCancelled = e.isCancelled();
                e.setCancelled(true);

                inv.handleClick(e);

                // This prevents un-canceling the event if another plugin canceled it before
                if (!wasCancelled && !e.isCancelled()) {
                    e.setCancelled(false);
                }
            }
        }

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent e) {
            if (e.getInventory().getHolder() instanceof GuiBuilder inv) {

                inv.handleOpen(e);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            if (e.getInventory().getHolder() instanceof GuiBuilder inv) {

                if (inv.handleClose(e)) {
                    Bukkit.getScheduler().runTask(this.plugin, () -> inv.open(e.getPlayer()));
                }

                if (inv.isUnCloseable()) {
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> inv.open(e.getPlayer()), 1);
                }
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent e) {
            if (e.getPlugin() == this.plugin) {
                closeAll();

                REGISTERED.set(false);
            }
        }
    }

}
