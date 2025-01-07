package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class RefreshUsernameCommand implements Command {
    @Override
    public String getName() {
        return "RefreshUsername";
    }

    @Override
    public String getCommand() {
        return "!refreshUsername";
    }

    @Override
    public String getDescription() {
        return "Refresca el nombre de usuario en la base de datos";
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
