package com.enriclop.kpopbot.twitchConnection.rewards;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

public interface Reward {
    public String getName();

    public String getReward();

    public boolean isActive();

    public boolean isModOnly();

    public int getCooldown();

    public void execute(TwitchConnection connection, RewardRedeemedEvent event);
}
