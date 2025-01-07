package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.Items;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import static com.enriclop.kpopbot.enums.Pokeballs.*;
import static com.enriclop.kpopbot.enums.Pokeballs.POKEBALL;

public class CatchCommand implements Command {

    @Override
    public String getName() {
        return "Catch";
    }

    @Override
    public String getCommand() {
        return "!catch";
    }

    @Override
    public String getDescription() {
        return "Atrapa una photocard";
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean isModOnly() {
        return false;
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {
        connection.start(event.getUser().getId());

        if (connection.getWildCard() != null) {

            String pokeball;

            try {
                pokeball = event.getMessage().split(" ")[1];
            } catch (Exception e) {
                pokeball = "pokeball";
            }

            Items items = connection.getUserService().getUserByTwitchId(event.getUser().getId()).getItems();

            switch (pokeball) {
                case "superball" -> {
                    if (items.getSuperball() > 0) {
                        items.useSuperball();
                        connection.getItemsService().saveItem(items);
                        connection.catchPokemon(event.getUser().getId(), SUPERBALL);
                    } else {
                        connection.sendMessage("No tienes Super Balls!");
                    }
                }
                case "ultraball" -> {
                    if (items.getUltraball() > 0) {
                        items.useUltraball();
                        connection.getItemsService().saveItem(items);
                        connection.catchPokemon(event.getUser().getId(), ULTRABALL);
                    } else {
                        connection.sendMessage("No tienes Ultra Balls!");
                    }
                }
                case "masterball" -> {
                    if (items.getMasterball() > 0) {
                        items.useMasterball();
                        connection.getItemsService().saveItem(items);
                        connection.catchPokemon(event.getUser().getId(), MASTERBALL);
                    } else {
                        connection.sendMessage("No tienes Master Balls!");
                    }
                }
                default -> connection.catchPokemon(event.getUser().getId(), POKEBALL);
            }

        } else {
            connection.sendMessage("No hay ninguna photocard!");
        }
    }
}
