package cn.funnymc.occupations;

import cn.funnymc.actions.Attack;
import cn.funnymc.game.Player;

import java.util.HashSet;
import java.util.Set;

public class Hercules extends UnemployedMan{
    private Set<String> immuneSet=new HashSet<>();
    public Hercules(String name, Player player){
        super(name,player);
        health=8;
    }

    @Override
    protected boolean doHarm(Attack attack){
        if(immuneSet.contains(attack.name))return false;
        this.health-=attack.harm;
        immuneSet.add(attack.name);
        return true;
    }

    @Override
    public String getOccupationName() {
        return "赫拉克勒斯";
    }
}
