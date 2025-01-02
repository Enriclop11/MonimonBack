package com.enriclop.kpopbot.modelo;

import com.enriclop.kpopbot.enums.Types;
import com.enriclop.kpopbot.utilities.Utilities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "photocards")
@Data
public class PhotoCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Enumerated(EnumType.STRING)
    private Types type;

    @Enumerated(EnumType.STRING)
    private Types type2;

    private int popularity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToOne(mappedBy = "card")
    @JsonIgnore
    private Marketplace marketplace;

    public PhotoCard() {
    }

    public PhotoCard(PhotoCard photoCard) {
        this.idolID = photoCard.getIdolID();
        this.name = photoCard.getName();
        this.fullName = photoCard.getFullName();
        this.band = photoCard.getBand();
        this.photo = photoCard.getPhoto();
        this.type = photoCard.getType();
        this.type2 = photoCard.getType2();
        this.hp = photoCard.getHp();
        this.defense = photoCard.getDefense();
        this.attack = photoCard.getAttack();
        this.popularity = photoCard.getPopularity();
    }

    public PhotoCard(Idol idolDTO, String photo) {
        this.idolID = idolDTO.getId();
        this.name = idolDTO.getName();
        this.fullName = idolDTO.getFullName();
        this.apiName = idolDTO.getApiName();
        this.band = idolDTO.getBand();
        this.photo = photo;
        this.popularity = idolDTO.getPopularity();

        this.type = idolDTO.getType();
        this.type2 = idolDTO.getType2();

        //Calculate the random stats with the popularity of the idol
        int popularity = idolDTO.getPopularity();

        this.hp = (int) (Math.random() * (500 + popularity * 10)) + 500;
        this.attack = (int) (Math.random() * (50 + popularity)) + 20;
        this.defense = (int) (Math.random() * (50 + popularity)) + 20;
    }

    public PhotoCard(String name, String fullName, String band, String photo, Types type, Types type2) {
        this.name = name;
        this.fullName = fullName;
        this.band = band;
        this.photo = photo;
        this.type = type;
        this.type2 = type2;
    }

    public PhotoCard(String name, String fullName, String band, String photo, Types type, Types type2, User user) {
        this.name = name;
        this.fullName = fullName;
        this.band = band;
        this.photo = photo;
        this.type = type;
        this.type2 = type2;
        this.user = user;
    }

    @Override
    public String toString() {
        return '{' +
                "\"id\":" + id +
                ", \"name\":\"" + name + '\"' +
                ", \"fullName\":\"" + fullName + '\"' +
                ", \"type\":\"" + type + '\"' +
                ", \"type2\":\"" + type2 + '\"' +
                ", \"photo\":\"" + photo + '\"' +
                ", \"group\":\"" + band + '\"' +
                '}';
    }

    public String getDisplayName() {
        return Utilities.firstLetterToUpperCase(this.name);
    }
}
