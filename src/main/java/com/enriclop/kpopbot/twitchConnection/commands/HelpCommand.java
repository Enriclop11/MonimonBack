package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class HelpCommand extends Command {

    public HelpCommand() {
        super(
                "Help",
                "!help",
                "Enseña los comandos disponibles",
                true,
                false,
                0,
                0
        );
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {

        String[] message = event.getMessage().split(" ");

        if (message.length > 1) {
            for (Command command : connection.getCommands()) {
                if (command.getCommand().equals("!" + message[1])) {
                    connection.sendMessage(command.getDescription() + " (Precio: " + command.getPrice() + ")");
                    return;
                }
            }
            connection.sendMessage("Comando no encontrado");
            return;
        }

        StringBuilder commands = new StringBuilder("Comandos disponibles: ");
        for (Command command : connection.getCommands()) {
            if (command.isActive() && !command.isModOnly()) commands.append(command.getCommand()).append(", ");
        }
        commands.delete(commands.length() - 2, commands.length());
        connection.sendMessage(commands.toString());
    }
}
