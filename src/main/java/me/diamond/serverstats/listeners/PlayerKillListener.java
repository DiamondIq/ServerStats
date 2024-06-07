package me.diamond.serverstats.listeners;

import me.diamond.serverstats.database.DatabaseUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.Date;
import java.sql.SQLException;

public class PlayerKillListener implements Listener {

    @EventHandler
    public void onPlayerKillEvent(EntityDeathEvent e) throws SQLException {
        if (e.getEntity() instanceof Player) {

            //Track Kills
            if (e.getEntity().getLastDamageCause().getEntity() instanceof Player) {
                DatabaseUtils.Kills.addKills((Player) e.getEntity().getLastDamageCause().getEntity(), new Date(System.currentTimeMillis()));
            }


        }
    }
}
