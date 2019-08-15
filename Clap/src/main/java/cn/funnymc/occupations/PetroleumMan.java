package cn.funnymc.occupations;

import cn.funnymc.actions.Attack;

import java.util.HashSet;
import java.util.Set;

public class PetroleumMan extends ImmuneMan{

    public PetroleumMan(int health, String name) {
        super(health, name);
        Set<String> immuneSet=new HashSet<>();
        immuneSet.add("Physics");
        super.setImmuneSet(immuneSet);
    }
    
    @Override
    protected boolean doHarm(Attack attack){
        if(attack.attribute.equals("Fire")){
            this.health-=attack.harm*2;
            return true;
        }else if(attack.attribute.equals("Wild")){
            this.health-=attack.harm-1;
            return true;
        }
        return super.doHarm(attack);
    }
}
