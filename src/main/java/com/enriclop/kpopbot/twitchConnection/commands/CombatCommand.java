package com.enriclop.kpopbot.twitchConnection.commands;

import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.enriclop.kpopbot.twitchConnection.threads.Combat;
import com.enriclop.kpopbot.utilities.Utilities;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class CombatCommand extends Command {

    public CombatCommand() {
        super(
            "Combat",
            "!combat",
            "Empieza un combate con otro usuario",
            true,
            false,
            0,
            0
        );
    }

    @Override
    public void execute(TwitchConnection connection, ChannelMessageEvent event) {

        if (connection.getActiveCombat() != null && connection.getActiveCombat().active) {
            connection.sendMessage("Ya hay un combate en curso!");
            return;
        }

        if (event.getMessage().split(" ").length < 2) {
            connection.sendMessage("Elige un usuario para combatir!");
            return;
        }

        User player1;
        User player2;

        try {
            player1 = connection.getUserService().getUserByTwitchId(event.getUser().getId());
        } catch (Exception e) {
            connection.sendMessage("No tienes ningun pokemon!");
            return;
        }

        try {
            String username = event.getMessage().split(" ")[1];

            if (username.startsWith("@")) {
                username = username.substring(1);
            }

            player2 = connection.getUserService().getUserByUsername(username.toLowerCase());

            if (player2 == null) {
                connection.sendMessage("El usuario no existe!");
                return;
            }
        } catch (Exception e) {
            connection.sendMessage("El usuario no existe!");
            return;
        }

        if (player1.getTwitchId().equals(player2.getTwitchId())) {
            connection.sendMessage("No puedes combatir contra ti mismo!");
            return;
        } else if (player1.getPhotoCards().isEmpty()) {
            connection.sendMessage("No tienes ninguna carta!");
            return;
        } else if (player2.getPhotoCards().isEmpty()) {
            connection.sendMessage("El " +  Utilities.firstLetterToUpperCase(player2.getUsername()) + " no tiene ninguna carta!");
            return;
        }

        List<PhotoCard> team1 = getSelectedCards(player1, connection);
        List<PhotoCard> team2 = getSelectedCards(player2, connection);

        if (team1.isEmpty()) {
            connection.sendMessage("No tienes ninguna carta seleccionada!");
            return;
        } else if (team2.isEmpty()) {
            connection.sendMessage("El " +  Utilities.firstLetterToUpperCase(player2.getUsername()) + " no tiene ninguna carta seleccioanda!");
            return;
        }

        connection.setActiveCombat(new Combat(player1, player2, connection.getUserService(), connection.getCardService(), connection.getTwitchClient(), connection.getCardInfoClient(), connection.getSettings(), team1, team2));
        connection.getActiveCombat().start();
    }

    @Transactional
    public List<PhotoCard> getSelectedCards(User user, TwitchConnection connection) {
        try {
            return user.getSelectedCards().stream().map(id -> connection.getCardService().getCardById(id)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting selected cards", e);
            return new ArrayList<>();
        }
    }


}
