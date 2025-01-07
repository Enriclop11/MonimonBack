package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class SelectCommand implements Command{
    @Override
    public String getName() {
        return "Select Cards";
    }

    @Override
    public String getCommand() {
        return "!select";
    }

    @Override
    public String getDescription() {
        return "Selecciona una carta para tu equipo";
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
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {

        User user = connection.getUserService().getUserByTwitchId(event.getUser().getId());

        if (event.getMessage().split(" ").length < 2) {
            connection.sendMessage("Elige una carta para seleccionar!");
            return;
        }

        String cardPos = event.getMessage().split(" ")[1];

        int pos;

        try {
            pos = Integer.parseInt(cardPos);
        } catch (Exception e) {
            connection.sendMessage("Elige una carta para seleccionar!");
            return;
        }

        if (pos < 1 || pos > user.getPhotoCards().size()) {
            connection.sendMessage("Elige una carta para seleccionar!");
            return;
        }

        PhotoCard photoCard = user.getPhotoCards().get(pos - 1);

        if (user.getSelectedCards().contains(photoCard.getId())) {
            connection.sendMessage("Ya has seleccionado a " + photoCard.getName() + "!");
            return;
        }

        if (user.getSelectedCards().size() >= 3) {
            user.getSelectedCards().remove(0);
        }

        user.getSelectedCards().add(photoCard.getId());

        connection.getUserService().saveUser(user);

        connection.sendMessage(user.getUsernameDisplay() + " ha seleccionado a " + photoCard.getName() + "!");
    }
}
