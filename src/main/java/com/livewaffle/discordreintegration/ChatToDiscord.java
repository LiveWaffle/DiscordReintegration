package com.livewaffle.discordreintegration;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class ChatToDiscord {

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (MainConfigurations.enableJoinsLeaves) {
            String playerName = event.player.getCommandSenderName();
            createEmbed("Player Joined", "**" + playerName + "** joined the server", 5763719, playerName);
            updateBotStatus();
        }
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (MainConfigurations.enableJoinsLeaves) {
            String playerName = event.player.getCommandSenderName();
            createEmbed("Player Left", "**" + playerName + "** left the server", 5763719, playerName);
            updateBotStatus();
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        String playerName = event.entityLiving.getCommandSenderName();
        String deathReason = String.valueOf(
            event.source.func_151519_b(event.entityLiving)
                .getUnformattedText());
        deathReason = deathReason.replaceFirst(playerName, "")
            .trim();
        if (MainConfigurations.enableDeaths) {
            if (event.entityLiving instanceof EntityPlayer) {
                createEmbed(
                    "" + playerName + " Died",
                    "**" + playerName + "** " + deathReason + "",
                    1752220,
                    playerName);
            }
        }
    }

    @SubscribeEvent
    public void onAchievementGain(AchievementEvent event) {
        String playerName = event.entityLiving.getCommandSenderName();
        String AchivementName = event.achievement.func_150951_e()
            .getUnformattedText();
        if (MainConfigurations.enableAchievements) {
            createEmbed(
                "Achievement Earned!",
                "**" + playerName + "** earned **" + AchivementName + "**",
                1752220,
                playerName);
        }
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        String username = event.username;
        String message = event.message;

        createEmbed(username, message, 1752220, username);
    }

    public static void createEmbed(String title, String description, int color, String player) {
        try {
            String thumbnail = "";
            if (player != null && !player.isEmpty()) {
                thumbnail = ",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/" + player + "\"}";
            }

            String json = "{" + "\"embeds\": [{"
                + "\"title\": \""
                + title
                + "\","
                + "\"description\": \""
                + description
                + "\","
                + "\"color\": "
                + color
                + thumbnail
                + "}]"
                + "}";

            URL url = new URL("https://discord.com/api/v10/channels/" + MainConfigurations.ChannelID + "/messages");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            String token = MainConfigurations.BotToken;
            conn.setRequestProperty("Authorization", "Bot " + token);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            byte[] out = json.getBytes(StandardCharsets.UTF_8);
            conn.setFixedLengthStreamingMode(out.length);
            conn.connect();

            try (OutputStream os = conn.getOutputStream()) {
                os.write(out);
            }

            int status = conn.getResponseCode();
            String statusMessage = conn.getResponseMessage();
            System.out.println("Channel ID: [" + MainConfigurations.ChannelID + "]");
            System.out.println("Discord Status: " + status + " " + statusMessage);

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBotStatus() {
        if (DiscordToChat.instance == null) return;

        StringBuilder status = new StringBuilder();
        int playerCount = MinecraftServer.getServer()
            .getCurrentPlayerCount();

        if (MainConfigurations.PlayerCount) {
            status.append(playerCount)
                .append(" players");
        }

        if (status.length() > 0) {
            DiscordToChat.instance.updateBotStatus(status.toString());
        }
    }
}
