package cn.funnymc.actions;

public class Defend {
	//TODO: 设置private,getter,setter,javadoc
	public boolean a_Physics,a_Wild,a_Lightning,a_Fire,a_Explosion;
	public String name;
	public int useB;
	public Defend(boolean a_Physics,boolean a_Wild,boolean a_Lightning,boolean a_Fire,boolean a_Explosion,String name,int useB) {
		this.a_Physics=a_Physics;
		this.a_Wild=a_Wild;
		this.a_Lightning=a_Lightning;
		this.a_Fire=a_Fire;
		this.a_Explosion=a_Explosion;
		this.name=name;
		this.useB=useB;
	}
	public final static Defend empty=new Defend(false,false,false,false,false,"空",0);
}
