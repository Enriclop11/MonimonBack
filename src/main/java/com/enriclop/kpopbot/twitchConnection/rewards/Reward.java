package com.enriclop.kpopbot.twitchConnection.rewards;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reward {

    private String name;

    private String reward;

    private boolean active;

    private boolean modOnly;

    private int cooldown;

    public void execute(TwitchConnection connection, RewardRedeemedEvent event) {
        connection.sendMessage("Reward not implemented");
    }
}
