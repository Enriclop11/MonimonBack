package com.enriclop.kpopbot.modelo;


import com.enriclop.kpopbot.dto.BadgeDTO;
import com.enriclop.kpopbot.enums.Rarity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "badges")
@Data
public class Badge {

    @Id
    private int id;

    @ManyToMany(mappedBy = "badges")
    private List<User> users;

    private String name;

    private String description;

    private String image;

    @ElementCollection
    private List<String> requirements;

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
        this.id = badgeDTO.getId();
        this.name = badgeDTO.getName();
        this.description = badgeDTO.getDescription();
        this.image = badgeDTO.getImage();
        this.rarity = Rarity.fromValue(badgeDTO.getRarity());
        this.requirements = Arrays.asList(badgeDTO.getRequirements());
    }

}
