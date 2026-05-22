package com.enriclop.kpopbot.twitchConnection.rewards;

import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.helix.domain.CustomReward;
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

    private int price;

    private boolean active;

    private boolean modOnly;

    private int cooldown;

    public void execute(TwitchConnection connection, RewardRedeemedEvent event) {
        connection.sendMessage("Reward not implemented");
    }

    public void createReward(TwitchConnection connection) {
        CustomReward newReward = CustomReward.builder()
                .title(reward)
                .isEnabled(active)
                .isUserInputRequired(false)
                .cost(price)
                .build();

        connection.getTwitchClient().getHelix().createCustomReward(
                connection.getSettings().getTokenChannel(),
                connection.getChannel().getId(),
                newReward
        ).execute();
    }

    public void deleteReward(TwitchConnection connection) {
        connection.getTwitchClient().getHelix().getCustomRewards(
                connection.getSettings().getTokenChannel(),
                connection.getChannel().getId(),
                null,
                true
        ).execute().getRewards().stream()
                .filter(customReward -> customReward.getTitle().equals(reward))
                .findFirst()
                .ifPresent(customReward -> connection.getTwitchClient().getHelix().deleteCustomReward(
                        connection.getSettings().getTokenChannel(),
                        connection.getChannel().getId(),
                        customReward.getId()
                ).execute());

    }

    public void editReward(boolean active, TwitchConnection connection) {
        deleteReward(connection);
        if (active) {
            createReward(connection);
        }
    }

    public void returnReedemption(TwitchConnection connection, RewardRedeemedEvent event) {
        connection.returnRedemption(event.getRedemption());
    }
}
