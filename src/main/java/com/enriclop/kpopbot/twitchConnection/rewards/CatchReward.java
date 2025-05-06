package com.enriclop.kpopbot.twitchConnection.rewards;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import static com.enriclop.kpopbot.enums.Pokeballs.POKEBALL;

public class CatchReward extends Reward {

    public CatchReward() {
        super(
                "Catch",
                "catch",
                true,
                false,
                0
        );
    }

    @Override
    public void execute(TwitchConnection connection, RewardRedeemedEvent event) {
        connection.catchPokemon(event.getRedemption().getUser().getId(), POKEBALL);
    }
}
