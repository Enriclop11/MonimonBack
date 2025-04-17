package com.enriclop.kpopbot.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class BoosterPackDTO {
    private int id;
    private String name;
    private String image;
    private double price;
    private List<Integer> idolIds;
    private List<CustomCardDTO> customCards;

}
