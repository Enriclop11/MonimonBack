package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class PointsCommand extends Command {

    public PointsCommand() {
        super(
                "Points",
                "!points",
                "Muestra tus puntos",
                true,
                false,
                0,
                0
        );
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {
        User user = connection.getUserService().getUserByTwitchId(event.getUser().getId());
        connection.sendMessage("Tienes " + user.getScore() + " puntos!");
    }
}
