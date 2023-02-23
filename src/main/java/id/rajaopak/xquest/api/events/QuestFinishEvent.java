package id.rajaopak.xquest.api.events;

import id.rajaopak.xquest.quest.QuestHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class QuestFinishEvent extends Event {

    private final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final QuestHandler questHandler;

    public QuestFinishEvent(Player player, QuestHandler questHandler) {
        this.player = player;
        this.questHandler = questHandler;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public Player getPlayer() {
        return player;
    }

    public QuestHandler getQuestHandler() {
        return questHandler;
    }
}
