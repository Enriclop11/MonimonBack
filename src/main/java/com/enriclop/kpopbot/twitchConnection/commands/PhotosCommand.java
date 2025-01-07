package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class PhotosCommand implements Command {
    @Override
    public String getName() {
        return "Photos";
    }

    @Override
    public String getCommand() {
        return "!photos";
    }

    @Override
    public String getDescription() {
        return "Enseña las photocard que tienes";
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
        connection.sendMessage("Tus photocards: " + connection.getSettings().getDomain() + "/photocards/" + event.getUser().getName().toLowerCase());
    }


}
