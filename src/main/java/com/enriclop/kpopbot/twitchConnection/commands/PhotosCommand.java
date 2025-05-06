package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class PhotosCommand extends Command {

    public PhotosCommand() {
        super(
                "Photos",
                "!photos",
                "Enseña las photocard que tienes",
                true,
                false,
                0,
                0
        );
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {
        connection.sendMessage("Tus photocards: " + connection.getSettings().getDomain() + "/photocards/" + event.getUser().getName().toLowerCase());
    }


}
