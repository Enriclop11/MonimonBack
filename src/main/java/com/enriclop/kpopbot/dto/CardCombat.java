package com.enriclop.kpopbot.dto;

import com.enriclop.kpopbot.modelo.PhotoCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardCombat extends PhotoCard {

    int currentHp;

    public CardCombat(PhotoCard photoCard) {
        super(photoCard);
        currentHp = photoCard.getHp();
    }

    @Override
    public String toString() {
        return '{' +
                "\"id\":" + getId() +
                ", \"name\":\"" + getName() + '\"' +
                ", \"photo\":\"" + getPhoto() + '\"' +
                ", \"hp\":" + getHp() +
                ", \"currentHp\":" + currentHp +
                ", \"atk\":" + getAttack() +
                ", \"def\":" + getDefense() +
                ", \"type\":\"" + getType() + '\"' +
                ", \"type2\":\"" + getType2() + '\"' +
                '}';
    }
}
