package com.livewaffle.discordreintegration;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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

            CloseableHttpClient client = HttpClients.createDefault();

            HttpPost post = new HttpPost(
                "https://discord.com/api/v10/channels/" + MainConfigurations.ChannelID + "/messages");

            post.setHeader("Authorization", "Bot " + MainConfigurations.BotToken);

            post.setHeader("Content-Type", "application/json");

            post.setEntity(new StringEntity(json));

            CloseableHttpResponse response = client.execute(post);
            System.out.println("Channel ID: [" + MainConfigurations.ChannelID + "]");
            System.out.println("Discord Status: " + response.getStatusLine());

            response.close();
            client.close();

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
