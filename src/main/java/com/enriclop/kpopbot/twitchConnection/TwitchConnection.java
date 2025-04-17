package com.enriclop.kpopbot.twitchConnection;

import com.enriclop.kpopbot.enums.Pokeballs;
import com.enriclop.kpopbot.kpopDB.KpopPhotos;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.security.Settings;
import com.enriclop.kpopbot.servicio.CardService;
import com.enriclop.kpopbot.servicio.ItemsService;
import com.enriclop.kpopbot.servicio.UserService;
import com.enriclop.kpopbot.twitchConnection.commands.*;
import com.enriclop.kpopbot.twitchConnection.rewards.CatchReward;
import com.enriclop.kpopbot.twitchConnection.rewards.GachaReward;
import com.enriclop.kpopbot.twitchConnection.rewards.Reward;
import com.enriclop.kpopbot.twitchConnection.settings.Prices;
import com.enriclop.kpopbot.twitchConnection.threads.Combat;
import com.enriclop.kpopbot.twitchConnection.threads.Spawn;
import com.enriclop.kpopbot.twitchConnection.threads.Trade;
import com.enriclop.kpopbot.utilities.Utilities;
import com.enriclop.kpopbot.websockets.card.CardInfoService;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.eventsub.domain.RedemptionStatus;
import com.github.twitch4j.helix.domain.Chatter;
import com.github.twitch4j.helix.domain.ChattersList;
import com.github.twitch4j.helix.domain.ModeratorList;
import com.github.twitch4j.pubsub.events.ChannelSubscribeEvent;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import com.github.twitch4j.util.PaginationUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Getter
@Setter
@Slf4j
public class TwitchConnection {

    @Autowired
    UserService userService;

    @Autowired
    CardService cardService;

    @Autowired
    ItemsService itemsService;

    @Autowired
    private Settings settings;

    @Autowired
    Prices prices;

    @Autowired
    CardInfoService cardInfoClient;

    PhotoCard wildCard;

    TwitchClient twitchClient;

    EventManager eventManager;

    Combat activeCombat;

    Trade currentTrade;

    Spawn spawn;

    List<Command> commands;

    List<Reward> rewards;

    OAuth2Credential streamerCredential;

    com.github.twitch4j.helix.domain.User channel;

    private Map<String, List<String>> cooldowns = new HashMap<>();

    @Autowired
    private KpopPhotos kpopPhotos;

    public TwitchConnection() {
        commands = new ArrayList<>();
        commands.add(new LeaderboardCommand());
        commands.add(new SpawnCommand());
        commands.add(new HelpCommand());
        commands.add(new RefreshUsernameCommand());
        commands.add(new CatchCommand());
        commands.add(new PointsCommand());
        commands.add(new PhotosCommand());
        commands.add(new CombatCommand());
        commands.add(new WatchtimeCommand());
        commands.add(new SelectCommand());
        commands.add(new TradeCommand());
        commands.add(new GiftCommand());
        commands.add(new GachaCommand());

        rewards = new ArrayList<>();
        rewards.add(new CatchReward());
        rewards.add(new GachaReward());
    }


    @PostConstruct
    public void connect() {

        if (settings == null) {
            throw new IllegalStateException("Settings bean is not initialized");
        }

        if (twitchClient != null) {
            twitchClient.close();
        }

         twitchClient = TwitchClientBuilder.builder()
                 .withDefaultAuthToken(new OAuth2Credential(settings.getBotUsername(), settings.getoAuthTokenBot()))
                 .withEnableHelix(true)
                 .withEnableChat(true)
                 .withEnablePubSub(true)
                 .withChatAccount(new OAuth2Credential(settings.getBotUsername(), settings.getoAuthTokenBot()))
                 .build();

         twitchClient.getChat().joinChannel(settings.getChannelName());

         channel = getUserDetails(settings.getChannelName());

         streamerCredential = new OAuth2Credential("twitch", settings.getoAuthTokenChannel());

         twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(null, channel.getId());
         twitchClient.getPubSub().listenForSubscriptionEvents(streamerCredential, channel.getId());
         
         twitchClient.getClientHelper().enableFollowEventListener(settings.getChannelName());

         commands();

         new SetWatchTime(this).start();

         sendMessage("Bot Online");
    }

    public void sendMessage(String message) {
        twitchClient.getChat().sendMessage(settings.getChannelName(), message);
    }

    public void commands() {
        eventManager = twitchClient.getEventManager();

        eventManager.onEvent(ChannelMessageEvent.class, event -> {
            if (!event.getMessage().startsWith("!")) return;

            start(event.getUser().getId());

            String command = event.getMessage().split(" ")[0];
            String finalCommand = command.toLowerCase();
            Command commandCalled = commands.stream().filter(c -> c.getCommand().equals(finalCommand)).findFirst().orElse(null);

            if (commandCalled == null) return;
            if (!commandCalled.isActive()) return;
            if (commandCalled.isModOnly() && !checkMod(event.getUser().getId())) return;
            if (!checkPoints(commandCalled, event.getUser().getId())) return;
            if (commandCalled.getCooldown() > 0 && checkCooldown(commandCalled.getCommand(), event.getUser().getId())) return;

            commandCalled.execute(this, event);
        });

        eventManager.onEvent(RewardRedeemedEvent.class, event -> {
            String reward = event.getRedemption().getReward().getTitle();

            String finalReward = reward.toLowerCase();
            Reward rewardCalled = rewards.stream().filter(r -> r.getReward().equals(finalReward)).findFirst().orElse(null);

            if (rewardCalled == null) return;
            if (!rewardCalled.isActive()) return;
            if (rewardCalled.isModOnly() && !checkMod(event.getRedemption().getUser().getId())) return;
            if (rewardCalled.getCooldown() > 0 && checkCooldown(rewardCalled.getReward(), event.getRedemption().getUser().getId())) return;


            rewardCalled.execute(this, event);
        });

        eventManager.onEvent(FollowEvent.class , event -> {
            followingReward(event.getUser().getId());
        });

        eventManager.onEvent(ChannelSubscribeEvent.class, event -> {
            subReward(event.getData().getUserId());
        });

    }

    private boolean checkPoints(Command commandCalled, String id) {
        User user = userService.getUserByTwitchId(id);
        if (user == null) {
            start(id);
            user = userService.getUserByTwitchId(id);
        }

        if (user.getScore() < commandCalled.getPrice()) {
            sendMessage("No tienes suficientes puntos para usar este comando!");
            return false;
        }

        if (commandCalled.getPrice() > 0) {
            user.setScore(user.getScore() - commandCalled.getPrice());
            userService.saveUser(user);
        }

        return true;
    }

    public boolean checkMod(String userId) {
        if (channel.getId().equals(userId)) return true;

        ModeratorList resultList = twitchClient.getHelix().getModerators(settings.getTokenChannel(), getChannel().getId(), null, null, null).execute();
        return resultList.getModerators().stream().anyMatch(m -> m.getUserId().equals(userId));
    }

    public boolean checkCooldown(String command, String id) {
        if (cooldowns.containsKey(command)) {
            List<String> users = cooldowns.get(command);
            if (users.contains(id)) {
                sendMessage("Espera un momento antes de volver a usar este comando!");
                return true;
            } else {
                cooldowns.put(command, users);
            }
        } else {
            List<String> users = new ArrayList<>();
            cooldowns.put(command, users);
        }

        return false;
    }

    public void addCooldown(String command, String id, int minutesCD) {
        if (cooldowns.containsKey(command)) {
            List<String> users = cooldowns.get(command);
            users.add(id);
        } else {
            List<String> users = new ArrayList<>();
            users.add(id);
            cooldowns.put(command, users);
        }

        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cooldowns.get(command).remove(id);
            }
        }, (long) minutesCD * 60 * 1000);
    }

    public Collection<Chatter> getChatters() {

        return PaginationUtil.getPaginated(
                cursor -> {
                    try {
                        return twitchClient.getHelix().getChatters(settings.getTokenChannel(), channel.getId(), channel.getId(), 1000, cursor).execute();
                    } catch (Exception e) {
                        log.error("Error", e);
                        return null;
                    }
                },
                ChattersList::getChatters,
                call -> call.getPagination() != null ? call.getPagination().getCursor() : null
        );
    }

    public List<User> getChattersUsers() {
        Collection<Chatter> chatters = getChatters();
        List<User> users = new ArrayList<>();
        for (Chatter chatter : chatters) {
            User user = userService.getUserByTwitchId(chatter.getUserId());
            if (user != null) {
                users.add(user);
            } else {
                start(chatter.getUserId());
                users.add(userService.getUserByTwitchId(chatter.getUserId()));
            }
        }
        return users;
    }


    public void setSpawn(Boolean active, int cdMinutes, int maxCdMinutes) {
        if (spawn != null) {
            spawn.active = false;
            log.info("Spawn thread deactivated");
        }
        if (active) {
            spawn = new Spawn(this, cdMinutes, maxCdMinutes);
            spawn.start();
            log.info("Spawn thread started with cdMinutes: {} and maxCdMinutes: {}", cdMinutes, maxCdMinutes);
        } else {
            log.info("Stopping spawn");
        }
    }

    public void start (String twitchId) {
        if (userService.getUserByTwitchId(twitchId) == null) {
            com.github.twitch4j.helix.domain.User user = getUserDetails(Integer.parseInt(twitchId));
            User newUser = new User(user.getId(), user.getDisplayName().toLowerCase(), user.getProfileImageUrl());
            userService.saveUser(newUser);
        }
    }

    public void followingReward(String twitchId) {
        start(twitchId);

        User user = userService.getUserByTwitchId(twitchId);

        userService.saveUser(user);
    }

    public void subReward(String twitchId){
        start(twitchId);

        User user = userService.getUserByTwitchId(twitchId);

        userService.saveUser(user);
    }

    public com.github.twitch4j.helix.domain.User getUserDetails(String username) {
        com.github.twitch4j.helix.domain.User[] user = new com.github.twitch4j.helix.domain.User[1];

        twitchClient.getHelix().getUsers(null, null, List.of(username)).execute().getUsers().forEach(u -> {
            user[0] = u;
        });

        return user[0];
    }

    public com.github.twitch4j.helix.domain.User getUserDetails(int id) {
        com.github.twitch4j.helix.domain.User[] users = new com.github.twitch4j.helix.domain.User[1];

        twitchClient.getHelix().getUsers(null, List.of(String.valueOf(id)), null).execute().getUsers().forEach(user -> {
            users[0] = user;
        });

        return users[0];
    }

    public PhotoCard spawnPhoto() {
        PhotoCard newPokemon = kpopPhotos.generateRandomPhotocard();

        if (newPokemon != null) {
            wildCard = newPokemon;

            try {
                sendMessage("Ha aparecido una foto de " + Utilities.firstLetterToUpperCase(wildCard.getName()) + " (" + Utilities.firstLetterToUpperCase(wildCard.getBand()) + ")" + " en el suelo!");
                cardInfoClient.sendWildCard(wildCard);
                return wildCard;
            } catch (Exception e) {
                System.out.println("Error al enviar el sprite del pokemon");
            }
        }
        return null;
    }

    public void catchPokemon(String idTwitch, Pokeballs pokeball) {
        start(idTwitch);

        if (wildCard == null) {
            sendMessage("No hay ninguna photocard!");
            return;
        }

        int random = (int) (Math.random() * pokeball.catchRate) + 1;


        int catchDifficulty = 101 - wildCard.getPopularity();

        System.out.println(random  + " / " + catchDifficulty);

        boolean caught = random < catchDifficulty;

        cardInfoClient.sendCatchPokemon(pokeball.toString(), caught);

        User user = userService.getUserByTwitchId(idTwitch);
        if (caught) {
            wildCard.setUser(user);
            cardService.saveCard(wildCard);

            sendMessage(Utilities.firstLetterToUpperCase(user.getUsername()) + " ha conseguido una foto de " + Utilities.firstLetterToUpperCase(wildCard.getName()) + "!");

            wildCard = null;
        } else {
            //sendMessage("La foto de " + Utilities.firstLetterToUpperCase(wildPokemon.getName()) + " se le ha escapado de las manos a " + user.getUsername() + "!");
        }

    }

    public boolean isLive() {
        return !twitchClient.getHelix().getStreams(settings.getTokenBot(), null, null, null, null, null, null, List.of(settings.getChannelName())).execute().getStreams().isEmpty();
    }

    public void refreshAllCards() {
        List<PhotoCard> cards = cardService.getCards();

        for (PhotoCard card : cards) {
            log.info("Regenerating card: " + card.getName());
            KpopPhotos.regeneratePhotocard(card);
            cardService.saveCard(card);
        }
    }

    public void returnRedemption(String rewardId, String id) {
              twitchClient.getHelix().updateRedemptionStatus(
                settings.getTokenChannel(),
                channel.getId(),
                rewardId,
                List.of(id),
                RedemptionStatus.CANCELED
        ).execute();
    }
}