package id.rajaopak.xquest.util;

import id.rajaopak.xquest.XQuest;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class RefreshGui extends BukkitRunnable {

    private final XQuest plugin;
    private final GuiBuilder inv;
    private Runnable runnable;

    public RefreshGui(XQuest plugin, GuiBuilder inv) {
        this.plugin = plugin;
        this.inv = inv;
    }

    public void start(@NotNull Runnable runnable, int ticks) {
        this.runnable = runnable;
        runTaskTimerAsynchronously(this.plugin, 0, ticks);
    }

    @Override
    public void run() {
        if (this.inv.getInventory().getViewers().isEmpty()) {
            this.cancel();
        }

        this.runnable.run();
        this.inv.updateInventory();
    }
}
