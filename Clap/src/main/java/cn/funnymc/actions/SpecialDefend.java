package cn.funnymc.actions;

import cn.funnymc.occupations.UnemployedMan;

public class SpecialDefend {
	//TODO: getter,setter,private,javadocs...
	public String name;
	public UnemployedMan me;
	public SpecialDefend(String name, UnemployedMan me) {
		this.name=name;
		this.me=me;
	}
	/**
	 * 使用时执行
	 */
	public void onExecuted() {
		
	}
	/**
	 * 被攻击时执行
	 * @param othersAttack - 别人的攻击列表
	 * @param othersLength -别人的攻击列表长度
	 * @return 是否切回合
	 */
	public boolean onAttacked(Attack[] othersAttack, int othersLength, UnemployedMan otherPlayer) {
		return false;
	}
}
