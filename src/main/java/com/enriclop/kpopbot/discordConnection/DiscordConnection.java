package com.enriclop.kpopbot.discordConnection;

import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.security.Settings;
import com.enriclop.kpopbot.servicio.UserService;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Component
public class DiscordConnection {

    @Autowired
    private Settings settings;

    @Autowired
    private UserService userService;

    DiscordClient client;

    GatewayDiscordClient gateway;

    public DiscordConnection() {
        //connect();
    }

    public void restart() {
        if (this.gateway != null) {
            this.gateway.logout().block();
        }
        //connect();
    }

    public void connect() {
        //client = DiscordClient.create(settings.tokenDiscord);

        gateway = client.login().block();

        gateway.on(ReadyEvent.class).subscribe(event -> {
            System.out.println("Logged in as " + event.getSelf().getUsername());
        });

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            Message message = event.getMessage();

            String command = message.getContent().split(" ")[0];

            if (!command.startsWith("!")) return;

            command = command.substring(1);
            command = command.toLowerCase();

            switch (command) {
                case "ping":
                    MessageChannel channel = message.getChannel().block();
                    channel.createMessage("Pong!").block();
                    System.out.println(message.getAuthor().get().getUsername());
                    break;
                case "help":
                    MessageChannel channel2 = message.getChannel().block();
                    channel2.createMessage("Comandos disponibles: !photos").block();
                    break;
                case "photos":
                    lookPC(event);
                    break;
                default:
                    break;
            }
        });
    }

    public void lookPC(MessageCreateEvent event) {
        System.out.println("Looking PC");

        MessageChannel channel = event.getMessage().getChannel().block();

        User user = null;

        try{
            user = userService.getUserByDiscordUsername(event.getMessage().getAuthor().get().getUsername());

            if (user == null) {
                channel.createMessage("No tienes cuenta vinculada").block();
                return;
            }
        } catch (Exception e) {
            channel.createMessage("No tienes cuenta vinculada").block();
            return;
        }

        if (user.getPhotoCards().isEmpty()) {
            channel.createMessage("El usuario " + user.getUsernameDisplay() + " no tiene photo cards").block();
            return;
        }

        EmbedCreateSpec embedPokemon = getEmbedPhotoCard(user, 0);

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.secondary("select_" + user.getId() + "_1", "Seleccionar"));
        if (user.getPhotoCards().size() > 1) {
            buttons.add(Button.primary("page_" + user.getId() + "_2", "▶"));
        }


        Mono<Message> createMessageMono = channel.createMessage(MessageCreateSpec.builder()
                .addEmbed(embedPokemon)
                .addComponent(ActionRow.of(buttons))
                .build());

        Mono<Void> tempListener = gateway.on(ButtonInteractionEvent.class, eventButton -> {
            if (eventButton.getCustomId().startsWith("page")) {
                String[] data = eventButton.getCustomId().split("_");
                int userId = Integer.parseInt(data[1]);
                int page = Integer.parseInt(data[2]);

                User user1 = userService.getUserById(userId);

                EmbedCreateSpec embedPokemon1 = getEmbedPhotoCard(user1, page - 1);

                List<Button> buttons1 = new ArrayList<>();
                if (page > 1) {
                    buttons1.add(Button.primary("page_" + user1.getId() + "_" + (page - 1), "◀"));
                }
                buttons1.add(Button.secondary("select_" + user1.getId() + "_" + (page), "Seleccionar"));
                if (user1.getPhotoCards().size() > page) {
                    buttons1.add(Button.primary("page_" + user1.getId() + "_" + (page + 1), "▶"));
                }


                return eventButton.edit().withEmbeds(embedPokemon1)
                        .withComponents(ActionRow.of(buttons1));
            } else {
                if (eventButton.getCustomId().startsWith("select")) {
                    String[] data = eventButton.getCustomId().split("_");
                    int userId = Integer.parseInt(data[1]);
                    int selectedIndex = Integer.parseInt(data[2]) - 1;

                    System.out.println("Seleccionando photo card " + selectedIndex + " de " + userId);

                    User user1 = userService.getUserById(userId);

                    if (!Objects.equals(user1.getDcUsername(), eventButton.getInteraction().getUser().getUsername())) {
                        return eventButton.reply().withContent(eventButton.getInteraction().getUser().getUsername() + " no puedes seleccionar la photo card de otro usuario");
                    }

                    PhotoCard photoCard = user1.getPhotoCards().get(selectedIndex);
                    //user1.setPhotoCardSelected(photoCard.getId());
                    userService.saveUser(user1);

                    return eventButton.reply().withContent("Photo Card " + photoCard.getDisplayName() + " seleccionado").withEphemeral(true);
                }

                return Mono.empty();
            }
        }).timeout(Duration.ofMinutes(30))
                .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
                .then();

        createMessageMono.then(tempListener).subscribe();
    }

    public EmbedCreateSpec getEmbedPhotoCard(User user, int pokemonIndex) {
        PhotoCard photoCard = user.getPhotoCards().get(pokemonIndex);

        return EmbedCreateSpec.builder()
                .title(photoCard.getDisplayName())
                .url("https://kpopping.com/profiles/idol/" + photoCard.getApiName().toLowerCase())
                .author(user.getUsernameDisplay(), null, user.getAvatar())
                .description("Photo Card de " + user.getUsernameDisplay())
                .thumbnail(user.getAvatar())
                .addField("Tipo", photoCard.getType().getDisplayName(), true)
                .addField("Tipo 2", photoCard.getType2().getDisplayName(), true)
                .addField("Grupo", photoCard.getBand(), true)
                .addField("HP", String.valueOf(photoCard.getHp()), true)
                .addField("ATK", String.valueOf(photoCard.getAttack()), true)
                .addField("DEF", String.valueOf(photoCard.getDefense()), true)
                .image(photoCard.getPhoto())
                .timestamp(Instant.now())
                .footer("Photo Card " + (user.getPhotoCards().indexOf(photoCard) + 1) + " / " + user.getPhotoCards().size(), null)
                .build();
    }

}
