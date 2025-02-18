package com.cryo.commands.impl;

import com.cryo.DiscordBot;
import com.cryo.entities.Command;
import com.cryo.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class UtilityCommands implements Command {

    @Override
    public int getPermissionsReq(String command) {
        switch (command) {
            case "purge":
            case "guild-id":
            case "my-id":
            case "channel-id":
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"purge", "guild-id", "my-id", "channel-id", "guess"};
    }

    @Override
    public void handleCommand(Message message, String command, String[] cmd) {
        switch (cmd[0]) {
            case "purge":
                if (cmd.length == 1) {
                    message.getChannel().sendMessage("Incorrect syntax. Correct usage: .purge (num)").queue();
                    return;
                }
                long channelId = message.getChannel().getIdLong();
                TextChannel channel = DiscordBot.getInstance().getJda().getTextChannelById(channelId);
                if (cmd[1].equals("all")) {
                    while (true) {
                        List<Message> messages = channel.getHistory().retrievePast(50).complete();
                        OffsetDateTime twoWeeksAgo = OffsetDateTime.now().minus(2, ChronoUnit.WEEKS);
                        messages.removeIf(m -> m.getCreationTime().isBefore(twoWeeksAgo));
                        if (messages.isEmpty() || messages.size() < 2) break;
                        channel.deleteMessages(messages).complete();
                    }
                } else {
                    int length;
                    try {
                        length = Integer.parseInt(cmd[1]) + 1;
                    } catch (Exception e) {
                        message.getChannel().sendMessage("Incorrect syntax. Correct usage: .purge (num)").queue();
                        return;
                    }
                    channel.getHistory().retrievePast(length).queue(l -> l.forEach(m -> m.delete().queue()));
                }
                break;
            case "guess":
                Game game = DiscordBot.getInstance().getGameManager().getCurrentGame();
                if (game == null) break;
                game.processGuessCommand(message, command, cmd);
                break;
            case "my-id":
                message.getChannel().sendMessage("Your Discord ID: " + message.getAuthor().getIdLong()).queue();
                break;
            case "channel-id":
                message.getChannel().sendMessage("This channel's ID is " + message.getChannel().getIdLong()).queue();
                break;
            case "guild-id":
                message.getChannel().sendMessage("Guild ID: " + message.getGuild().getIdLong()).queue();
                break;
        }
    }
}
