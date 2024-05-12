package me.diamond.serverstats.listeners;

import me.diamond.serverstats.config.Config;
import me.diamond.serverstats.database.DatabaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws SQLException {
        if (Config.get().getBoolean("Track-Daily-Stats.Most-Players-Online")) {
            Collection<? extends Player> online_Players = Bukkit.getOnlinePlayers();
            int online_Players_Amount = online_Players.size();
            if (online_Players_Amount >= DatabaseUtils.getMostPlayersInADay(new Date(System.currentTimeMillis()))) {
                DatabaseUtils.updateMostPlayersInADay(online_Players_Amount, new Timestamp(System.currentTimeMillis()));
            }
        }
        if (Config.get().getBoolean("Track-Daily-Stats.Joins")){
            DatabaseUtils.addJoinsPerDay(1, new Date(System.currentTimeMillis()));
        }
    }

}
