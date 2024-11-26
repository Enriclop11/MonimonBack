package com.enriclop.kpopbot.enums;

import lombok.Getter;

@Getter
public enum Types {
    LEADER("Leader"),
    MAIN_VOCAL("Main Vocal"),
    LEAD_VOCAL("Lead Vocal"),
    SUB_VOCAL("Sub Vocal"),
    MAIN_RAPPER("Main Rapper"),
    MAIN_DANCER("Main Dancer"),
    CENTER("Center"),
    VISUAL("Visual"),
    UNNIE("Unnie"),
    MAKNAE("Maknae"),
    SOLOIST("Soloist"),
    NONE("None");

    private final String displayName;

    Types(String displayName) {
        this.displayName = displayName;
    }

}