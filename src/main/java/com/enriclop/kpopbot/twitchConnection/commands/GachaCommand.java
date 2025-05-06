package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class GachaCommand extends Command {

    public GachaCommand() {
        super(
            "Gacha",
            "!gacha",
            "Haz una tirada en el gacha",
            true,
            false,
            250,
            0
        );
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {

        User user = connection.getUserService().getUserByTwitchId(event.getUser().getId());

        PhotoCard randomCard =  connection.getKpopPhotos().generateRandomPhotocard();

        if (randomCard == null) {
            connection.sendMessage("No se ha podido generar una carta");
            return;
        }

        user = connection.getUserService().getUserById(user.getId());

        randomCard.setUser(user);

        connection.getCardService().saveCard(randomCard);

        connection.sendMessage( user.getUsernameDisplay() + " te ha tocado una carta de " + randomCard.getName() + " (" + randomCard.getBand() + ")\n");
    }
}
