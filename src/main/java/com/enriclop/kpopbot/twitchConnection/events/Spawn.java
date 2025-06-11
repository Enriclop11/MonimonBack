package com.enriclop.kpopbot.twitchConnection.events;

import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Spawn extends Event {

    PhotoCard wildCard;

    public Spawn() {
        super(
                "Spawn",
                false,
                5,
                30);
    }

    @Override
    protected void execute(TwitchConnection connection) {
            wildCard = connection.spawnPhoto();
            log.info("Card spawned: " + wildCard.getName());
    }

}
