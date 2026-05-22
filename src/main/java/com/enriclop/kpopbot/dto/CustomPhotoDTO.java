package com.enriclop.kpopbot.dto;

import com.enriclop.kpopbot.enums.Types;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomPhotoDTO {

    private String id;
    private String name;
    private String band;
    private int hp;
    private int defense;
    private int attack;
    private Types type;
    private Types type2;
    private int idolId;
    private String photo;
    private String fullName;
    private int popularity;

    public CustomPhotoDTO() {}

    public CustomPhotoDTO(String id, String name, String band, int hp, int defense, int attack,
                         Types type, Types type2, int idolId, String photo, String fullName, int popularity) {
        this.id = id;
        this.name = name;
        this.band = band;
        this.hp = hp;
        this.defense = defense;
        this.attack = attack;
        this.type = type;
        this.type2 = type2;
        this.idolId = idolId;
        this.photo = photo;
        this.fullName = fullName;
        this.popularity = popularity;
    }

    public CustomPhotoDTO(String id, String name, String band, int hp, int defense, int attack,
                          String type, String type2, int idolId, String photo, String fullName, int popularity) {
        this.id = id;
        this.name = name;
        this.band = band;
        this.hp = hp;
        this.defense = defense;
        this.attack = attack;
        this.type = Types.valueOf(type);
        this.type2 = Types.valueOf(type2);
        this.idolId = idolId;
        this.photo = photo;
        this.fullName = fullName;
        this.popularity = popularity;
    }

}
