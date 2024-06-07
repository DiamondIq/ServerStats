package me.diamond.serverstats.config;

import me.diamond.serverstats.ServerStats;
import me.diamond.serverstats.database.DatabaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.Locale;

public class Config {


    private static final JavaPlugin plugin = ServerStats.getPlugin();
    private static FileConfiguration config = null;
    private File configFile;


    public static void init() {
        plugin.getConfig().options().copyDefaults();
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public static FileConfiguration get() {
        return config;
    }

    public static String replaceVariables(String string, Timestamp date, String server_name, int most_players_at_once, Timestamp most_players_at_once_time) {
        String output = string;
        for (ConfigVariables configVariable : ConfigVariables.values()) {
            String variable = "%" + configVariable.name().toLowerCase() + "%";
            if (string.contains(variable)) {
                if (configVariable == ConfigVariables.DATE) {
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                    output =  output.replace(variable, sdf.format(date));
                    String replacement;
//                        replacement = sdf.format(most_players_at_once_time);
                    replacement = "<t:" + date.getTime() / 1000 + ":d>";

                    output = output.replace(variable, replacement);
                } else if (configVariable == ConfigVariables.DAY) {
                    LocalDate localDate = date.toLocalDateTime().toLocalDate();
                    DayOfWeek day = localDate.getDayOfWeek();
                    output = output.replace(variable, day.getDisplayName(TextStyle.FULL, Locale.getDefault()));
                } else if (configVariable == ConfigVariables.HOUR) {
                    String replacement;
//                        replacement = sdf.format(most_players_at_once_time);
                    replacement = "<t:" + date.getTime() / 1000 + ":t>";

//                    output =  output.replace(variable, date.toLocalDateTime().getHour() + ":" + date.toLocalDateTime().getMinute());
                    output = output.replace(variable, replacement);
                } else if (configVariable == ConfigVariables.DAY_OF_THE_MONTH) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String[] s = sdf.format(date).split("/");
                    output = output.replace(variable, s[0]);
                } else if (configVariable == ConfigVariables.MONTH) {
                    LocalDate localDate = date.toLocalDateTime().toLocalDate();
                    Month month = localDate.getMonth();
                    output = output.replace(variable, month.getDisplayName(TextStyle.FULL, Locale.getDefault()));
                } else if (configVariable == ConfigVariables.YEAR) {
                    LocalDate localDate = date.toLocalDateTime().toLocalDate();
                    int year = localDate.getYear();
                    output = output.replace(variable, String.valueOf(year));
                } else if (configVariable == ConfigVariables.MOST_PLAYERS_AT_ONCE) {
                    output = output.replace(variable, String.valueOf(most_players_at_once));
                } else if (configVariable == ConfigVariables.MOST_PLAYERS_AT_ONCE_TIME) {
//                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String replacement;
                    if (most_players_at_once_time != null) {
//                        replacement = sdf.format(most_players_at_once_time);
                        replacement = "<t:" + most_players_at_once_time.getTime() / 1000 + ":t>";
                    } else {
                        replacement = "n/a";
                    }
                    output = output.replace(variable, replacement);
                } else if (configVariable == ConfigVariables.TOTAL_JOINS) {
                    try {
                        output = output.replace(variable, String.valueOf(DatabaseUtils.Joins.getJoinsPerDay(new Date(System.currentTimeMillis()))));
                    } catch (SQLException ex) {
                        Bukkit.getLogger().info("A DataBase error occurred, report this to the ServerStats Discord Server");
                        ex.printStackTrace();
                    }
                }
            }

        }
        return output;
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Error occurred while saving config file!");
            e.printStackTrace();
        }
    }
}
