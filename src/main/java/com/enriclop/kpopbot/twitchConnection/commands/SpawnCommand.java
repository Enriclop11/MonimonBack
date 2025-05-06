package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class SpawnCommand extends Command {

    public SpawnCommand() {
        super(
                "Spawn",
                "!spawn",
                "Spawnea una foto de un idol",
                true,
                true,
                0,
                0
        );
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {
        connection.spawnPhoto();
    }


}
