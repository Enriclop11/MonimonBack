package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class LeaderboardCommand implements Command {

    @Override
    public String getName() {
        return "Leaderboard";
    }

    @Override
    public String getCommand() {
        return "!leaderboard";
    }

    @Override
    public String getDescription() {
        return "Enseña el leaderboard de los usuarios con más puntos.";
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
        connection.sendMessage("Leaderboard: " + connection.getSettings().getDomain() + "/leaderboard");
    }
}
