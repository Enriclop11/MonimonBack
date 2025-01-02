package com.enriclop.kpopbot.dto;

import com.enriclop.kpopbot.enums.Types;
import com.enriclop.kpopbot.modelo.Idol;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomCardDTO {
        private String idolId;
        private String customPhoto;
        private int rarity;

        private Integer customPopularity;
        private Integer customAttack;
        private Integer customDefense;
        private Integer customHealth;

        private String customType1;
        private String customType2;

        public CustomCardDTO() {
        }

        public CustomCardDTO(String idolId, String customPhoto, int rarity) {
            this.idolId = idolId;
            this.customPhoto = customPhoto;
            this.rarity = rarity;
        }

        public Idol setCustomPropertiesIdol(Idol idol) {
            if (customPopularity != null) idol.setPopularity(customPopularity);
            if (customType1 != null) idol.setType(Types.valueOf(customType1));
            if (customType2 != null) idol.setType2(Types.valueOf(customType2));
            return idol;
        }

        public PhotoCard setCustomPropertiesCard(PhotoCard photoCard) {

            if (customAttack != null) photoCard.setAttack(customAttack);
            if (customDefense != null) photoCard.setDefense(customDefense);
            if (customHealth != null) photoCard.setHp(customHealth);

            return photoCard;
        }
}
