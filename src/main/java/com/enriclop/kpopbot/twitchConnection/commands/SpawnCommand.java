package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class SpawnCommand implements Command {
    @Override
    public String getName() {
        return "Spawn";
    }

    @Override
    public String getCommand() {
        return "!spawn";
    }

    @Override
    public String getDescription() {
        return "Spawnea una foto de un idol";
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean isModOnly() {
        return true;
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {
        connection.spawnPhoto();
    }


}
