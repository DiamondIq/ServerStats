package me.diamond.serverstats.discord;

import me.diamond.serverstats.ServerStats;
import me.diamond.serverstats.config.Config;
import me.diamond.serverstats.database.DatabaseUtils;
import me.diamond.serverstats.utils.ChartUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import org.bukkit.Bukkit;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Timers {
    private static final LocalTime targetTime = LocalTime.of(23, 59); // 23:59
    private static LocalDateTime lastDateTime = LocalDateTime.now();

    public static void startEveryHourTask() {
        Calendar now = Calendar.getInstance();

        // Calculate the delay until the start of the next hour
        int minutes = now.get(Calendar.MINUTE);
        int seconds = now.get(Calendar.SECOND);
        int milliseconds = now.get(Calendar.MILLISECOND);

        // Time left until the next hour
        int delay = ((60 - minutes) * 60 - seconds) * 1000 - milliseconds;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                int onlinePlayers = Bukkit.getOnlinePlayers().size();
                try {
                    DatabaseUtils.setOnlinePlayersPerHour(onlinePlayers, new Timestamp(System.currentTimeMillis()));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, delay, 60 * 60 * 1000);
    }

    public static void startEveryDayTask() throws IOException, FontFormatException {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LocalDateTime currentDateTime = LocalDateTime.now();
                LocalDateTime nextExecutionTime = currentDateTime.with(targetTime);
                if (nextExecutionTime.isBefore(currentDateTime)) {
                    nextExecutionTime = nextExecutionTime.plusDays(1); // Move to the next day
                }
                long delay = ChronoUnit.MILLIS.between(currentDateTime, nextExecutionTime);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (Config.get().getBoolean("Enable-Discord")) {
                            if (Config.get().getBoolean("Daily-Stats.enable")) {
                                TextChannel channel = ServerStats.getJda().getTextChannelById(Config.get().getLong("Daily-Stats.Channel-id"));
                                if (channel != null) {

                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setDescription(Config.replaceVariables(Config.get().getString("Daily-Stats.message"), new Timestamp(System.currentTimeMillis()), Bukkit.getServer().getName(), DatabaseUtils.getMostPlayersInADay(new Date(System.currentTimeMillis())), DatabaseUtils.getMostPlayersInADayTime(new Date(System.currentTimeMillis()))));
                                    HashMap<Integer, String> values1 = new HashMap<>();
                                    try {
                                        DatabaseUtils.getValuesInDate("most_players_per_day", new Date(System.currentTimeMillis())).forEach((key, value) -> {
                                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                            values1.put(key, sdf.format(value));
                                        });
                                        HashMap<Integer, String> values2 = new HashMap<>();
                                        DatabaseUtils.getValuesInDate("most_players_per_day", new Date(System.currentTimeMillis())).forEach((key, value) -> {
                                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                            values2.put(key, sdf.format(value));
                                        });


                                        //Add Image
                                        if (Config.get().getBoolean("Daily-Stats.add-chart")) {
                                            BufferedImage image = null;
                                            BufferedImage image1 = null;
                                            try {
                                                image = ChartUtils.generateLineGraph(values1);
                                                image1 = ChartUtils.generateLineGraph(values2);

                                                if (Config.get().getBoolean("Use-Bar-Charts")) {
                                                    image = ChartUtils.generateBarChart(values1);
                                                    image1 = ChartUtils.generateLineGraph(values2);

                                                }
                                            } catch (Exception ex) {
                                                System.out.println(ex);
                                            }

                                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                                            try {
                                                ImageIO.write(image, "jpg", os);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            InputStream is = new ByteArrayInputStream(os.toByteArray());
                                            ByteArrayOutputStream os2 = new ByteArrayOutputStream();
                                            try {
                                                ImageIO.write(image1, "jpg", os2);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            InputStream is1 = new ByteArrayInputStream(os2.toByteArray());
                                            channel.sendFiles(FileUpload.fromData(is, "image.jpg"), FileUpload.fromData(is1, "image.jpg")).queue();
                                        }
                                        embed.setFooter("By ServerStats " + ServerStats.getPlugin().getDescription().getVersion());

                                        channel.sendMessageEmbeds(embed.build()).queue();
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                            }
                        }
                        lastDateTime = LocalDateTime.now();
                    }
                }, delay);
            }
        }, 0, 60 * 60 * 1000); // Check every hour


    }

    private static String imageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
