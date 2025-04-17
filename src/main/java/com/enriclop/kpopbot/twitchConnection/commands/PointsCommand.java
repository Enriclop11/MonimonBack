package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class PointsCommand implements Command{
    @Override
    public String getName() {
        return "Points";
    }

    @Override
    public String getCommand() {
        return "!points";
    }

    @Override
    public String getDescription() {
        return "Muestra tus puntos";
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
    public int getPrice() {
        return 0;
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {
        User user = connection.getUserService().getUserByTwitchId(event.getUser().getId());
        connection.sendMessage("Tienes " + user.getScore() + " puntos!");
    }
}
