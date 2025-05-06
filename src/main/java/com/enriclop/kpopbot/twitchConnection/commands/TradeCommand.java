package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.servicio.UserService;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.enriclop.kpopbot.twitchConnection.threads.Trade;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class TradeCommand extends Command {

    public TradeCommand() {
        super(
                "Trade",
                "!trade",
                "Intercambia una carta con otro usuario",
                true,
                false,
                0,
                0
        );
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {

        UserService userService = connection.getUserService();
        User user = userService.getUserByTwitchId(event.getUser().getId());

        if (event.getMessage().split(" ").length < 3) {
            connection.sendMessage("Elige un usuario y una carta para intercambiar!");
            return;
        }

        String username = event.getMessage().split(" ")[1];
        String photocardIndex = event.getMessage().split(" ")[2];

        if (username.startsWith("@")) {
            username = username.substring(1);
        }

        User receiver = userService.getUserByUsername(username);

        if (receiver == null) {
            connection.sendMessage("El usuario no existe!");
            return;
        }

        int index;

        try {
            index = Integer.parseInt(photocardIndex);
        } catch (Exception e) {
            connection.sendMessage("Elige una foto para intercambiar!");
            return;
        }

        if (index < 1 || index > user.getPhotoCards().size()) {
            connection.sendMessage("Elige una foto para intercambiar!");
            return;
        }

        PhotoCard photocard = user.getPhotoCards().get(index - 1);

        if (photocard.getUser() != user) {
            connection.sendMessage("No puedes intercambiar una foto que no es tuya!");
            return;
        }

        if (connection.getCurrentTrade() != null) {
            connection.getCurrentTrade().stopTrade();
        }

        connection.setCurrentTrade(new Trade(user, receiver, photocard, userService, connection.getCardService(), connection.getTwitchClient(), connection));
        connection.getCurrentTrade().start();
    }
}
