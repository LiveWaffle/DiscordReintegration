package com.livewaffle.discordreintegration;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@ClientEndpoint
public class DiscordToChat {

    private Session session;
    private Timer heartbeatTimer;
    public static DiscordToChat instance;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        DiscordToChat.instance = this;
        System.out.println("Discord gateway connection opened");
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        try {
            JsonObject json = new JsonParser().parse(message)
                .getAsJsonObject();
            int op = json.get("op")
                .getAsInt();

            if (op == 10) {
                int interval = json.get("d")
                    .getAsJsonObject()
                    .get("heartbeat_interval")
                    .getAsInt();
                startHeartbeat(interval);
                identify();
            } else if (op == 0) {
                String eventType = json.has("t") ? json.get("t")
                    .getAsString() : null;

                if ("MESSAGE_CREATE".equals(eventType)) {
                    JsonObject data = json.get("d")
                        .getAsJsonObject();
                    String channelId = data.get("channel_id")
                        .getAsString();

                    if (!channelId.equals(MainConfigurations.ChannelID)) return;

                    JsonObject author = data.get("author")
                        .getAsJsonObject();
                    boolean isBot = author.has("bot") && author.get("bot")
                        .getAsBoolean();

                    if (isBot) return;

                    String username = author.get("username")
                        .getAsString();
                    String content = data.has("content") ? data.get("content")
                        .getAsString() : "";

                    if (content.isEmpty() && data.has("embeds")) {
                        content = "[Embed]";
                    }
                    if (content.isEmpty() && data.has("sticker_items")) {
                        content = "[Sticker]";
                    }
                    if (content.isEmpty() && data.has("attachments")) {
                        content = "[File]";
                    }

                    if (!content.isEmpty()) {
                        String formattedMessage = EnumChatFormatting.BLUE.toString()
                            + EnumChatFormatting.BOLD.toString()
                            + "[DISCORD] "
                            + EnumChatFormatting.RESET.toString()
                            + EnumChatFormatting.BLUE.toString()
                            + "§f"
                            + username
                            + ": "
                            + content;
                        MinecraftServer.getServer()
                            .getConfigurationManager()
                            .sendChatMsg(new ChatComponentText(formattedMessage));
                    }
                }
            }
        } catch (Exception e) {}
    }

    private void identify() throws IOException {
        JsonObject payload = new JsonObject();
        payload.addProperty("op", 2);
        JsonObject data = new JsonObject();
        data.addProperty("token", MainConfigurations.BotToken);
        JsonObject props = new JsonObject();
        props.addProperty("$os", "windows");
        props.addProperty("$browser", "minecraft");
        props.addProperty("$device", "minecraft");
        data.add("properties", props);
        data.addProperty("intents", 33281);
        payload.add("d", data);
        session.getBasicRemote()
            .sendText(payload.toString());
    }

    public void updateBotStatus(String statusText) {
        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("op", 3);
            JsonObject data = new JsonObject();
            data.addProperty("since", System.currentTimeMillis());
            data.addProperty("afk", false);

            JsonObject activity = new JsonObject();
            activity.addProperty("name", statusText);
            activity.addProperty("type", 0);

            com.google.gson.JsonArray activities = new com.google.gson.JsonArray();
            activities.add(activity);
            data.add("activities", activities);

            payload.add("d", data);
            if (session != null && session.isOpen()) {
                session.getBasicRemote()
                    .sendText(payload.toString());
            }
        } catch (Exception e) {}
    }

    private void startHeartbeat(int interval) {
        heartbeatTimer = new Timer();
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    JsonObject hb = new JsonObject();
                    hb.addProperty("op", 1);
                    hb.add("d", JsonNull.INSTANCE);
                    session.getBasicRemote()
                        .sendText(hb.toString());
                } catch (Exception e) {}
            }
        }, 0, interval);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        if (heartbeatTimer != null) heartbeatTimer.cancel();
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }
}
