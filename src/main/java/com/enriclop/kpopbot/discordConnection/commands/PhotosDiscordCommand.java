package com.enriclop.kpopbot.discordConnection.commands;

import com.enriclop.kpopbot.discordConnection.DiscordConnection;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Slf4j
public class PhotosDiscordCommand extends DiscordCommand {

    public PhotosDiscordCommand() {
        super("Photos", "photos", "Muestra tus photocards", true, false, 0, 0);
    }

    @Override
    public ApplicationCommandRequest toRequest() {
        return ApplicationCommandRequest.builder().name(getCommand()).description(getDescription()).build();
    }

    @Override
    public void execute(DiscordConnection connection, MessageCreateEvent event) {

        //String username = event.getUser().getId().asString();
        String discordId = event.getMessage().getAuthor().get().getId().asString();
        log.info("El usuario " + discordId + " ha usado el comando /" + getCommand());

        User user;

        try {
            user = connection.getUserService().getUserByDiscordUsername(discordId);

            if (user == null) {
                //event.editReply("No tienes cuenta vinculada").block();
                event.getMessage().getChannel().block().createMessage("No tienes cuenta vinculada").block();
                return;
            }

            log.info("El usuario " + discordId + " es el usuario " + user.getUsernameDisplay());
        } catch (Exception e) {
            //event.editReply("No tienes cuenta vinculada").block();
            event.getMessage().getChannel().block().createMessage("No tienes cuenta vinculada").block();
            return;
        }

        if (user.getPhotoCards().isEmpty()) {
            //event.editReply("El usuario " + user.getUsernameDisplay() + " no tiene photo cards").block();
            event.getMessage().getChannel().block().createMessage("El usuario " + user.getUsernameDisplay() + " no tiene photo cards").block();
            return;
        }

        EmbedCreateSpec embedPokemon = getEmbedPhotoCard(user, 0);

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.secondary("select_" + user.getId() + "_1", "Seleccionar"));
        if (user.getPhotoCards().size() > 1) {
            buttons.add(Button.primary("page_" + user.getId() + "_2", "▶"));
        }

        //event.editReply().withEmbeds(embedPokemon).withComponents(ActionRow.of(buttons)).block();
        event.getMessage().getChannel().block().createMessage(MessageCreateSpec.builder()
                .addEmbed(embedPokemon)
                .addComponent(ActionRow.of(buttons))
                .build()).block();


        connection.getGateway().on(ButtonInteractionEvent.class, eventButton -> {

            //log.info("Interaccion de boton " + eventButton.getCustomId() + " del usuario " + eventButton.getInteraction().getUser().getUsername());


            if (eventButton.getCustomId().startsWith("page")) {
                String[] data = eventButton.getCustomId().split("_");
                int userId = Integer.parseInt(data[1]);
                int page = Integer.parseInt(data[2]);

                User user1 = connection.getUserService().getUserById(userId);

                EmbedCreateSpec embedPokemon1 = getEmbedPhotoCard(user1, page - 1);

                List<Button> buttons1 = new ArrayList<>();
                if (page > 1) {
                    buttons1.add(Button.primary("page_" + user1.getId() + "_" + (page - 1), "◀"));
                }
                buttons1.add(Button.secondary("select_" + user1.getId() + "_" + (page), "Seleccionar"));
                if (user1.getPhotoCards().size() > page) {
                    buttons1.add(Button.primary("page_" + user1.getId() + "_" + (page + 1), "▶"));
                }


                return eventButton.edit().withEmbeds(embedPokemon1).withComponents(ActionRow.of(buttons1));
            } else {
                if (eventButton.getCustomId().startsWith("select")) {
                    eventButton.deferEdit().block();

                    String[] data = eventButton.getCustomId().split("_");
                    int userId = Integer.parseInt(data[1]);
                    int selectedIndex = Integer.parseInt(data[2]) - 1;

                    System.out.println("Seleccionando photo card " + selectedIndex + " de " + userId);

                    User user1 = connection.getUserService().getUserById(userId);

                    if (!Objects.equals(user1.getDcUsername(), eventButton.getInteraction().getUser().getUsername())) {
                        return eventButton.reply().withContent(eventButton.getInteraction().getUser().getUsername() + " no puedes seleccionar la photo card de otro usuario");
                    }

                    PhotoCard photoCard = user1.getPhotoCards().get(selectedIndex);
                    user1.selectCard(photoCard.getId());
                    connection.getUserService().saveUser(user1);

                    return eventButton.reply().withContent("Photo Card " + photoCard.getDisplayName() + " seleccionado").withEphemeral(true);
                }

                return Mono.empty();
            }
        }).timeout(Duration.ofMinutes(30)).onErrorResume(TimeoutException.class, ignore -> Mono.empty()).subscribe();
    }

    public EmbedCreateSpec getEmbedPhotoCard(User user, int pokemonIndex) {
        PhotoCard photoCard = user.getPhotoCards().get(pokemonIndex);

        return EmbedCreateSpec.builder().title(photoCard.getDisplayName())
                .url("https://kpopping.com/profiles/idol/" + photoCard.getApiName().toLowerCase())
                .author(user.getUsernameDisplay(), null, user.getAvatar())
                .description("Photo Card de " + user.getUsernameDisplay())
                .thumbnail(user.getAvatar()).addField("Tipo", photoCard.getType().getDisplayName(), true)
                .addField("Tipo 2", photoCard.getType2() != null ? photoCard.getType2().getDisplayName() : "Ninguno", true).addField("Grupo", photoCard.getBand(), true)
                .addField("HP", String.valueOf(photoCard.getHp()), true).addField("ATK", String.valueOf(photoCard.getAttack()), true)
                .addField("DEF", String.valueOf(photoCard.getDefense()), true)
                .image(photoCard.getPhoto()).timestamp(Instant.now())
                .footer("Photo Card " + (user.getPhotoCards().indexOf(photoCard) + 1) + " / " + user.getPhotoCards().size(), null)
                .build();
    }
}
