package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class LeaderboardCommand extends Command {
    public LeaderboardCommand() {
        super(
                "Leaderboard",
                "!leaderboard",
                "Enseña el leaderboard de los usuarios con más puntos.",
                true,
                false,
                0,
                0
        );
    }
    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {
        connection.sendMessage("Leaderboard: " + connection.getSettings().getDomain() + "/leaderboard");
    }
}
