package id.rajaopak.xquest.api.events;

import id.rajaopak.xquest.quest.QuestHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class QuestCancelEvent extends Event implements Cancellable {

    private final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final QuestHandler questHandler;
    private boolean unCancelableQuest = false;

    public QuestCancelEvent(Player player, QuestHandler questHandler) {
        this.player = player;
        this.questHandler = questHandler;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return unCancelableQuest;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.unCancelableQuest = cancel;
    }

    public QuestHandler getQuestHandler() {
        return questHandler;
    }

    public Player getPlayer() {
        return player;
    }
}
