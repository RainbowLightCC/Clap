package cn.funnymc.actions;
/**
 * ������ʽ
 * @author ������
 */
public class Attack {
	//TODO ���������private�����getter/setter
	public String name,attribute,type,sender;
	public int useB,harm,defend;
	/**
	 * ��ʼ��������ʽ
	 * @param name ����
	 * @param attribute ���� Physics,Wild,Lightning,Fire,Explosion
	 * @param type ���� Physics,Magic,Special
	 * @param sender ������
	 * @param useB �ñ�
	 * @param harm �˺�ֵ
	 * @param defend ����ֵ
	 */
	public Attack(String name,String attribute,String type,String sender,int useB,int harm,int defend) {
		this.name=name;
		this.attribute=attribute;
		//Physics,Wild,Lightning,Fire,Explosion
		this.type=type;
		//Physics,Magic,Special
		this.sender=sender;
		this.useB=useB;
		this.harm=harm;
		this.defend=defend;
	}
	
}
