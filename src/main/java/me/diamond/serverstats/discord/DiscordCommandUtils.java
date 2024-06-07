package me.diamond.serverstats.discord;

import me.diamond.serverstats.ServerStats;
import me.diamond.serverstats.config.Config;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;

public class DiscordCommandUtils {
    public static void registerCommands() {
        if (ServerStats.getJda() != null) {
            Guild guild = ServerStats.getJda().getGuildById(GuildUtils.GUILD_ID);
            OptionData server_stats_option_data = new OptionData(OptionType.STRING, "stat", "The type of stat you want to check", true);
            if (Config.get().getBoolean("Track-Stats.Joins")) {
                server_stats_option_data.addChoice("Joins", "joins");
            }
            if (Config.get().getBoolean("Track-Stats.Most-Players-Online")) {
                server_stats_option_data.addChoice("Players Online", "players online");
            }
            guild.upsertCommand("server-stats", "Check The stats of your minecraft server")
                    .addOptions(server_stats_option_data)
                    .queue();


        } else  {
            Bukkit.getLogger().info("Error Occured While Registering Commands For the discord bot");
        }

    }
}
