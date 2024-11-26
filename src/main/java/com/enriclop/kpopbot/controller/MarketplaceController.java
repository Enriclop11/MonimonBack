package com.enriclop.kpopbot.controller;

import com.enriclop.kpopbot.modelo.Marketplace;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.servicio.CardService;
import com.enriclop.kpopbot.servicio.MarketplaceService;
import com.enriclop.kpopbot.servicio.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class MarketplaceController {

    @Autowired
    private MarketplaceService marketplaceService;

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    @GetMapping("/marketplace")
    public List<Marketplace> getMarketplace() {
        return marketplaceService.getMarketplace();
    }

    @PostMapping("/marketplace/buy")
    public void buyCard(@RequestParam int cardId, @RequestHeader("Authorization") String token) {
        User buyer = userService.getUserByToken(token);
        Marketplace market = marketplaceService.getMarketplaceById(cardId);
        PhotoCard card = market.getCard();
        int price = market.getPrice();
        User seller = card.getUser();

        if (buyer.getScore() >= price) {
            buyer.minusScore(price);
            userService.saveUser(buyer);
            seller.addScore(price);
            userService.saveUser(seller);

            cardService.changeUser(card, buyer);
        }
    }

    @PostMapping("/marketplace/offer")
    public void sellCard(@RequestParam SellDTO sellDTO, @RequestHeader("Authorization") String token) {

        User seller = userService.getUserByToken(token);
        PhotoCard card = cardService.getCardById(sellDTO.cardId);

        if (card.getUser().getId() != seller.getId()) {
            return;
        }

        int price = sellDTO.price;

        Marketplace market = new Marketplace();
        market.setCard(card);
        market.setPrice(price);

        marketplaceService.saveMarketplace(market);
    }

    public static class SellDTO {
        private int cardId;
        private int price;
    }

}
