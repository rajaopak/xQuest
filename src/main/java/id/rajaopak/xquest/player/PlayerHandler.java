package id.rajaopak.xquest.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerHandler {

    private final OfflinePlayer player;

    public PlayerHandler(UUID uuid) {
        this(Bukkit.getOfflinePlayer(uuid));
    }

    public PlayerHandler(String name) {
        this(Bukkit.getOfflinePlayer(name));
    }

    public PlayerHandler(Player player) {
        this(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    public PlayerHandler(OfflinePlayer offlinePlayer) {
        this.player = offlinePlayer;
    }



}
