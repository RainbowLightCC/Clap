package cn.funnymc.actions;

import cn.funnymc.occupations.*;

public class SpecialDefend {
	//TODO: getter,setter,private,javadocs...
	public String name;
	public Player me;
	public SpecialDefend(String name, Player me) {
		this.name=name;
		this.me=me;
	}
	/**
	 * ʹ��ʱִ��
	 */
	public void onExecuted() {
		
	}
	/**
	 * ������ʱִ��
	 * @param othersAttack - ���˵Ĺ����б�
	 * @param othersLength -���˵Ĺ����б�����
	 * @return �Ƿ��лغ�
	 */
	public boolean onAttacked(Attack[] othersAttack, int othersLength, Player otherPlayer) {
		return false;
	}
}