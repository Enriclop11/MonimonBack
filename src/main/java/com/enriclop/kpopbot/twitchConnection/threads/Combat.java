package com.enriclop.kpopbot.twitchConnection.threads;

import com.enriclop.kpopbot.dto.CardCombat;
import com.enriclop.kpopbot.kpopDB.TableType;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.security.Settings;
import com.enriclop.kpopbot.servicio.CardService;
import com.enriclop.kpopbot.servicio.UserService;
import com.enriclop.kpopbot.utilities.Timer;
import com.enriclop.kpopbot.utilities.Utilities;
import com.enriclop.kpopbot.websockets.card.CardInfoService;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Slf4j
public class Combat extends Thread{

    UserService userService;

    CardService cardService;

    TwitchClient twitchClient;

    CardInfoService cardInfoService;

    Settings settings;

    User player1;
    User player2;

    CardCombat card1;
    CardCombat card2;

    List<PhotoCard> team1;
    List<PhotoCard> team2;

    Boolean accepted = false;

    public Boolean active = true;

    Timer timer;

    User winner;

    Boolean started = false;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public Combat(User user1, User user2, UserService userService, CardService cardService, TwitchClient twitchClient, CardInfoService cardInfoService, Settings settings, List<PhotoCard> team1, List<PhotoCard> team2) {
        this.userService = userService;
        this.cardService = cardService;
        this.twitchClient = twitchClient;
        this.cardInfoService = cardInfoService;
        this.settings = settings;

        this.player1 = user1;
        this.player2 = user2;

        this.team1 = team1;
        this.team2 = team2;

        timer = new Timer();
        timer.start();
    }

    public void sendMessage(String message) {
        twitchClient.getChat().sendMessage(settings.getChannelName(), message);
    }

    @Override
    public void run() {
        sendMessage("¿" + Utilities.firstLetterToUpperCase(player2.getUsername()) + " aceptara el combate? !accept");

        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            if (!active) return;
            if (accepted) return;
            if (timer.getMinutes() > 1) {
                sendMessage("¡" + Utilities.firstLetterToUpperCase(player2.getUsername()) + " no ha aceptado el combate a tiempo!");
                active = false;
                return;
            }
            if (!event.getUser().getId().equals(player2.getTwitchId())) return;

            String command = event.getMessage().split(" ")[0];

            if (command.equals("!accept")) {
                accepted = true;
                sendMessage("¡" + Utilities.firstLetterToUpperCase(player2.getUsername()) + " ha aceptado el combate!");
                startCombat();
            }
        });
    }

    public void startCombat() {
        started = true;

        log.info("Combat started between " + player1.getUsername() + " and " + player2.getUsername());

        card1 = new CardCombat(team1.get(0));
        card2 = new CardCombat(team2.get(0));

        team1.remove(0);
        team2.remove(0);

        cardInfoService.startCombat(card1, card2);

        scheduleWait(2, this::combat);
    }

    public void combat() {
        if (card1.getCurrentHp() > 0 && card2.getCurrentHp() > 0) {
            scheduleWait(1, () -> {
                attack(card1, card2);
                if (card2.getCurrentHp() > 0) {
                    scheduleWait(1, () -> {
                        attack(card2, card1);
                        combat();
                    }
                    );
                } else {
                    checkWinner();
                }
            });
        } else {
            checkWinner();
        }
    }

    private void checkWinner() {
        log.info("Checking winner");

        if (team1.isEmpty() && card1.getCurrentHp() <= 0) {
            winner = player2;
            endCombat();
        } else if (team2.isEmpty() && card2.getCurrentHp() <= 0) {
            winner = player1;
            endCombat();
        } else if (card1.getCurrentHp() <= 0) {
            changeCard(1);
            combat();
        } else if (card2.getCurrentHp() <= 0) {
            changeCard(2);
            combat();
        }
    }

    private void scheduleWait(int seconds, Runnable task) {
        scheduler.schedule(task, seconds, TimeUnit.SECONDS);
    }

    public void endCombat(){
        winner = userService.getUserById(winner.getId());
        winner.addScore(100);
        userService.saveUser(winner);

        log.info(winner.getUsername() + " has won the combat!");
        sendMessage("¡" + Utilities.firstLetterToUpperCase(winner.getUsername()) + " ha ganado el combate!");

        cardInfoService.endCombat();
        active = false;
        winner = null;
    }

    public void changeCard(Integer user) {
        if (user == 1) {
            card1 = new CardCombat(team1.get(0));
            team1.remove(0);

            cardInfoService.changeCard(card1, card2, 1);
        } else {
            card2 = new CardCombat(team2.get(0));
            team2.remove(0);

            cardInfoService.changeCard(card1, card2, 2);
        }

    }

    public void attack(CardCombat attacker, CardCombat defender){

        int critical = 1;
        if (Math.random() < 0.0625) {
            critical = 2;
        }

        double damage = 0;

        damage = (2 * 20 * critical) / 5.0;
        damage *= ((double) attacker.getAttack() / defender.getDefense());

        double type1 = TableType.modifierAgainst(attacker.getType(), defender.getType());
        double type2 = TableType.modifierAgainst(attacker.getType(), defender.getType2());
        double type = type1 * type2;

        type1 = TableType.modifierAgainst(attacker.getType2(), defender.getType());
        type2 = TableType.modifierAgainst(attacker.getType2(), defender.getType2());

        type = Math.max(type, type1 * type2);

        damage *= type;

        //random is realized as a multiplication by a random uniformly distributed integer between 217 and 255 (inclusive), followed by an integer division by 255. If the calculated damage thus far is 1, random is always 1.
        double random = Math.random() * 39 + 217;
        random /= 255.0;
        damage *= random;

        damage *= 5;

        //sendMessage(attacker.getName() + " ha atacado y ha inflinjido " + (int) damage +  " de daño !");
        defender.setCurrentHp((int) (defender.getCurrentHp() - damage));

        if (defender.getCurrentHp() < 0) {
            defender.setCurrentHp(0);
        }

        cardInfoService.attackCombat(card1, card2, (int) damage);
    }

    private void wait(int seconds) {
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
