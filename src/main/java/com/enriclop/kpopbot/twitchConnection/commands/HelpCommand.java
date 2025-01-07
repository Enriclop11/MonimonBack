package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class HelpCommand implements Command{

    @Override
    public String getName() {
        return "Help";
    }

    @Override
    public String getCommand() {
        return "!help";
    }

    @Override
    public String getDescription() {
        return "Enseña los comandos disponibles";
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean isModOnly() {
        return false;
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {

        String[] message = event.getMessage().split(" ");

        if (message.length > 1) {
            for (Command command : connection.getCommands()) {
                if (command.getCommand().equals("!" + message[1])) {
                    connection.sendMessage(command.getDescription());
                    return;
                }
            }
            connection.sendMessage("Comando no encontrado");
            return;
        }

        StringBuilder commands = new StringBuilder("Comandos disponibles: ");
        for (Command command : connection.getCommands()) {
            commands.append(command.getCommand()).append(", ");
        }
        commands.delete(commands.length() - 2, commands.length());
        connection.sendMessage(commands.toString());
    }
}
