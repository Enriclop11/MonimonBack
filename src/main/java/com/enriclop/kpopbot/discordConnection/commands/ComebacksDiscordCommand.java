package com.enriclop.kpopbot.discordConnection.commands;

import com.enriclop.kpopbot.discordConnection.DiscordConnection;
import com.enriclop.kpopbot.dto.KpopComeback;
import com.enriclop.kpopbot.kpopDB.KpopComebacksApi;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ComebacksDiscordCommand extends DiscordCommand {

    public ComebacksDiscordCommand() {
        super(
                "Comebacks",
                "comebacks",
                "Muestra los próximos comebacks de Kpop",
                true,
                false,
                0,
                0
        );
    }

    @Override
    public void execute(DiscordConnection connection, MessageCreateEvent event) {
        List<KpopComeback> data = KpopComebacksApi.getUpcomingComebacks();

        log.info("Fetched comebacks: " + (data == null ? "null" : data.size()));

        if (data == null || data.isEmpty()) {
            event.getMessage().getChannel().block().createMessage("No se han encontrado comebacks próximos.").block();
            return;
        }

        String header = String.format("%-10s %-8s %-25s %-30s %-20s\n",
                "Día", "Hora", "Artista", "Álbum", "Tipo")
                + "---------------------------------------------------------------------------------------------\n";

        StringBuilder message = new StringBuilder("Próximos comebacks de Kpop:\n```");
        message.append(header);

        for (KpopComeback comeback : data) {
            String row = String.format("%-10s %-8s %-25s %-30s %-20s\n",
                    comeback.day.isEmpty() ? "??" : comeback.day,
                    comeback.time.isEmpty() ? "??:??" : comeback.time,
                    comeback.artist.isEmpty() ? "Artista desconocido" : comeback.artist,
                    comeback.albumTitle.isEmpty() ? "Álbum desconocido" : comeback.albumTitle,
                    comeback.albumType.isEmpty() ? "Tipo desconocido" : comeback.albumType
            );

            if (message.length() + row.length() > 1990) { // dejamos espacio para ```
                message.append("```"); // cierre bloque
                event.getMessage().getChannel().block().createMessage(message.toString()).block();

                message = new StringBuilder("```");
            }

            message.append(row);
        }

        if (message.length() > 3) {
            message.append("```");
            event.getMessage().getChannel().block().createMessage(message.toString()).block();
        }
    }

}
