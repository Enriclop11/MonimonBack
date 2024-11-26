package com.enriclop.kpopbot.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "items")
@Data
public class Items {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int superball;

    private int ultraball;

    private int masterball;

    public Items() {
        this.superball = 0;
        this.ultraball = 0;
        this.masterball = 0;
    }

    public void useSuperball() {
        this.superball -= 1;
    }

    public void useUltraball() {
        this.ultraball -= 1;
    }

    public void useMasterball() {
        this.masterball -= 1;
    }

    public void addSuperball() {
        this.superball += 1;
    }

    public void addUltraball() {
        this.ultraball += 1;
    }

    public void addMasterball() {
        this.masterball += 1;
    }

    public void addSuperball(int amount) {
        this.superball += amount;
    }

    public void addUltraball(int amount) {
        this.ultraball += amount;
    }

    public void addMasterball(int amount) {
        this.masterball += amount;
    }

}
