package com.enriclop.kpopbot.twitchConnection.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prices {

    private int superballPrice = 100;
    private int ultraballPrice = 300;
    private int masterballPrice =  5000;



}
