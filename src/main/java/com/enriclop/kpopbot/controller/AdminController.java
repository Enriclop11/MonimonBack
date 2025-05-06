package com.enriclop.kpopbot.controller;

import com.enriclop.kpopbot.enums.Types;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.servicio.CardService;
import com.enriclop.kpopbot.servicio.UserService;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.enriclop.kpopbot.twitchConnection.commands.Command;
import com.enriclop.kpopbot.twitchConnection.events.Event;
import com.enriclop.kpopbot.twitchConnection.rewards.Reward;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    CardService cardService;

    @Autowired
    TwitchConnection twitchConnection;

    private class UserPoints {
        String username;
        Integer points;
    }

    @PostMapping("/user/points")
    public void setUserPoints(@RequestBody UserPoints userPoints) {
        User user = userService.getUserByUsername(userPoints.username);
        if (user == null) {
            user = userService.getUserById(Integer.valueOf(userPoints.username));
        }

        if (user != null) {
            user.setScore(userPoints.points);
            userService.saveUser(user);
        } else {
            log.error("User not found: " + userPoints.username);
        }
    }

    private class CustomCardCreate {
        String name;
        String band;
        String fullName;
        String photo;
        String type;
        String type2;
        int attack;
        int defense;
        int hp;
        int popularity;
        String user;
    }

    @PostMapping("/card/create")
    public void createCustomCard(@RequestBody CustomCardCreate customCardCreate) {
        User user = userService.getUserByUsername(customCardCreate.user);
        if (user == null) {
            user = userService.getUserById(Integer.valueOf(customCardCreate.user));
        }

        if (user != null) {
            cardService.createCustomCard(customCardCreate.name, customCardCreate.band, customCardCreate.fullName, customCardCreate.photo, Types.valueOf(customCardCreate.type), Types.valueOf(customCardCreate.type2), customCardCreate.attack, customCardCreate.defense, customCardCreate.hp, customCardCreate.popularity, user);
        } else {
            log.error("User not found: " + customCardCreate.user);
        }
    }

    @PostMapping("/card/delete")
    public void deleteCard(@RequestBody Integer id) {
        cardService.deleteCardById(id);
    }

    @GetMapping("/commands")
    public ResponseEntity<List<Command>> getCommands() {
        return new ResponseEntity<>(twitchConnection.getCommands(), HttpStatus.OK);
    }

    @AllArgsConstructor
    private static class CommandSettings {
        String name;
        String command;
        String description;
        boolean active;
        boolean modOnly;
        int price;
        int cooldown;
    }

    @PostMapping("/command/edit")
    public ResponseEntity<List<Command>> setCommand(@RequestBody CommandSettings commandSettings) {
       List<Command> commands = twitchConnection.getCommands();

        Command command = null;

        for (Command cmd : commands) {
            log.info("Command: " + cmd.getName() + " - " + commandSettings.name);
            if (cmd.getName().equalsIgnoreCase(commandSettings.name)) {
                command = cmd;
                commands.remove(cmd);
                break;
            }
        }

        if (command != null) {
            command.setCommand(commandSettings.command);
            command.setDescription(commandSettings.description);
            command.setActive(commandSettings.active);
            command.setModOnly(commandSettings.modOnly);
            command.setPrice(commandSettings.price);
            command.setCooldown(commandSettings.cooldown);
            commands.add(command);
            twitchConnection.setCommands(commands);
        } else {
            log.error("Command not found: " + commandSettings.command);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(twitchConnection.getCommands(), HttpStatus.OK);
    }

    @GetMapping("/rewards")
    public ResponseEntity<List<Reward>> getRewards() {
        return new ResponseEntity<>(twitchConnection.getRewards(), HttpStatus.OK);
    }

    @AllArgsConstructor
    private static class RewardSettings {
        String name;
        String reward;
        boolean active;
        boolean modOnly;
        int cooldown;
    }

    @PostMapping("/reward/edit")
    public ResponseEntity<List<Reward>> setReward(@RequestBody RewardSettings rewardSettings) {
        List<Reward> rewards = twitchConnection.getRewards();

        Reward reward = null;

        for (Reward rwd : rewards) {
            if (rwd.getName().equals(rewardSettings.name)) {
                reward = rwd;
                rewards.remove(rwd);
                break;
            }
        }

        if (reward != null) {
            reward.setName(rewardSettings.name);
            reward.setReward(rewardSettings.reward);
            reward.setActive(rewardSettings.active);
            reward.setModOnly(rewardSettings.modOnly);
            reward.setCooldown(rewardSettings.cooldown);
            rewards.add(reward);
            twitchConnection.setRewards(rewards);
        } else {
            log.error("Reward not found: " + rewardSettings.reward);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(twitchConnection.getRewards(), HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getEvents() {
        return new ResponseEntity<>(twitchConnection.getEvents(), HttpStatus.OK);
    }

    @AllArgsConstructor
    private static class EventSettings {
        String name;
        boolean active;
        int minCooldown;
        int maxCooldown;
    }

    @PostMapping("/event/edit")
    public ResponseEntity<List<Event>> setEvent(@RequestBody EventSettings eventSettings) {
        List<Event> events = twitchConnection.getEvents();

        Event event = null;

        for (Event evt : events) {
            if (evt.getName().equals(eventSettings.name)) {
                event = evt;
                events.remove(evt);
                break;
            }
        }

        if (event != null) {
            event.stop();

            event.setActive(eventSettings.active);
            event.setMinCooldown(eventSettings.minCooldown);
            event.setMaxCooldown(eventSettings.maxCooldown);

            if (event.isActive())
                event.run(twitchConnection);


            events.add(event);
            twitchConnection.setEvents(events);
        } else {
            log.error("Event not found: " + eventSettings.name);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(twitchConnection.getEvents(), HttpStatus.OK);
    }

}
