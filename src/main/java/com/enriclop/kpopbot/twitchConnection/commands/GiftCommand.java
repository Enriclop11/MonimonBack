package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class GiftCommand implements Command{
    @Override
    public String getName() {
        return "Gift";
    }

    @Override
    public String getCommand() {
        return "!gift";
    }

    @Override
    public String getDescription() {
        return "Regala una carta a otro usuario";
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
    public int getPrice() {
        return 0;
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {
        User user = connection.getUserService().getUserByTwitchId(event.getUser().getId());

        if (event.getMessage().split(" ").length < 3) {
            connection.sendMessage("Elige un usuario y una carta para regalar!");
            return;
        }

        String username = event.getMessage().split(" ")[1];

        String cardIndex = event.getMessage().split(" ")[2];

        if (username.startsWith("@")) {
            username = username.substring(1);
        }

        User receiver = connection.getUserService().getUserByUsername(username);

        if (receiver == null) {
            connection.sendMessage("El usuario no existe!");
            return;
        }

        int index;

        try {
            index = Integer.parseInt(cardIndex);
        } catch (Exception e) {
            connection.sendMessage("Elige una carta para regalar!");
            return;
        }

        if (index < 1 || index > user.getPhotoCards().size()) {
            connection.sendMessage("Elige una carta para regalar!");
            return;
        }

        PhotoCard card = user.getPhotoCards().get(index - 1);

        if (card.getUser() != user) {
            connection.sendMessage("No puedes regalar una carta que no es tuya!");
            return;
        }

        card.setUser(receiver);
        connection.getCardService().saveCard(card);

        connection.sendMessage(user.getUsernameDisplay() + " ha regalado a " + receiver.getUsernameDisplay() + " la foto de " + card.getName() + "!");
    }
}
