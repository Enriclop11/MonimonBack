package com.enriclop.kpopbot.twitchConnection.rewards;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import static com.enriclop.kpopbot.enums.Pokeballs.MASTERBALL;

public class SuperCatchReward extends Reward{

    public SuperCatchReward() {
        super(
                "SuperCatch",
                "SuperCatch",
                1000,
                false,
                false,
                0
        );
    }

    @Override
    public void execute(TwitchConnection connection, RewardRedeemedEvent event) {
        if(!connection.catchPokemon(event.getRedemption().getUser().getId(), MASTERBALL)) {
            returnReedemption(connection, event);
        }
    }
}
