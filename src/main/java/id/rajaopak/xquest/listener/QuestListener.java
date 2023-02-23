package id.rajaopak.xquest.listener;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.quest.QuestHandler;
import id.rajaopak.xquest.quest.QuestType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;

public class QuestListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

        if (XQuest.getInstance().getQuestManager().checkIfPlayerInQuest(p)) {
            QuestHandler q = XQuest.getInstance().getQuestManager().getPlayerProgressionQuest(p);

            HashMap<QuestType, Object> map = q.getObject().getObjects();

            map.keySet().forEach(type -> {
                if (type.equals(QuestType.BREAK)) {
                    Material material = Material.getMaterial(map.get(type).toString().split("=")[0].toUpperCase());
                    int amount = Integer.parseInt(map.get(type).toString().split("=")[1]);
                    boolean status = XQuest.getInstance().getCacheFile().getBoolean(p.getUniqueId() + ".status");

                    if (amount == 0) {
                        XQuest.getInstance().getCacheFile().set(p.getUniqueId() + ".status", true);
                        return;
                    }

                    if (!status && e.getBlock().getType().equals(material)) {
                        amount--;
                        XQuest.getInstance().getCacheFile().set(p.getUniqueId() + ".amount", amount);
                    }
                }
            });
        }
    }

}
