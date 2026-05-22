package com.enriclop.kpopbot.discordConnection.commands;

import com.enriclop.kpopbot.discordConnection.DiscordConnection;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class HelpDiscordCommand extends DiscordCommand {
    public HelpDiscordCommand() {
        super(
                "Help",
                "help",
                "Enseña los comandos disponibles",
                true,
                false,
                0,
                0
        );
    }

    @Override
    public ApplicationCommandRequest toRequest() {
        return ApplicationCommandRequest.builder()
                .name(getCommand())
                .description(getDescription())
                .addOption(ApplicationCommandOptionData.builder()
                        .name("command")
                        .description("Comando del que quieres saber más")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(false)
                        .build()
                )
                .build();
    }

    @Override
    public void execute(DiscordConnection connection, MessageCreateEvent event) {

        /*ChatInputInteractionEvent chatEvent = (ChatInputInteractionEvent) event;
        String message = chatEvent.getOption("command")
                .flatMap(option -> option.getValue())
                .map(value -> value.asString())
                .orElse("");

         */

        String[] parts = event.getMessage().getContent().split(" ", 2);
        String message = parts.length > 1 ? parts[1] : "";

        if (!message.isEmpty()) {
            for (DiscordCommand command : connection.getCommands()) {
                if (command.getCommand().equals("/" + message) || command.getCommand().equals(message)) {
                    //event.editReply(command.getDescription() + " (Precio: " + command.getPrice() + ")").block();
                    event.getMessage().getChannel().block().createMessage(command.getDescription() + " (Precio: " + command.getPrice() + ")").block();
                    return;
                }
            }
            //event.editReply("Comando no encontrado").block();
            event.getMessage().getChannel().block().createMessage("Comando no encontrado").block();
            return;
        }

        StringBuilder commands = new StringBuilder("Comandos disponibles: ");
        for (DiscordCommand command : connection.getCommands()) {
            if (command.isActive() && !command.isModOnly()) commands.append(command.getCommand()).append(", ");
        }
        commands.delete(commands.length() - 2, commands.length());
        //event.editReply(commands.toString()).block();
        event.getMessage().getChannel().block().createMessage(commands.toString()).block();
    }
}
