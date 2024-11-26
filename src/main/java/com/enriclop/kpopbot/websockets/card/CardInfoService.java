package com.enriclop.kpopbot.websockets.card;

import com.enriclop.kpopbot.dto.CardCombat;
import com.enriclop.kpopbot.modelo.PhotoCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CardInfoService {

    @Autowired
    private SimpMessagingTemplate template;

    public void sendWildCard(PhotoCard wildPokemon) {
        template.convertAndSend("/topic/pokemon/wild", wildPokemon);
    }

    public void sendCatchPokemon(String pokeball, Boolean caught) {
        Map<String, String> data = new HashMap<>();
        data.put("pokeball", pokeball.toLowerCase());
        data.put("caught", caught.toString());
        template.convertAndSend("/topic/pokemon/catch", data);
    }

    public void startCombat(CardCombat pokemon1, CardCombat pokemon2) {
        Map<String, Object> data = new HashMap<>();
        data.put("card1", pokemon1);
        data.put("card2", pokemon2);
        data.put("start", true);
        template.convertAndSend("/topic/card/combat", data);
    }

    public void changeCard(CardCombat pokemon1, CardCombat pokemon2, int cardChanged) {
        Map<String, Object> data = new HashMap<>();
        data.put("card1", pokemon1);
        data.put("card2", pokemon2);
        data.put("change", true);
        data.put("cardChanged", cardChanged);
        template.convertAndSend("/topic/card/combat", data);
    }

    public void attackCombat(CardCombat pokemon1, CardCombat pokemon2, double attack) {
        Map<String, Object> data = new HashMap<>();
        data.put("card1", pokemon1);
        data.put("card2", pokemon2);
        data.put("attack", attack);
        template.convertAndSend("/topic/card/combat", data);
    }

    public void endCombat() {
        Map<String, Object> data = new HashMap<>();
        data.put("end", true);
        template.convertAndSend("/topic/card/combat", data);
    }
}
