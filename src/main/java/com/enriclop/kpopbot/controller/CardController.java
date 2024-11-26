package com.enriclop.kpopbot.controller;

import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.servicio.CardService;
import com.enriclop.kpopbot.servicio.UserService;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @Autowired
    private TwitchConnection twitchConnection;

    @GetMapping("/photoCards")
    public List<PhotoCard> getPokemons() {
        return cardService.getCards();
    }

    @GetMapping("/photoCards/{id}")
    public PhotoCard getPokemonById(Integer id) {
        return cardService.getCardById(id);
    }

    @GetMapping("/photoCards/wild/sprite")
    public String getWildPokemonSprite() {
        if (twitchConnection.getWildCard() != null)
            return twitchConnection.getWildCard().getPhoto();
        else
            return null;
    }


    @PostMapping("/photoCards/combat")
    public void endCombat() {
        twitchConnection.sendMessage(twitchConnection.getActiveCombat().getWinner().getUsername() + " ha ganado el combate!");
        twitchConnection.getActiveCombat().endCombat();
    }

    @DeleteMapping("/deleteCard/{id}")
    public void deletePokemon(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        User user = userService.getUserByToken(token);
        PhotoCard photoCard = cardService.getCardById(id);

        if (user != null && photoCard != null && user.getPhotoCards().contains(photoCard)) {
            user.getPhotoCards().remove(photoCard);
            userService.saveUser(user);
            cardService.deleteCardById(id);
            log.info("User " + user.getUsername() + " deleted card " + photoCard.getName());
        } else {
            log.warn("User " + user.getUsername() + " attempted to delete a card that does not exist or does not belong to them.");
        }
    }
}