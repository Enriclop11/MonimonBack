package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public interface Command {

    public String getName();

    public String getCommand();

    public String getDescription();

    public boolean isActive();

    public boolean isModOnly();

    public void execute(TwitchConnection connection, ChannelMessageEvent event);

}
