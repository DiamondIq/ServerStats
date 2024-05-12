package me.diamond.serverstats;

import me.diamond.serverstats.database.Database;
import me.diamond.serverstats.database.DatabaseUtils;
import me.diamond.serverstats.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public final class ServerStats extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            new Database(getDataFolder().getAbsolutePath() + "/database.db");
        } catch (SQLException e) {
            getLogger().info("Error Connecting to the database");
            getServer().getPluginManager().disablePlugin(this);
        }

        //Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        Calendar now = Calendar.getInstance();
        int delay = 60 - now.get(Calendar.MINUTE) * 60 * 1000;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                int online_Players = Bukkit.getOnlinePlayers().size();

            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, delay, 60 * 60 * 1000);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
