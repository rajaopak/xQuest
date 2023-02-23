package id.rajaopak.xquest.listener;

import id.rajaopak.xquest.XQuest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class CreatorListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (XQuest.getInstance().getQuestCreateMec().isCreator(e.getPlayer())) {

        }
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent e) {
        if (XQuest.getInstance().getQuestCreateMec().isCreator(e.getPlayer())) {

        }
    }

}
