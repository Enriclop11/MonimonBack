package com.enriclop.kpopbot.discordConnection;

import com.enriclop.kpopbot.discordConnection.commands.*;
import com.enriclop.kpopbot.kpopDB.KpopPhotos;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.security.Settings;
import com.enriclop.kpopbot.servicio.CardService;
import com.enriclop.kpopbot.servicio.UserService;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.discordjson.json.ApplicationCommandData;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Getter
@Setter
public class DiscordConnection {

    @Autowired
    private Settings settings;

    @Autowired
    private UserService userService;

    @Autowired
    private KpopPhotos kpopPhotos;

    @Autowired
    CardService cardService;

    DiscordClient client;

    GatewayDiscordClient gateway;

    List<DiscordCommand> commands = new ArrayList<>();

    public DiscordConnection() {
        commands.add(new HelpDiscordCommand());
        commands.add(new PhotosDiscordCommand());
        commands.add(new GachaDiscordCommand());
        commands.add(new ComebacksDiscordCommand());
    }

    public void restart() {
        if (this.gateway != null) {
            this.gateway.logout().block();
        }
        connect();
    }

    @PostConstruct
    public void connect() {
        assert settings != null;
        client = DiscordClient.create(settings.getTokenDiscord());

        gateway = client.login().block();

        if (gateway == null) {
            log.error("Error al conectar con Discord");
            return;
        }

        registerDiscordCommands();

        gateway.on(ReadyEvent.class).subscribe(event -> {
            log.info("Logged in as " + event.getSelf().getUsername());
        });

        //gateway.on(ApplicationCommandInteractionEvent.class)
        gateway.on(MessageCreateEvent.class)
                .doOnError(e -> log.error("Error in event stream", e))
                .retry()
                .subscribe(event -> {
                    //event.deferReply().block();
                    // log.info("Mensaje recibido: " + event.getCommandName());
                    // String command = event.getCommandName();
                    // if (event.getMessage().getAuthor().isEmpty()) return;
                    if (event.getMessage().getAuthor().get().isBot()) return;
                    String content = event.getMessage().getContent();
                    if (!content.startsWith("!")) return;
                    String command = content.split(" ")[0].substring(1);

                    String finalCommand = command.toLowerCase();

                    log.info("Comando recibido: " + finalCommand);

                    DiscordCommand commandCalled = commands.stream().filter(c -> c.getCommand().equals(finalCommand)).findFirst().orElse(null);

                    if (commandCalled == null) return;
                    if (!commandCalled.isActive()) return;
                    // if (commandCalled.isModOnly() && !checkMod(event.getUser().getId())) return;
                    // if (commandCalled.getPrice() > 0 && !checkPoints(commandCalled, event.getUser().getId().asString(), event)) return;
                    if (commandCalled.getPrice() > 0 && !checkPoints(commandCalled, event.getMessage().getAuthor().get().getId().asString(), event)) return;
                    // if (commandCalled.getCooldown() > 0 && checkCooldown(commandCalled.getCommand(), event.getUser().getId())) return;

                    try {
                        commandCalled.execute(this, event);
                    } catch (Exception e) {
                        log.error("Error al ejecutar el comando", e);
                        //event.editReply("Ha ocurrido un error al procesar el comando.").block();
                        event.getMessage().getChannel().block().createMessage("Ha ocurrido un error al procesar el comando.").block();
                    }
                });
    }

    private boolean checkPoints(DiscordCommand commandCalled, String id, MessageCreateEvent event) {
        User user = userService.getUserByDiscordUsername(id);

        if (user == null) {
            //event.reply("No tienes cuenta vinculada").block();
            event.getMessage().getChannel().block().createMessage("No tienes cuenta vinculada").block();
            return false;
        }

        if (user.getScore() < commandCalled.getPrice()) {
            //event.reply("No tienes suficientes puntos para usar este comando").block();
            event.getMessage().getChannel().block().createMessage("No tienes suficientes puntos para usar este comando").block();
            return false;
        }

        if (commandCalled.getPrice() > 0) {
            user.setScore(user.getScore() - commandCalled.getPrice());
            userService.saveUser(user);
        }

        return true;
    }
    public void registerDiscordCommands() {

        Map<String, ApplicationCommandData> discordCommands = client
                .getApplicationService()
                .getGlobalApplicationCommands(client.getApplicationId().block())
                .collectMap(ApplicationCommandData::name)
                .block();


        for (String commandName : discordCommands.keySet()) {
            client.getApplicationService()
                    .deleteGlobalApplicationCommand(client.getApplicationId().block(), discordCommands.get(commandName).id().asLong())
                    .subscribe();
            log.info("Comando eliminado: " + commandName);
        }


        /*
        for (DiscordCommand command : commands) {
            log.info("Comando registrado: " + command.getCommand() + " - " + command.getDescription());

            client.getApplicationService()
                    .createGlobalApplicationCommand(client.getApplicationId().block(), command.toRequest())
                    .subscribe();
        }

         */
    }

    public void registerCommand(DiscordCommand command) {
        log.info("Comando registrado: " + command.getCommand() + " - " + command.getDescription());

        client.getApplicationService()
                .createGlobalApplicationCommand(client.getApplicationId().block(), command.toRequest())
                .subscribe();
    }

    public void deleteCommand(DiscordCommand command) {
        Map<String, ApplicationCommandData> discordCommands = client
                .getApplicationService()
                .getGlobalApplicationCommands(client.getApplicationId().block())
                .collectMap(ApplicationCommandData::name)
                .block();

        if (discordCommands.containsKey(command.getCommand())) {
            client.getApplicationService()
                    .deleteGlobalApplicationCommand(client.getApplicationId().block(), discordCommands.get(command.getCommand()).id().asLong())
                    .subscribe();
            log.info("Comando eliminado: " + command.getCommand());
        }
    }

}
