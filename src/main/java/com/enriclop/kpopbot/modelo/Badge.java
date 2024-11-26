package com.enriclop.kpopbot.modelo;


import com.enriclop.kpopbot.dto.BadgeDTO;
import com.enriclop.kpopbot.enums.Rarity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "badges")
@Data
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String name;

    private String description;

    private String image;

    @Enumerated(EnumType.STRING)
    private Rarity rarity;

    public Badge() {
    }

    public Badge(String name, String description, String image, Rarity rarity) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.rarity = rarity;
    }

    public Badge (BadgeDTO badgeDTO) {
        this.name = badgeDTO.getName();
        this.description = badgeDTO.getDescription();
        this.image = badgeDTO.getImage();
        this.rarity = Rarity.valueOf(String.valueOf(badgeDTO.getRarity()));
    }

}
