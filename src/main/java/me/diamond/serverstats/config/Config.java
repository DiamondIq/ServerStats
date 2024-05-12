package me.diamond.serverstats.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.Locale;

public class Config {


    private final JavaPlugin plugin;
    private static FileConfiguration config = null;

    public Config(JavaPlugin plugin){
        this.plugin = plugin;
        config = plugin.getConfig();
        plugin.getConfig().options().copyDefaults();
        plugin.saveDefaultConfig();
    }

    public static FileConfiguration get(){
        return config;
    }

    public static String replaceVariables(String string, Date date, String server_name, int most_players_at_once, Timestamp most_players_at_once_time){
        for (ConfigVariables configVariable : ConfigVariables.values()){
            String variable = "%" + configVariable.name().toLowerCase() + "%";
            if (string.contains(variable)){
                if (configVariable == ConfigVariables.DATE){
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    return string.replace(variable, sdf.format(date));
                } else if (configVariable == ConfigVariables.DAY) {
                    LocalDate localDate = date.toLocalDate();
                    DayOfWeek day = localDate.getDayOfWeek();
                    return string.replace(variable, day.getDisplayName(TextStyle.FULL, Locale.getDefault()));
                } else if (configVariable == ConfigVariables.HOUR) {
                    return string.replace(variable, null); //todo - replace with the actual hour
                } else if (configVariable == ConfigVariables.DAY_OF_THE_MONTH){
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String[] s = sdf.format(date).split("/");
                    return string.replace(variable, s[0]);
                } else if (configVariable == ConfigVariables.MONTH){
                    LocalDate localDate = date.toLocalDate();
                    Month month = localDate.getMonth();
                    return string.replace(variable, month.getDisplayName(TextStyle.FULL, Locale.getDefault()));
                } else if (configVariable == ConfigVariables.YEAR){
                    LocalDate localDate = date.toLocalDate();
                    int year = localDate.getYear();
                    return string.replace(variable, String.valueOf(year));
                } else if (configVariable == ConfigVariables.SERVER_NAME){
                    return string.replace(variable, server_name);
                } else if (configVariable == ConfigVariables.MOST_PLAYERS_AT_ONCE){
                    return string.replace(variable, String.valueOf(most_players_at_once));
                } else if (configVariable == ConfigVariables.MOST_PLAYERS_AT_ONCE_TIME){
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    return string.replace(variable, sdf.format(most_players_at_once_time));
                }
            }

        }
    }

}
