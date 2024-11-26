package com.enriclop.kpopbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IdolDTO {
    @JsonProperty("ID")
    private String id;
    private String name;
    private String fullName;
    private String apiName;
    private String group;
    private String type;
    private String type2;
    private int popularity;

    public IdolDTO() {
    }
}
