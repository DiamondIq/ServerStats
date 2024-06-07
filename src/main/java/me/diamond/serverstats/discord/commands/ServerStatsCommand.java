package me.diamond.serverstats.discord.commands;

import me.diamond.serverstats.ServerStats;
import me.diamond.serverstats.config.Config;
import me.diamond.serverstats.database.DatabaseUtils;
import me.diamond.serverstats.utils.ChartUtils;
import me.diamond.serverstats.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.Base64;

public class ServerStatsCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        if (e.getFullCommandName().equalsIgnoreCase("server-stats")) {
            OptionMapping option = e.getOption("stat");
            if (option == null) {
                e.reply("No stat option provided!").queue();
                return;
            }

            String stat = option.getAsString();
            switch (stat) {
                case "joins" -> handleJoins(e);
                case "players online" -> handlePlayersOnline(e);
                default -> e.reply("Invalid stat option!").queue();
            }
        }
    }

    private void handleJoins(SlashCommandInteractionEvent e) {
        BufferedImage image;
        String uuid = UUID.randomUUID().toString();
        File outputFile = new File(uuid.substring(uuid.length() - 10) + ".jpg");

        try {
            if (!outputFile.exists() && !outputFile.createNewFile()) {
                throw new IOException("Failed to create output file");
            }

            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_YEAR, 7);

            HashMap<Integer, Timestamp> rawValues = (HashMap<Integer, Timestamp>) DatabaseUtils.getValuesBetweenDates("joins_per_day", new Timestamp(TimeUtils.getStartOfDay(calendar.getTime())), new Timestamp(TimeUtils.getEndOfDay(new Date())));
            HashMap<Integer, String> values = new HashMap<>();
            rawValues.forEach((key, value) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                values.put(key, sdf.format(value));
            });



            image = ChartUtils.generateLineGraph(values);
            if (Config.get().getBoolean("Use-Bar-Charts")) {
                image = ChartUtils.generateBarChart(values);
            }

            ImageIO.write(image, "jpg", outputFile);
            e.replyFiles(FileUpload.fromData(outputFile)).queue();
        } catch (Exception ex) {
            ex.printStackTrace();
            e.reply("An error occurred while processing your request!").queue();
        } finally {
            if (outputFile.exists() && !outputFile.delete()) {
                System.err.println("Failed to delete output file: " + outputFile.getPath());
            }
        }
    }


    private void handlePlayersOnline(SlashCommandInteractionEvent e) {
        BufferedImage image = null;
        String uuid = UUID.randomUUID().toString();
        File outputFile = new File(uuid.substring(uuid.length() - 10) + ".jpg");

        try {
            if (!outputFile.exists() && !outputFile.createNewFile()) {
                throw new IOException("Failed to create output file");
            }

            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_YEAR, 7);

            HashMap<Integer, String> values = new HashMap<>();
            DatabaseUtils.getValuesInDate("most_players_per_hour", new java.sql.Date(System.currentTimeMillis())).forEach((key, value) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                values.put(key, sdf.format(value));
            });

            try {
                image = ChartUtils.generateLineGraph(values);
                if (Config.get().getBoolean("Use-Bar-Charts")) {
                    image = ChartUtils.generateBarChart(values);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (image != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                sendPlayerStatsEmbed(e);
            } else {
                e.reply("Failed to generate image.").queue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e.reply("An error occurred while processing your request!").queue();
        }
    }

    private static String imageToBase64(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("image == null!");
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void sendPlayerStatsEmbed(SlashCommandInteractionEvent e) {
        EmbedBuilder embed = new EmbedBuilder();

        // Fetching necessary data

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // Formatting the time and date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");



        // Fetching the message template from config
        String messageTemplate = Config.get().getString("Commands.Server-Stats.Players-Online-Msg");
        String formattedMessage = Config.replaceVariables(messageTemplate, new Timestamp(System.currentTimeMillis()), Bukkit.getServer().getName(), DatabaseUtils.getMostPlayersInADay(new java.sql.Date(System.currentTimeMillis())), DatabaseUtils.getMostPlayersInADayTime(new java.sql.Date(System.currentTimeMillis())));
        formattedMessage = formattedMessage.replace("\\n", "\n");
        formattedMessage = formattedMessage.replace("\\", "");

        // Building the embed
        embed.setTitle("Server Stats: Players Online");
        embed.setDescription(formattedMessage);
        embed.setColor(Color.RED);
        embed.setFooter("By ServerStats " + ServerStats.getPlugin().getDescription().getVersion() + " | " + dateFormat.format(currentTimestamp));

        // Sending the embed
        e.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
