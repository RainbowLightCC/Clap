package cn.funnymc.occupations;

import cn.funnymc.actions.Attack;

import java.util.Set;

/**
 * Abstract class of occupations that supports immune system.
 */
public abstract class ImmuneMan extends UnemployedMan{
    private Set<String> immuneSet;
    public ImmuneMan(String name){
        super(name);
    }
    
    @Override
    protected boolean doHarm(Attack attack){
        if(immuneSet.contains(attack.attribute))return false;
        this.health-=attack.harm;
        return true;
    }
    
    protected void setImmuneSet(Set<String> immuneSet) {
        this.immuneSet = immuneSet;
    }

    public Set<String> getImmuneSet() {
        return immuneSet;
    }
}
