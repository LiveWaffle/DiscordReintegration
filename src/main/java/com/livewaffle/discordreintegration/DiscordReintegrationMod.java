package com.livewaffle.discordreintegration;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

@Mod(
    modid = DiscordReintegrationMod.MODID,
    name = "Discord Reintegration",
    acceptedMinecraftVersions = "[1.7.10]",
    canBeDeactivated = true)
public class DiscordReintegrationMod {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MainConfigurations.registerConfigs(MainConfigurations.configDirectory);
    }

    public static final String MODID = "discordreintegration";

    @Mod.EventHandler
    public void init(FMLServerStartingEvent event) {
        event.registerServerCommand(new SetupCommands());
        MinecraftForge.EVENT_BUS.register(new ChatToDiscord());
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartedEvent event) {
        ChatToDiscord.createEmbed("Server is Started!", "", 5763719, "");
        connectDiscordGateway();
    }

    private void connectDiscordGateway() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container
                .connectToServer(new DiscordToChat(), new java.net.URI("wss://gateway.discord.gg/?v=10&encoding=json"));
        } catch (Exception e) {}
    }

    @Mod.EventHandler
    public void onServerClose(FMLServerStoppingEvent event) {
        ChatToDiscord.createEmbed("Server is Stopping!", "", 16711735, "");
    }
}
