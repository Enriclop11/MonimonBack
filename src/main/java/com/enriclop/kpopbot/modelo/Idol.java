package com.enriclop.kpopbot.modelo;

import com.enriclop.kpopbot.dto.IdolDTO;
import com.enriclop.kpopbot.enums.Types;
import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@Entity
@Table(name = "idols")
@Data
public class Idol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String fullName;
    private String apiName;
    private String band;
    @Enumerated(EnumType.STRING)
    private Types type;
    @Enumerated(EnumType.STRING)
    private Types type2;
    private int popularity;
    private boolean isActive;

    public Idol() {
        this.isActive = true;
    }

    public Idol(IdolDTO idolDTO) {
        this.id = idolDTO.getId();
        this.name = idolDTO.getName();
        this.fullName = idolDTO.getFullName();
        this.apiName = idolDTO.getApiName();
        this.band = idolDTO.getGroup();
        try {
            this.type = Types.valueOf(Objects.equals(idolDTO.getType(), "") ? "NONE" : idolDTO.getType());
        } catch (IllegalArgumentException e) {
            log.error("Error parsing type: " + idolDTO.getType());
            this.type = Types.NONE;
        }
        try {
            this.type2 = Types.valueOf(Objects.equals(idolDTO.getType2(), "") ? "NONE" : idolDTO.getType2());
        } catch (IllegalArgumentException e) {
            log.error("Error parsing type2: " + idolDTO.getType2());
            this.type2 = Types.NONE;
        }
        this.popularity = idolDTO.getPopularity();
        this.isActive = true;
    }
}
