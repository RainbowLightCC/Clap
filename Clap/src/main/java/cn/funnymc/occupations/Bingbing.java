package cn.funnymc.occupations;

import cn.funnymc.actions.Attack;
import cn.funnymc.actions.Defend;
import cn.funnymc.game.Player;

import java.util.List;

public class Bingbing extends UnemployedMan{

    public Bingbing(String name, Player player) {
        super(name,player);
    }

    @Override
    public boolean onDefend(Defend mine, List<Attack> others) {
        boolean endRound=false;
        for(Attack i:others) {
            if((i.attribute.equals("Physics")&&!mine.a_Physics)||
                    (i.attribute.equals("Arrows")&&!mine.a_Arrows)||
                    (i.attribute.equals("Wild")&&!mine.a_Wild)||
                    (i.attribute.equals("Lightning")&&!mine.a_Lightning)||
                    (i.attribute.equals("Fire")&&!mine.a_Fire)||
                    (i.attribute.equals("Explosion")&&!mine.a_Explosion)
            ) {
                if(doHarm(i)) endRound=true;
            }else{
                this.biscuits+=i.useB;
            }
        }
        return endRound;
    }

    @Override
    public String getOccupationName() {return "饼饼";}
}
