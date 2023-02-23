package id.rajaopak.xquest.mechanics;

import id.rajaopak.xquest.gui.QuestCreatorGui;
import id.rajaopak.xquest.quest.QuestCreator;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class QuestCreateMec {

    private final HashMap<QuestCreatorGui, QuestCreator> creator;

    public QuestCreateMec() {
        this.creator = new HashMap<>();
    }

    public void addCreator(QuestCreatorGui gui, QuestCreator questCreator) {
        if (!this.isCreator(gui.getPlayer())) {
            this.creator.put(gui, questCreator);
        }
    }

    public void removeCreator(Player player) {
        this.creator.remove(this.creator.keySet().stream().filter(gui -> gui.getPlayer() == player).findAny().orElse(null));
    }

    public boolean isCreator(Player player) {
        return this.creator.containsKey(this.creator.keySet().stream().filter(gui -> gui.getPlayer().equals(player)).findAny().orElse(null));
    }

    public HashMap<QuestCreatorGui, QuestCreator> getCreator(Player player) {
        HashMap<QuestCreatorGui, QuestCreator> map = new HashMap<>();

        if (!this.isCreator(player)) {
            return null;
        }

        map.put(this.creator.keySet().stream().filter(gui -> gui.getPlayer() == player).findAny().orElse(null),
                this.creator.get(this.creator.keySet().stream().filter(gui -> gui.getPlayer() == player).findAny().orElse(null)));

        return map;
    }

    public HashMap<QuestCreatorGui, QuestCreator> getCreators() {
        return creator;
    }

    public void clear() {
        this.creator.clear();;
    }
}
