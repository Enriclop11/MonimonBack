package com.enriclop.kpopbot.discordConnection.commands;

import com.enriclop.kpopbot.discordConnection.DiscordConnection;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.EmbedCreateSpec;

import java.time.Instant;

public class GachaDiscordCommand extends DiscordCommand {

    public GachaDiscordCommand() {
        super(
                "Gacha",
                "gacha",
                "Haz una tirada en el gacha",
                true,
                false,
                250,
                0
        );
    }

    @Override
    public void execute(DiscordConnection connection, MessageCreateEvent event) {

        //User user = connection.getUserService().getUserByDiscordUsername(event.getUser().getId().asString());
        User user = connection.getUserService().getUserByDiscordUsername(event.getMessage().getAuthor().get().getId().asString());

        if (user == null) {
            //event.editReply("No tienes cuenta vinculada").block();
            event.getMessage().getChannel().block().createMessage("No tienes cuenta vinculada").block();
            return;
        }

        PhotoCard randomCard =  connection.getKpopPhotos().generateRandomPhotocard();

        if (randomCard == null) {
            //event.editReply("No se ha podido generar una carta").block();
            event.getMessage().getChannel().block().createMessage("No se ha podido generar una carta").block();
            return;
        }

        user = connection.getUserService().getUserById(user.getId());

        randomCard.setUser(user);

        connection.getCardService().saveCard(randomCard);

        //event.editReply( user.getUsernameDisplay() + " te ha tocado una carta de " + randomCard.getName() + " (" + randomCard.getBand() + ")\n")
                //.withEmbeds(getEmbedPhotoCard(user, randomCard)).block();

        event.getMessage().getChannel().block().createMessage(user.getUsernameDisplay() + " te ha tocado una carta de " + randomCard.getDisplayName() + " (" + randomCard.getBand() + ")\n")
                .withEmbeds(getEmbedPhotoCard(user, randomCard)).block();
    }

    public EmbedCreateSpec getEmbedPhotoCard(User user, PhotoCard photoCard) {

        return EmbedCreateSpec.builder().title(photoCard.getDisplayName())
                .url("https://kpopping.com/profiles/idol/" + photoCard.getApiName().toLowerCase())
                .author(user.getUsernameDisplay(), null, user.getAvatar())
                .description("Photo Card de " + user.getUsernameDisplay())
                .thumbnail(user.getAvatar()).addField("Tipo", photoCard.getType().getDisplayName(), true)
                .addField("Tipo 2", photoCard.getType2() != null ? photoCard.getType2().getDisplayName() : "Ninguno", true).addField("Grupo", photoCard.getBand(), true)
                .addField("HP", String.valueOf(photoCard.getHp()), true).addField("ATK", String.valueOf(photoCard.getAttack()), true)
                .addField("DEF", String.valueOf(photoCard.getDefense()), true)
                .image(photoCard.getPhoto()).timestamp(Instant.now())
                .build();
    }
}
