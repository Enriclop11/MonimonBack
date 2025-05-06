package com.enriclop.kpopbot.kpopDB;

import com.enriclop.kpopbot.enums.Types;

public class TableType {

    private static final double[][] table = {
            //Leader, Main Vocal, Lead Vocal, Sub Vocal, Main Rapper, Main Dancer, Center, Visual, Unnie, Maknae, Soloist
            {2, 1, 1, 1, 1, 1, 0.5, 0.5, 1, 1, 1}, //Leader
            {1, 1, 2, 2, 1, 2, 1, 1, 1, 1, 1}, //Main Vocal
            {1, 0.5, 1, 2, 1, 1, 1, 1, 1, 1, 1}, //Lead Vocal
            {1, 0.5, 1, 1, 1, 0.5, 1, 1, 1, 1, 1}, //Sub Vocal
            {1, 2, 1, 1, 2, 0.5, 1, 1, 1, 1, 1}, //Main Rapper
            {1, 0.5, 0.5, 1, 2, 1, 2, 2, 1, 1, 1}, //Main Dancer
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //Center
            {0, 0.5, 1, 1, 1, 1, 2, 1, 1, 1, 1}, //Visual
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1}, //Unnie
            {1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1}, //Maknae
            {2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 2}  //Soloist
    };

    public static double modifierAgainst(Types attackType, Types defenseType) {
        if (attackType == Types.NONE || defenseType == Types.NONE || attackType == null || defenseType == null) {
            return 1;
        }

        int attackIndex = attackType.ordinal();
        int defenseIndex = defenseType.ordinal();
        return table[attackIndex][defenseIndex];
    }

    public static void main(String[] args) {
        System.out.println(modifierAgainst(Types.MAIN_VOCAL, Types.LEAD_VOCAL));
    }

}
