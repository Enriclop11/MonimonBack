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

    public static Rarity fromValue(int value) {
        for (Rarity rarity : Rarity.values()) {
            if (rarity.getValue() == value) {
                return rarity;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }
}