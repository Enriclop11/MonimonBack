package com.enriclop.kpopbot.kpopDB;

import com.enriclop.kpopbot.dto.BoosterPackDTO;
import com.enriclop.kpopbot.dto.CustomCardDTO;
import com.enriclop.kpopbot.modelo.Idol;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.servicio.CardService;
import com.enriclop.kpopbot.servicio.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class KpopBoosterPack {

    @Autowired
    private KpopService kpopService;
    @Autowired
    private CardService cardService;
    @Autowired
    private UserService userService;

    public PhotoCard openBoosterPack(User user, int boosterPackId) {
        BoosterPackDTO boosterPack = kpopService.getBoosterPackById(boosterPackId);

        if (boosterPack.getPrice() > user.getScore()) {
            return null;
        }

        user.setScore((int) (user.getScore() - boosterPack.getPrice()));
        userService.saveUser(user);

        if (boosterPack == null) {
            return null;
        }

        List<CustomCardDTO> customCards = boosterPack.getCustomCards();
        Map<CustomCardDTO, Integer> probabilityMap = new HashMap<>();

        int totalRarity = customCards.stream().mapToInt(CustomCardDTO::getRarity).sum();

        Random randomIdol = new Random();
        int randomIdolIndex = randomIdol.nextInt(boosterPack.getIdolIds().size());
        String idolId = boosterPack.getIdolIds().get(randomIdolIndex);

        CustomCardDTO randomCommonCard = new CustomCardDTO(idolId, kpopService.getRandomPhoto(idolId), 1);

        int commonProbability = 80;
        //probabilityMap.put(commonProbability, randomCommonCard);

        for (CustomCardDTO customCard : customCards) {
            int probability = (int) Math.floor(((double) customCard.getRarity() / totalRarity) * 100);
            probabilityMap.put(customCard, probability);
        }

        Random random = new Random();
        int randomNumber = random.nextInt(100);

        int start = 0;
        for (Map.Entry<CustomCardDTO, Integer> entry : probabilityMap.entrySet()) {
            int end = start + entry.getValue();

            if (randomNumber >= start && randomNumber < end) {
                if (entry.getValue() != null) {
                    CustomCardDTO customCard = entry.getKey();
                    return addCardToUser(user, customCard);
                }
                break;
            }

            start = end;
        }
        log.error("Error opening booster pack");
        return null;
    }

    public PhotoCard addCardToUser(User user, CustomCardDTO customCardDTO) {
        Idol idol = kpopService.getIdolById(customCardDTO.getIdolId());

        idol = customCardDTO.setCustomPropertiesIdol(idol);

        PhotoCard photoCard = new PhotoCard(idol, customCardDTO.getCustomPhoto());

        photoCard = customCardDTO.setCustomPropertiesCard(photoCard);
        photoCard.setUser(user);
        cardService.saveCard(photoCard);
        return photoCard;
    }
}
