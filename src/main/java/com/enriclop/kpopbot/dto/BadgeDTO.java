package com.enriclop.kpopbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BadgeDTO {
    private String name;
    private String description;
    private String image;
    private String[] requirements;
    private int rarity;

    public BadgeDTO() {
    }
}
