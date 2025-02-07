package com.enriclop.kpopbot.twitchConnection.threads;

import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Spawn extends Thread {

    TwitchConnection conn;

    public Boolean active = true;

    public int cdMinutes = 5;

    public int maxcdMinutes = 10;

    PhotoCard wildCard;

    public Spawn() {
    }

    public Spawn(TwitchConnection conn, int cdMinutes, int maxcdMinutes) {
        this.conn = conn;
        this.cdMinutes = cdMinutes;
        this.maxcdMinutes = maxcdMinutes;
    }

    public void run() {

        while (active) {
            try {
                int random = (int) (Math.random() * (maxcdMinutes - cdMinutes)) + cdMinutes;
                Thread.sleep(1000 * 60 * random);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            log.info("Spawning card");
            if (active && conn.isLive())  {
                wildCard = conn.spawnPhoto();

                log.info("Card spawned: " + wildCard.getName());

                Timer timer = new Timer(this, wildCard);
                timer.start();
            }
         }
    }

}

class Timer extends Thread {

    Spawn spawn;

    PhotoCard wildCard;

    public Timer(Spawn spawn, PhotoCard wildCard) {
        this.spawn = spawn;
        this.wildCard = wildCard;
    }

    public void run() {
        try {
            Thread.sleep(1000 * 60 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (spawn.wildCard == spawn.conn.getWildCard() && spawn.active) {
            spawn.conn.sendMessage("La foto de " + wildCard.getName() + " ha volado con el aire.");
            spawn.conn.setWildCard(null);
        }
    }
}