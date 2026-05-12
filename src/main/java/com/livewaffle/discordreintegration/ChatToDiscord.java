package com.livewaffle.discordreintegration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import net.minecraft.client.main.Main;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import scala.Console;

import static java.lang.Math.round;

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
    public void onExplosion(ExplosionEvent.Start event) {
        if (MainConfigurations.enableExplosions) {
            Explosion explosion = event.explosion;
            if (explosion.exploder != null) {
                String causedBy = explosion.exploder.getClass().getName();
                createEmbed(
                    "Explosion Detected! \uD83E\uDDE8",
                    "There was an Explosion caused by " + causedBy +
                        "\\n **" +
                        round(explosion.explosionX) + " " +
                        round(explosion.explosionY) + " " +
                        round(explosion.explosionZ) + "**",
                    1752220,
                    "5573d7046d6e08198390aa56c8f8678c16d4407af9f214bf0291f3c7db1f379a"
                );
            }
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

            byte[] out = json.getBytes(StandardCharsets.UTF_8);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bot " + MainConfigurations.BotToken);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Content-Length", String.valueOf(out.length));
            conn.setRequestProperty("User-Agent", "DiscordReintegration/1.0");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(out);
            }

            int status = conn.getResponseCode();
            String statusMessage = conn.getResponseMessage();
            System.out.println("Channel ID: [" + MainConfigurations.ChannelID + "]");
            System.out.println("Discord Status: " + status + " " + statusMessage);

            if (status >= 400) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder errorBody = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    errorBody.append(line);
                }
                System.out.println("Discord Error Response: " + errorBody.toString());
                reader.close();
            }

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
