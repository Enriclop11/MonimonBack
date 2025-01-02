package com.enriclop.kpopbot.dto;

import com.enriclop.kpopbot.modelo.PhotoCard;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PhotoCardDTO {

    private Integer id;

    private String idolID;

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

    public PhotoCardDTO(PhotoCard card) {
        this.id = card.getId() == null ? 0 : card.getId();
        this.idolID = card.getIdolID();
        this.name = card.getName();
        this.apiName = card.getApiName();
        this.fullName = card.getFullName();
        this.band = card.getBand();
        this.photo = card.getPhoto();
        this.hp = card.getHp();
        this.defense = card.getDefense();
        this.attack = card.getAttack();
        this.type = card.getType().getDisplayName();
        this.type2 = card.getType2().getDisplayName();
        this.popularity = card.getPopularity();
    }

    public static List<PhotoCardDTO> fromPhotoCards(List<PhotoCard> photoCards) {
        return photoCards.stream().map(PhotoCardDTO::new).toList();
    }
}
