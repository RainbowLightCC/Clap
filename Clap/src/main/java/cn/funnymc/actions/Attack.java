package cn.funnymc.actions;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 攻击招式
 * @author 滑稽神
 */
public class Attack {
	//TODO 给参数设成private并添加getter/setter
	@JSONField(name="action")
	public String name;
	@JSONField(name="attribute")
	public String attribute;
	@JSONField(name="type")
	public String type;
	@JSONField(name="to")
	private String to;
	public String sender;
	public int useB,harm,defend;
	/**
	 * 初始化攻击招式
	 * @param name 名字
	 * @param attribute 属性 Physics,Wild,Lightning,Fire,Explosion
	 * @param type 类型 Physics,Magic,Special
	 * @param to 被发送者
	 * @param sender 发送者
	 * @param useB 用饼
	 * @param harm 伤害值
	 * @param defend 抵消值
	 */
	public Attack(String name, String attribute, String type, String to, String sender, int useB, int harm, int defend) {
		this.name=name;
		this.attribute=attribute;
		//Physics,Wild,Lightning,Fire,Explosion
		this.type=type;
		//Physics,Magic,Special
		this.to = to;
		this.sender=sender;
		this.useB=useB;
		this.harm=harm;
		this.defend=defend;
	}
	public Attack(Attack a) {
		this.name=a.name;
		this.attribute=a.attribute;
		//Physics,Wild,Lightning,Fire,Explosion
		this.type=a.type;
		//Physics,Magic,Special
		this.to = a.to;
		this.sender=a.sender;
		this.useB=a.useB;
		this.harm=a.harm;
		this.defend=a.defend;
	}
	public Attack setTo(String to) {
		this.to = to;
		return this;
	}

	public String getTo() {
		return to;
	}
}
