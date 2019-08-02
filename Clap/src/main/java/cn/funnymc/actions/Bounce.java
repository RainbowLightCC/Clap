package cn.funnymc.actions;

import cn.funnymc.occupations.*;

public class Bounce extends SpecialDefend {

	public Bounce(Player me) {
		super("��",me);
	}
	
	@Override
	public void onExecuted() {
		if(me.getBiscuits()>0) {
			me.setBiscuits(0);
		}
		else {
			me.setBiscuits(-60);
		}
	}
	
	@Override
	public boolean onAttacked(Attack[] othersAttack, int othersLength, Player otherPlayer) {
		
		return false;
	}
}