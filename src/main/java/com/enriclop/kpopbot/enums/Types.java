package com.enriclop.kpopbot.enums;

import lombok.Getter;

@Getter
public enum Types {
    LEADER("Leader"),
    MAIN_VOCAL("Main Vocalist"),
    LEAD_VOCAL("Lead Vocalist"),
    SUB_VOCAL("Sub Vocalist"),
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

    public static Types fromString(String text) {
        for (Types type : Types.values()) {
            text = text.replace("-", " ");

            if (text.toLowerCase().contains(type.getDisplayName().toLowerCase())) {
                return type;
            }
        }
        return NONE;
    }

    public static Types randomType() {
        Types[] types = Types.values();
        int randomIndex = (int) (Math.random() * (types.length - 1));
        return types[randomIndex];
    }

}