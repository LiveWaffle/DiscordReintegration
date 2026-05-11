package com.livewaffle.discordreintegration;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class MainConfigurations {

    public static File configDirectory;
    private static final String DiscordBot = "Discord Bot";
    public static String BotToken;
    public static String ChannelID;
    public static boolean enableAchievements;
    public static boolean enableDeaths;
    public static boolean enableJoinsLeaves;
    public static boolean enableDimensionMessages;
    public static boolean enablePowerFails;
    public static boolean enableExplosions;
    public static boolean enableMessageFormatting;
    public static boolean enableDiscordStatus;
    public static boolean PlayerCount;
    public static boolean DeathCount;
    public static boolean ServerUptime;
    public static Configuration configFile;

    public static void registerConfigs(File configDirectory) {
        configFile = new Configuration(new File(configDirectory, "config/discordreintegration/config.cfg"));
        configFile.load();

        // Discord Bot Setup
        BotToken = configFile.getString("BotToken", DiscordBot, "", "Discord bot token");

        ChannelID = configFile.getString("ChannelID", DiscordBot, "", "Discord channel ID");

        // Main Features
        enableJoinsLeaves = configFile
            .getBoolean("enableJoinsLeaves", "Main Features", true, "Enable Join and Leave messages");
        enableAchievements = configFile
            .getBoolean("EnableAchievements", "Main Features", true, "Enable achievement messages");
        enableDeaths = configFile.getBoolean("EnableDeaths", "Main Features", true, "Enable Death messages");
        enableDimensionMessages = configFile
            .getBoolean("EnableDimensionMessages", "Main Features", true, "Enable Dimension messages");
        enablePowerFails = configFile
            .getBoolean("EnablePowerFails", "Main Features", true, "Enable Power Fail messages");
        enableExplosions = configFile
            .getBoolean("EnableExplosions", "Main Features", true, "Enable Explosion messages");
        enableMessageFormatting = configFile
            .getBoolean("EnableMessageFormatting", "Main Features", true, "Enable Message Formatting for Discord");
        enableDiscordStatus = configFile
            .getBoolean("EnableDiscordStatus", "Main Features", true, "Enable Discord Status");
        // Discord Status
        PlayerCount = configFile.getBoolean(
            "PlayerCountStatus",
            "Status Customization",
            true,
            "If you want to display the player count on the bot :3");
        DeathCount = configFile.getBoolean(
            "DeathCountStatus",
            "Status Customization",
            false,
            "If you want to display the death count on the bot :3");
        ServerUptime = configFile
            .getBoolean("ServerUptimeStatus", "Status Customization", false, "Display the server uptime on the BOT");

        if (configFile.hasChanged()) {
            configFile.save();
        }
    }
}
