package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class RefreshUsernameCommand extends Command {

    public RefreshUsernameCommand() {
        super(
                "RefreshUsername",
                "!refreshUsername",
                "Refresca el nombre de usuario en la base de datos",
                true,
                false,
                0,
                0
        );
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {
        try {
            User user = connection.getUserService().getUserByTwitchId(event.getChannel().getId());
            if (user != null && !user.getUsername().equals(event.getUser().getName().toLowerCase())) {
                user.setUsername(event.getUser().getName().toLowerCase());
                connection.getUserService().saveUser(user);
            }
        } catch (Exception e) {
            connection.start(event.getUser().getId());
        }
    }
}
