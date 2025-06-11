package com.enriclop.kpopbot.twitchConnection.events.announcements;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.enriclop.kpopbot.twitchConnection.events.Event;

public class AnnouncementEvent extends Event {

    private final String message;

    public AnnouncementEvent(String announcementName, boolean active, int minCooldown, int maxCooldown, String message) {
        super(
                announcementName,
                active,
                minCooldown,
                maxCooldown
        );

        this.message = message;
    }

    @Override
    protected void execute(TwitchConnection connection) {
        connection.sendMessage(message);
    }
}
