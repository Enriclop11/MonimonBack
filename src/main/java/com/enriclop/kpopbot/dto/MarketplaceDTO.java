package com.enriclop.kpopbot.dto;

import com.enriclop.kpopbot.enums.Types;
import com.enriclop.kpopbot.utilities.Utilities;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketplaceDTO {
    private int id;
    private int idolID;
    private String name;
    private String apiName;
    private String fullName;
    private String band;
    private String photo;
    private int hp;
    private int defense;
    private int attack;
    private String type;
    private String type2;
    private int popularity;
    private int price;
    private int ownerId;
    private String owner;

    public MarketplaceDTO(int id, int idolID, String name, String apiName, String fullName, String band, String photo, int hp, int defense, int attack, Types type, Types type2, int popularity, int price, int ownerId, String ownerUsername) {
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
        this.type = type.getDisplayName();
        if (type2 != null) {
            this.type2 = type2.getDisplayName();
        } else {
            this.type2 = String.valueOf(Types.NONE);
        }
        this.popularity = popularity;
        this.price = price;
        this.ownerId = ownerId;
        this.owner = Utilities.firstLetterToUpperCase(ownerUsername);
    }
}