package com.enriclop.kpopbot.twitchConnection;

import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.modelo.WatchTime;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SetWatchTime extends Thread {

    private static final int MINUTES = 10;

    private TwitchConnection twitchConnection;

    private List<User> usersInChat;

    public SetWatchTime(TwitchConnection twitchConnection) {
        this.twitchConnection = twitchConnection;
    }

    @Override
    public void run() {
        while (true) {
            wait(MINUTES);
            if (twitchConnection.isLive()) {
                setWatchTime();
            } else {
                log.info("Stream is offline");
            }
        }
    }

    private void setWatchTime() {
        log.info("Updating watch time");

        List<User> usersChat = twitchConnection.getChattersUsers();

        if (usersInChat != null && !usersInChat.isEmpty()) {
            for (User user : usersInChat) {
                for (User userChat : usersChat) {
                    if (user.getUsername().equals(userChat.getUsername())) {
                        log.info("User: " + user.getUsername());
                        WatchTime watchTime = user.getWatchTime();
                        watchTime.addMinutes(MINUTES);
                        user = twitchConnection.userService.getUserById(user.getId());
                        user.setWatchTime(watchTime);
                        twitchConnection.userService.saveUser(user);
                        break;
                    }
                }
            }
        }
        usersInChat = usersChat;
        log.info("Watch time updated");
    }

    private void wait(int minutes) {
        try {
            Thread.sleep(1000 * 60 * minutes);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
