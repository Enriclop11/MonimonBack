package com.enriclop.kpopbot.enums;

public enum Rarity {

    COMMON(1),
    UNCOMMON(2),
    RARE(3),
    EPIC(4),
    LEGENDARY(5),
    MYTHIC(6);

    private final int value;

    Rarity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}