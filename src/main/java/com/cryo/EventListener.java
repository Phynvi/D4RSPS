package com.cryo;

import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class EventListener {

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().getIdLong() == DiscordBot.getInstance().getId())
            return;
        switch (event.getChannel().getType()) {
            case TEXT:
                if (DiscordBot.getInstance().getCommandManager().processCommand(event.getMessage())) return;
                Links.handleDiscordMessage(event.getMessage());
                break;
            case PRIVATE:
                DiscordBot.getInstance().getDialogueManager().continueConversation((PrivateChannel) event.getChannel(), event.getMessage(), event.getAuthor().getIdLong(), event.getMessage().getContentRaw());
                break;
        }
    }

    @SubscribeEvent
    public void onRoleDeleted(RoleDeleteEvent event) {
        DiscordBot.getInstance().getRoleManager().roleDeleted(event.getRole().getIdLong());
    }
}
