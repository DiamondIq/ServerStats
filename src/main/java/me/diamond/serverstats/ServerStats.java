package me.diamond.serverstats;

import me.diamond.serverstats.config.Config;
import me.diamond.serverstats.database.Database;
import me.diamond.serverstats.discord.DiscordCommandUtils;
import me.diamond.serverstats.discord.Timers;
import me.diamond.serverstats.discord.commands.ServerStatsCommand;
import me.diamond.serverstats.listeners.PlayerJoinListener;
import me.diamond.serverstats.listeners.PlayerKillListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public final class ServerStats extends JavaPlugin {

    private static JavaPlugin plugin;

    private static JDA jda;


    @Override
    public void onEnable() {

        plugin = this;


        //Initialize Config
        Config config = new Config();
        Config.init();

        //Initialize database
        try {
            String path = getDataFolder().getAbsolutePath().replace("\\", "/") + "/database.db";
            Database.init(path);
        } catch (SQLException e) {
            getLogger().info("Error Connecting to the database");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        //Register Listeners
        registerEvents();

        try {
            Timers.startEveryHourTask();
            Timers.startEveryDayTask();
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }

        try {
            if (Config.get().getBoolean("Enable-Discord")) {
                jda = JDABuilder.createDefault(Config.get().getString("Bot-Token"))
                        .setActivity(Activity.playing("Server Stats")) //Todo - Set to Spigot link
                        .addEventListeners(new ServerStatsCommand())
//                        .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                        .setStatus(OnlineStatus.ONLINE)
                        .build().awaitReady();

                getLogger().info("Discord Bot connected");
                DiscordCommandUtils.registerCommands();
            }

        } catch (InterruptedException e) {
            Config.get().set("Enable-Discord", false);
            getLogger().info("Invalid Discord Bot Token");
        }
    }


    @Override
    public void onDisable() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    private void registerEvents(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(), this);
        pm.registerEvents(new PlayerKillListener(), this);
    }
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static JDA getJda() {
        return jda;
    }
}
