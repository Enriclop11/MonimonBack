package com.enriclop.kpopbot.twitchConnection.events.announcements;

public class DiscordAnnouncement extends AnnouncementEvent {

    private static final String MESSAGE = "nayeonNerd Deja sugerencias sobre el bot → !discord nayeonNerd";

    public DiscordAnnouncement() {
        super(
                "Discord Announcement",
                true,
                30,
                60,
                MESSAGE
        );
    }
}
