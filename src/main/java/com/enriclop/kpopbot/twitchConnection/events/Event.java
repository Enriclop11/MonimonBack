package com.enriclop.kpopbot.twitchConnection.events;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private String name = "Event";

    private boolean active = false;

    private int minCooldown = 0;

    private int maxCooldown = 0;

    @JsonIgnore
    private ScheduledExecutorService scheduler;

    public Event(String name, boolean active, int minCooldown, int maxCooldown) {
        this.name = name;
        this.active = active;
        this.minCooldown = minCooldown;
        this.maxCooldown = maxCooldown;
    }

    protected void execute(TwitchConnection connection) {
        connection.sendMessage("Event triggered: " + name);
    }

    public void run(TwitchConnection connection) {
        if (minCooldown == 0 && maxCooldown == 0) {
            active = false;
            return;
        }

        log.info("Starting event: " + name);

        scheduler = Executors.newSingleThreadScheduledExecutor();

        active = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleWithFixedDelay(() -> {
            if (active) {
                execute(connection);
            }
        }, getRandomCooldown(), getRandomCooldown(), TimeUnit.MINUTES);
    }

    public void stop() {
        active = false;
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    private int getRandomCooldown() {
        return (int) (Math.random() * (maxCooldown - minCooldown)) + minCooldown;
    }
}