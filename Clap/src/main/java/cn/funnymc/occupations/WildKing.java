package cn.funnymc.occupations;

import cn.funnymc.actions.Attack;

import java.util.HashSet;
import java.util.Set;

public class WildKing extends ImmuneMan{

    public WildKing(int health, String name) {
        super(health, name);
        Set<String> immuneSet=new HashSet<>();
        immuneSet.add("Wild");
        super.setImmuneSet(immuneSet);
    }
    
    @Override
    public Attack[] getAttackList() {
        return new Attack[] {sa,tin,arrows,
                new Attack("南蛮","Wild","Magic", null, name,2,3,3),
                lightning,fire,explosion};
    }
}
