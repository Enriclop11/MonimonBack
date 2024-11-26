package com.enriclop.kpopbot.controller;

import com.enriclop.kpopbot.servicio.CardService;
import com.enriclop.kpopbot.servicio.UserService;
import com.enriclop.kpopbot.twitchConnection.TwitchConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CardViewController {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @Autowired
    TwitchConnection twitchConnection;

    @GetMapping ("/photoSpawn")
    public String pokemonSpawn() {
        return "photocards/spawn";
    }
    @GetMapping("/battle")
    public String battle() {
        return "photocards/combat";
    }
}
