package com.enriclop.kpopbot.controller;

import com.enriclop.kpopbot.dto.BoosterPackDTO;
import com.enriclop.kpopbot.dto.PhotoCardDTO;
import com.enriclop.kpopbot.kpopDB.KpopBoosterPack;
import com.enriclop.kpopbot.kpopDB.KpopService;
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

    @Autowired
    private KpopService kpopService;

    @Autowired
    private KpopBoosterPack kpopBoosterPack;

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
    public void deleteCard(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        User user = userService.getUserByToken(token);
        PhotoCard photoCard = cardService.getCardById(id);

        if (user != null && photoCard != null && user.getPhotoCards().contains(photoCard)) {
            int price = kpopService.getPrice(photoCard);
            user.addScore(price);
            user.getPhotoCards().remove(photoCard);
            userService.saveUser(user);
            cardService.deleteCardById(id);
            log.info("User " + user.getUsername() + " deleted card " + photoCard.getName());
        } else {
            log.warn("User " + user.getUsername() + " attempted to delete a card that does not exist or does not belong to them.");
        }
    }

    @GetMapping("/photoCards/price/{id}")
    public int getPrice(@PathVariable Integer id) {
        return kpopService.getPrice(cardService.getCardById(id));
    }

    @GetMapping("/boosterPacks")
    public List<BoosterPackDTO> getBoosterPacks() {
        return kpopService.getBoosterPacks().getBoosterPacks();
    }

    @GetMapping("/boosterPacks/{id}")
    public BoosterPackDTO getBoosterPackById(@PathVariable int id) {
        return kpopService.getBoosterPackById(id);
    }

    @PostMapping("/boosterPacks/open/{id}")
    public PhotoCardDTO openBoosterPack(@PathVariable int id, @RequestHeader("Authorization") String token) {
        User user = userService.getUserByToken(token);
        return new PhotoCardDTO(kpopBoosterPack.openBoosterPack(user, id));
    }


}