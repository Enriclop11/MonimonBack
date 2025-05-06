package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Command {

    private String name = "Command";

    private String command = "!command";

    private String description = "Comando de ejemplo";

    private boolean active = false;

    private boolean modOnly = false;

    private int price = 0;

    private int cooldown = 0;

    public void execute(TwitchConnection connection, ChannelMessageEvent event) {
        connection.sendMessage("Comando no implementado");
    }

}
