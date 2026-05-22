package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.dto.CustomPhotoDTO;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class CustomCardCommand extends Command{

    public CustomCardCommand() {
        super(
                "GiveCustomCard",
                "!give",
                "Da una carta custom [!give {usuario} {carta}] [!give me {carta}]",
                true,
                true,
                0,
                0
        );
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {
        if (event.getMessage().split(" ").length < 3) {
            connection.sendMessage("Elige un usuario y una carta para regalar!");
            return;
        }

        String username = event.getMessage().split(" ")[1];

        if (username.startsWith("@")) {
            username = username.substring(1);
        }

        username = username.toLowerCase();

        User user;
        if (username.equals("me")) {
            user = connection.getUserService().getUserByTwitchId(event.getUser().getId());
        } else {
            user = connection.getUserService().getUserByUsername(username);
        }

        String cardId = event.getMessage().split(" ")[2];

        CustomPhotoDTO pabloCard = connection.kpopService.getCustomCard(cardId);

        if (pabloCard == null) {
            connection.sendMessage("La carta no existe.");
            return;
        }

        PhotoCard customCard = new PhotoCard(pabloCard);
        customCard.setUser(user);
        connection.getCardService().saveCard(customCard);

        connection.sendMessage( user.getUsernameDisplay() + " ha recibido " + customCard.getDisplayName() );
    }
}
