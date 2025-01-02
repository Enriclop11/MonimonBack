package com.enriclop.kpopbot.controller;

import com.enriclop.kpopbot.dto.MarketplaceDTO;
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
    public List<MarketplaceDTO> getMarketplace() {
        return marketplaceService.getMarketplaceDTO();
    }

    @PostMapping("/marketplace/buy")
    public void buyCard(@RequestBody int offerId, @RequestHeader("Authorization") String token) {
        User buyer = userService.getUserByToken(token);
        Marketplace market = marketplaceService.getMarketplaceById(offerId);
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
    public void sellCard(@RequestBody SellDTO sellDTO, @RequestHeader("Authorization") String token) {
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
        public int cardId;
        public int price;
    }

    @DeleteMapping("/marketplace/delete/{offerId}")
    public void removeOffer(@PathVariable int offerId, @RequestHeader("Authorization") String token) {
        User seller = userService.getUserByToken(token);
        Marketplace market = marketplaceService.getMarketplaceById(offerId);

        if (market.getCard().getUser().getId() != seller.getId()) {
            return;
        }
        

        marketplaceService.deleteMarketplace(offerId);
    }

}
