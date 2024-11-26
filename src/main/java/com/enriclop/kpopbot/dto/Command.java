package com.enriclop.kpopbot.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Command {

    String name;

    String customName;

    @Nullable
    boolean active;

    Command() {
    }

    public Command(String name, boolean active) {
        this.name = name;
        this.customName = name;
        this.active = active;
    }


}
