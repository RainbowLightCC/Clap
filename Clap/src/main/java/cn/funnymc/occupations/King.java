package cn.funnymc.occupations;

import cn.funnymc.game.Player;

public class King extends UnemployedMan{

    public King(String name, Player player) {
        super(name,player);
        this.health=15;
    }

    @Override
    public String getOccupationName() {return "国王";}
}
