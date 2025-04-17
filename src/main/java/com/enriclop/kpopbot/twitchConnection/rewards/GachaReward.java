package com.enriclop.kpopbot.twitchConnection.rewards;

import com.enriclop.kpopbot.enums.Types;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import java.util.*;

public class GachaReward implements Reward {

    @Override
    public String getName() {
        return "Suerte";
    }

    @Override
    public String getReward() {
        return "suerte de cojones";
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean isModOnly() {
        return false;
    }

    @Override
    public int getCooldown() {
        return 2;
    }

    private static final String PABLOMOTOS = "pablomotos";

    private static final List<String> PABLOMOTOS_PHOTOS = List.of(
            "https://pbs.twimg.com/media/FSK9jQ2XsAEst7g?format=jpg&name=small",
            "https://img.imgur.com/F65aj4N.jpg",
            "https://theriagames.com/wp-content/uploads/2025/02/Goblin_2.webp"
    );

    public static Map<String, int[]> SLOT_RESULTS = new HashMap<>() {{
        put("NoBitches", new int[]{10, 20});
        put("WinterUHH", new int[]{10, 40});
        put(":0", new int[]{10, 40});
        put("lalalala", new int[]{20, 60});
        put("nayeonstare", new int[]{20, 60});
        put("popipopipipopipo", new int[]{40, 60});
        put("wonyonEat", new int[]{40, 70});
        put("yujinFAT", new int[]{10, 40});
        put("yujinFinger", new int[]{10, 20});
        put("GGS", new int[]{70, 90});
        put("haewonSmack", new int[]{10, 20});
        put("HiFIVE", new int[]{10, 20});
        put("gahyunCheers", new int[]{70, 90});
        put("karinaSquish", new int[]{70, 100});
        put("karinaHuh", new int[]{70, 90});
        put(PABLOMOTOS, new int[]{0, 15});
        put("jWTF", new int[]{70, 90});
        put("chaewonPls", new int[]{70, 100});
        put("gg", new int[]{60, 80});
        put("gGs", new int[]{60, 80});
    }};

    @Override
    public void execute(TwitchConnection connection, RewardRedeemedEvent event) {
        User user = connection.getUserService().getUserByTwitchId(event.getRedemption().getUser().getId());

        Random random = new Random();

        String[] keys = SLOT_RESULTS.keySet().toArray(new String[0]);
        String[] randomKeys = new String[3];
        for (int i = 0; i < 3; i++) {
            if (randomKeys[0] != null && random.nextInt(5) == 0) {
                randomKeys[i] = randomKeys[0];
                continue;
            }

            int randomIndex = random.nextInt(keys.length);
            randomKeys[i] = keys[randomIndex];
        }

        List<String> randomKeysList = Arrays.asList(randomKeys);
        Collections.shuffle(randomKeysList);
        randomKeys = randomKeysList.toArray(new String[0]);

        StringBuilder message = new StringBuilder();
        message.append(user.getUsernameDisplay()).append(" ha sacado: ");
        for (String key : randomKeys) {
            message.append(key).append(" ");
        }
        message.append("\n");

        connection.sendMessage(message.toString());

        if (!customReward(connection, user, randomKeys)) {
            if (randomKeys[0].equals(randomKeys[1]) && randomKeys[1].equals(randomKeys[2])) {

                giveRandomCard(SLOT_RESULTS.get(randomKeys[0])[0], SLOT_RESULTS.get(randomKeys[0])[1], connection, user);

            } else if (randomKeys[0].equals(randomKeys[1]) || randomKeys[1].equals(randomKeys[2]) || randomKeys[0].equals(randomKeys[2])) {

                String repeatedKey = randomKeys[0].equals(randomKeys[1]) ? randomKeys[0] : randomKeys[2];
                giveScore(SLOT_RESULTS.get(repeatedKey)[0], SLOT_RESULTS.get(repeatedKey)[1], connection, user);
            }
        }

        connection.addCooldown(event.getRedemption().getReward().getTitle(), event.getRedemption().getUser().getId(), getCooldown());
    }

    private void giveRandomCard(int min, int max, TwitchConnection connection, User user) {
        PhotoCard randomCard =  connection.getKpopPhotos().generateRandomPhotocardByRange(min, max);

        if (randomCard == null) {
            connection.sendMessage("No se ha podido generar una carta");
            return;
        }

        randomCard.setUser(user);
        connection.getCardService().saveCard(randomCard);

        connection.sendMessage( user.getUsernameDisplay() + " te ha tocado una carta de " + randomCard.getName() + " (" + randomCard.getBand() + ")\n");
    }

    private void giveScore(int min, int max, TwitchConnection connection, User user) {
        Random random = new Random();
        int randomNumber = random.nextInt(max - min + 1) + min;

        user.addScore(randomNumber);
        connection.getUserService().saveUser(user);
        connection.sendMessage( user.getUsernameDisplay() + " has ganado " + randomNumber + " puntos.\n");
    }

    private boolean customReward(TwitchConnection conn, User user,  String[] randomKeys) {
        if (randomKeys[0].equals(PABLOMOTOS) && randomKeys[1].equals(PABLOMOTOS) && randomKeys[2].equals(PABLOMOTOS)) {
            PhotoCard customCard = new PhotoCard();
            customCard.setName("Pablo Motos");
            customCard.setBand("Club Pétalos");
            customCard.setHp(10);
            customCard.setDefense(10);
            customCard.setAttack(10);
            customCard.setType(Types.VISUAL);
            customCard.setType(Types.NONE);
            customCard.setUser(user);
            customCard.setIdolID(0);
            customCard.setPhoto(PABLOMOTOS_PHOTOS.get(new Random().nextInt(PABLOMOTOS_PHOTOS.size())));
            conn.getCardService().saveCard(customCard);
            return true;
        }

        return false;
    }
}
