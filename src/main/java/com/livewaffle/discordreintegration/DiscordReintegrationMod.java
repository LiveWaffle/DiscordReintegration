package com.livewaffle.discordreintegration;

import net.minecraftforge.common.MinecraftForge;

import org.glassfish.tyrus.client.ClientManager;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

@Mod(
    modid = DiscordReintegrationMod.MODID,
    name = "Discord Reintegration",
    acceptedMinecraftVersions = "[1.7.10]",
    canBeDeactivated = true,
    acceptableRemoteVersions = "*")
public class DiscordReintegrationMod {

    public static final String Reset = "\u001B[0m";
    public static final String Green = "\u001B[38;5;155m";
    public static final String Red = "\u001B[38;5;196m";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        String banner = "\n█████████████████████████\n" + "█                       █\n"
            + "█ DISCORD REINTEGRATION █\n"
            + "█                       █\n"
            + "█████████████████████████";

        FMLLog.info("%s", Green + banner + Reset);

        MainConfigurations.registerConfigs(MainConfigurations.configDirectory);
        if (MainConfigurations.BotToken.isEmpty() && MainConfigurations.ChannelID.isEmpty()) {
            String warning = "███████████████████████████████████████████████████\n"
                + "█                                                 █\n"
                + "█     Please set your bot token & Channel ID!     █\n"
                + "█                                                 █\n"
                + "███████████████████████████████████████████████████\n"
                + "Config is located in /config/discordreintegration.cfg\n"
                + "Or use the command:\n"
                + "/discord set <token>\n"
                + "/discord channel set <id>";

            FMLLog.warning("\n%s", Green + warning + Reset);
        }
    }

    public static final String MODID = "discordreintegration";

    @Mod.EventHandler
    public void init(FMLServerStartingEvent event) {
        event.registerServerCommand(new SetupCommands());
        MinecraftForge.EVENT_BUS.register(new ChatToDiscord());
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartedEvent event) {
        if (!MainConfigurations.BotToken.isEmpty() && MainConfigurations.ChannelID.isEmpty()) {
            ChatToDiscord.createEmbed("Server is Started!", "", 5763719, "");
            connectDiscordGateway();
        } else {
            FMLLog.info(Red + """
                \n
                +------------------------------+
                | Bot Token & ChannelID Not set|
                +------------------------------+
                """);
        }

    }

    private void connectDiscordGateway() {
        try {
            ClientManager.createClient()
                .connectToServer(new DiscordToChat(), new java.net.URI("wss://gateway.discord.gg/?v=10&encoding=json"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Mod.EventHandler
    public void onServerClose(FMLServerStoppingEvent event) {
        ChatToDiscord.createEmbed("Server is Stopping!", "", 16711735, "");
        DiscordToChat.instance.closeSessions();
    }
}
