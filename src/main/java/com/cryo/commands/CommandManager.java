package com.cryo.commands;

import com.cryo.DiscordBot;
import com.cryo.db.impl.AccountConnection;
import com.cryo.entities.Command;
import com.cryo.utils.Utilities;
import net.dv8tion.jda.core.entities.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Stream;

public class CommandManager {

    private HashMap<String, Command> commands;

    public void loadCommands() {
        try {
            commands = new HashMap<>();
            for (Class<?> c : Utilities.getClasses("com.cryo.commands.impl")) {
                if (!Command.class.isAssignableFrom(c)) continue;
                Object o = c.newInstance();
                if (!(o instanceof Command)) continue;
                Command command = (Command) o;
                Stream.of(command.getAliases()).forEach(c2 -> commands.put(c2, command));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public boolean processCommand(Message message) {
        String prefix = DiscordBot.getInstance().getProperties().getProperty("prefix");
        if (!message.getContentRaw().startsWith(prefix)) return false;
        String command = message.getContentRaw();
        command = command.replace(prefix, "");
        String[] cmd = command.split(" ");
        String opcode = cmd[0];
        if (commands.containsKey(opcode)) {
            Command commandObj = commands.get(opcode);
            int rights = AccountConnection.getRights(message.getAuthor().getIdLong());
            if (commandObj.getPermissionsReq(command) > rights) return false;
            commandObj.handleCommand(message, command, cmd);
            return true;
        }
        message.getChannel().sendMessage("Unable to recognize that command.");
        return false;
    }
}
