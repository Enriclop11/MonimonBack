package com.enriclop.kpopbot.dto;

import com.enriclop.kpopbot.enums.Types;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketplaceDTO {
    private int id;
    private String idolID;
    private String name;
    private String apiName;
    private String fullName;
    private String band;
    private String photo;
    private int hp;
    private int defense;
    private int attack;
    private Types type;
    private Types type2;
    private int popularity;
    private int price;
    private int ownerId;
    private String owner;

    public MarketplaceDTO(int id, String idolID, String name, String apiName, String fullName, String band, String photo, int hp, int defense, int attack, Types type, Types type2, int popularity, int price, int ownerId, String ownerUsername) {
        this.id = id;
        this.idolID = idolID;
        this.name = name;
        this.apiName = apiName;
        this.fullName = fullName;
        this.band = band;
        this.photo = photo;
        this.hp = hp;
        this.defense = defense;
        this.attack = attack;
        this.type = type;
        this.type2 = type2;
        this.popularity = popularity;
        this.price = price;
        this.ownerId = ownerId;
        this.owner = ownerUsername;
    }
}