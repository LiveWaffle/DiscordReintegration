package com.livewaffle.discordreintegration;

import static com.livewaffle.discordreintegration.MainConfigurations.BotToken;
import static com.livewaffle.discordreintegration.MainConfigurations.ChannelID;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class SetupCommands extends CommandBase {

    @Override
    public String getCommandName() {
        return "discord";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "discord";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText("Usage: /discord <set|reload|channel> <token|channel_id>"));
            return;
        }
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("Usage: /discord <set|reload|channel> <token|channel_id>"));
            return;
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (!args[1].isEmpty()) {
                BotToken = args[1];
                MainConfigurations.configFile.get("Discord Bot", "BotToken", "")
                    .set(args[1]);

                MainConfigurations.configFile.save();
                sender.addChatMessage(new ChatComponentText("§a Token Saved!"));
                if (args[1].length() < 50) {
                    sender.addChatMessage(new ChatComponentText("§c Invalid Discord Token"));
                    return;
                }
            }

        } ;
        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("channel")) {
                if (args[1].equalsIgnoreCase("set")) {
                    if (!args[2].isEmpty()) {
                        ChannelID = args[2];
                        MainConfigurations.configFile.get("Discord Bot", "ChannelID", "")
                            .set(args[2]);
                    }
                    MainConfigurations.configFile.save();
                    sender.addChatMessage(new ChatComponentText("§a Channel ID Saved!"));
                    if (args[2].length() < 15) {
                        sender.addChatMessage(new ChatComponentText("§c Invalid Channel ID"));
                    }
                }
            }
        } else {
            sender.addChatMessage(new ChatComponentText("§c Usage: /discord channel set <ChannelID>"));
        }
    } // god this is such a fucking headache
      // do ChatToDiscord
      // Websockets? for discord to chat

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            return getListOfStringsMatchingLastWord(args, "discord");
        }
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "set", "channel");
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "set");
        }
        return null;
    }
}
