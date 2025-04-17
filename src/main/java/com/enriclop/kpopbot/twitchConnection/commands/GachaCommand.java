package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class GachaCommand implements Command {

    @Override
    public String getName() {
        return "Gacha";
    }

    @Override
    public String getCommand() {
        return "!gacha";
    }

    @Override
    public String getDescription() {
        return "Haz una tirada en el gacha";
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
        return 100;
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {

        User user = connection.getUserService().getUserByTwitchId(event.getUser().getId());

        PhotoCard randomCard =  connection.getKpopPhotos().generateRandomPhotocard();

        if (randomCard == null) {
            connection.sendMessage("No se ha podido generar una carta");
            return;
        }

        randomCard.setUser(user);

        connection.getCardService().saveCard(randomCard);

        connection.sendMessage( user.getUsernameDisplay() + " te ha tocado una carta de " + randomCard.getName() + " (" + randomCard.getBand() + ")\n");
    }
}
