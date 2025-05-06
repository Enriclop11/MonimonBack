package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class WatchtimeCommand extends Command {

    public WatchtimeCommand() {
        super(
                "Watchtime",
                "!watchtime",
                "Muestra el tiempo que llevas viendo el stream",
                true,
                false,
                0,
                0
        );
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {

        User user = connection.getUserService().getUserByTwitchId(event.getUser().getId());

        if (user.getWatchTime().getDays() > 0) {
            connection.sendMessage(event.getUser().getName() + "ha pasado " + (int) user.getWatchTime().getDays() + " dias viendo el stream!");
        } else if (user.getWatchTime().getHours() > 0) {
            connection.sendMessage(event.getUser().getName() + "ha pasado " + (int) user.getWatchTime().getHours() + " horas viendo el stream!");
        } else {
            connection.sendMessage(event.getUser().getName() + "ha pasado " + (int) user.getWatchTime().getMinutes() + " minutos viendo el stream!");
        }
    }
}
