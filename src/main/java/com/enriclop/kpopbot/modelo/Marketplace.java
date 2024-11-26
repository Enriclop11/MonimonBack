package com.enriclop.kpopbot.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "marketplace")
@Data
public class Marketplace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "card_id")
    private PhotoCard card;

    private int price;


    public Marketplace() {
    }

}
