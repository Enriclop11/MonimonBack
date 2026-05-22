package com.enriclop.kpopbot.discordConnection.commands;

import com.enriclop.kpopbot.discordConnection.DiscordConnection;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DiscordCommand {

    private String name = "Command";

    private String command = "command";

    private String description = "Comando de ejemplo";

    private boolean active = false;

    private boolean modOnly = false;

    private int price = 0;

    private int cooldown = 0;

    public ApplicationCommandRequest toRequest() {
        return ApplicationCommandRequest.builder()
                .name(command)
                .description(description)
                .build();
    }

    public void execute(DiscordConnection connection, MessageCreateEvent event) {
        ///event.editReply("Comando no implementado").block();
        event.getMessage().getChannel().block().createMessage("Comando no implementado").block();
    }

    public void setActive(boolean active, DiscordConnection connection) {

        if (active == this.active) {
            return;
        }

        this.active = active;

        if (active) {
            connection.registerCommand(this);
        } else {
            connection.deleteCommand(this);
        }

    }
}
