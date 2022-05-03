package cn.funnymc.occupations;

import cn.funnymc.actions.Attack;
import cn.funnymc.game.Player;

public class Golem extends UnemployedMan{

    private boolean exploded=false,damaged=false;
    public Golem(String name, Player player) {
        super(name, player);
    }


    @Override
    public String getOccupationName() {return "戈仑";}

    @Override
    protected boolean doHarm(Attack attack) {
        damaged=true;
        return super.doHarm(attack);
    }

    @Override
    public void checkAfterRound() {
        if(damaged)health++;
        damaged=false;
        super.checkAfterRound();
        if(this.health<0&&!exploded){
            exploded=true;
            for(Player p:this.player.getGame().getPlayerList()){
                p.getClapper().health--;
            }
            health=0;
        }
    }
}
