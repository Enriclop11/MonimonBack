package com.enriclop.kpopbot.twitchConnection.rewards;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import static com.enriclop.kpopbot.enums.Pokeballs.POKEBALL;

public class CatchReward implements Reward {
    @Override
    public String getName() {
        return "Catch";
    }

    @Override
    public String getReward() {
        return "catch";
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
    public int getCooldown() {
        return 0;
    }

    @Override
    public void execute(TwitchConnection connection, RewardRedeemedEvent event) {
        connection.catchPokemon(event.getRedemption().getUser().getId(), POKEBALL);
    }
}
