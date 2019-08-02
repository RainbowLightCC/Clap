package cn.funnymc.actions;
/**
 * 攻击招式
 * @author 滑稽神
 */
public class Attack {
	//TODO 给参数设成private并添加getter/setter
	public String name,attribute,type,sender;
	public int useB,harm,defend;
	/**
	 * 初始化攻击招式
	 * @param name 名字
	 * @param attribute 属性 Physics,Wild,Lightning,Fire,Explosion
	 * @param type 类型 Physics,Magic,Special
	 * @param sender 发送者
	 * @param useB 用饼
	 * @param harm 伤害值
	 * @param defend 抵消值
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
