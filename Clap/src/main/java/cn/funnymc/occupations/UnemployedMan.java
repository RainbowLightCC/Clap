package cn.funnymc.occupations;

import cn.funnymc.actions.Attack;
import cn.funnymc.actions.Defend;

import java.util.List;

/**
 * 原版人
 * @author 滑稽神
 */
public class UnemployedMan {
	private int health,biscuits=0;
	private String name="";
	/**
	 * 获得生命值
	 * @return 生命值
	 */
	public int getHealth() {return this.health;}
	/**
	 * 获得玩家名
	 * @return 玩家名
	 */
	public String getName() {return this.name;}
	/**
	 * 获得饼数
	 * @return 饼数
	 */
	public int getBiscuits() {return this.biscuits;}
	public void setBiscuits(int biscuits) {this.biscuits=biscuits;}
	/**
	 * 获得职业名
	 * @return 职业名
	 */
	public String getOccupationName() {return "原版人";}
	/**
	 * 生成一个BasicMan(原版人)
	 * @param health - 生命值
	 * @param name - 玩家名
	 */
	public UnemployedMan(int health, String name) {
		this.health=health;
		this.name=name;
	}
	/*====================
	    基础方法和游戏内方法的分割线
	  ====================*/
	//Attack 攻击
	private Attack
		sa=new Attack("sa","Physics","Physics", null, this.name,1,1,3),
		tin=new Attack("tin","Physics","Physics", null, this.name,1,3,1),
		arrows=new Attack("万箭","Physics","Magic", null, this.name,2,2,2),
		wildForce=new Attack("南蛮","Wild","Magic", null, this.name,3,3,3),
		lightning=new Attack("闪电","Lightning","Magic", null, this.name,4,4,4),
		fire=new Attack("火舞","Fire","Magic", null, this.name,5,5,5),
		explosion=new Attack("核爆","Explosion","Magic", null, this.name,6,6,6);
	
	//Defend 防御
	private Defend
		biscuit=new Defend(false,false,false,false,true,"饼",-1),
		defendPhysics=new Defend(true,false,false,false,false,"防",0),
		defendWild=new Defend(false,true,false,false,false,"防南",0),
		defendLightning=new Defend(false,false,true,false,false,"防闪",0),
		defendFire=new Defend(false,false,false,true,false,"防火",0);
	
	/**
	 * 获得攻击列表
	 * @return 攻击列表
	 */
	public Attack[] getAttackList() {
		return new Attack[] {sa,tin,arrows,wildForce,lightning,fire,explosion};
	}
	/**
	 * 获得防御列表
	 * @return 防御列表
	 */
	public Defend[] getDefendList() {
		return new Defend[] {biscuit,defendPhysics,defendWild,defendLightning,defendFire};
	}
	/**
	 * 初始化
	 * （什么也不做）
	 */
	public void init() {
		
	}
	
	/**
	 * 检查爆点
	 * @return 是否爆点
	 * @param mine 我的攻击列表
	 */
	public boolean checkAfterInput(List<Attack> mine) {
		for(Attack i:mine) {
			this.biscuits-=i.useB;
		}
		if(this.getBiscuits()<0) {
			if(this.health>0)this.health=0;
			else this.health=-60;
			return true;
		}
		return false;
	}
	/**
	 * 检查爆点
	 * @param mine 我的防御
	 * @return 是否爆点
	 */
	public boolean checkAfterInput(Defend mine) {
		this.biscuits-=mine.useB;
		if(this.getBiscuits()<0) {
			this.health=(this.health==0?0:-60);
			return true;
		}
		return false;
	}
	/**
	 * 两方攻击抵消
	 * @param mineAttack - 己方攻击列表
	 * @param othersAttack - 对方攻击列表
	 * @return 是否切回合
	 */
	public boolean onCounteract(List<Attack> mineAttack,List<Attack> othersAttack) {
		//吃屎了，要改这么多
		//TODO 下面两行是急救措施 以后再改
		Attack[] mine= (Attack[]) mineAttack.toArray(),others= (Attack[]) othersAttack.toArray();
		int mineLength=mineAttack.size(),othersLength=othersAttack.size();
		int minePos=0,othersPos=0;
		boolean endRound=false;
		while(minePos<mineLength&&othersPos<othersLength) {
			//相同则抵消
			if(mine[minePos].defend==others[othersPos].defend) {
				minePos++;othersPos++;
				continue;
			}
			//sa遇到tintin
			if(othersLength-othersPos>=2&&mine[minePos].name.equals("sa")
					&&others[othersPos].name.equals("tin")&&others[othersPos+1].name.equals("tin")) {
				minePos++;othersPos+=2;
				continue;
			}
			if(mineLength-minePos>=2&&others[othersPos].name.equals("sa")
					&&mine[minePos].name.equals("tin")&&mine[minePos+1].name.equals("tin")) {
				minePos+=2;othersPos++;
				continue;
			}
			//sa遇到tin
			if(mine[minePos].name.equals("sa")&&others[othersPos].name.equals("tin")) {
				this.health++;
				endRound=true;
				minePos++;othersPos++;
				continue;
			}
			if(mine[minePos].name.equals("tin")&&others[othersPos].name.equals("sa")) {
				this.health--;
				endRound=true;
				minePos++;othersPos++;
				continue;
			}
			//抵消值相比
			if(others[othersPos].defend<mine[minePos].defend) {
				if(mine[minePos].harm-others[othersPos].defend>0) {
					mine[minePos].harm-=others[othersPos].defend;
					mine[minePos].defend-=others[othersPos].defend;
					othersPos++;
					continue;
				}
				minePos++;othersPos++;
				continue;
			}
			if(mine[minePos].defend<others[othersPos].defend) {
				if(others[othersPos].harm-mine[minePos].defend>0) {
					others[othersPos].harm-=mine[minePos].defend;
					others[othersPos].defend-=mine[minePos].defend;
					minePos++;
					continue;
				}
				minePos++;othersPos++;
				continue;
			}
		}
		if(othersPos<othersLength) {
			endRound=true;
			for(int i=othersPos;i<othersLength;i++) {
				this.health-=others[othersPos].harm;
			}
		}
		return endRound;
	}
	/**
	 * 自己防住别人
	 * @param mine 我的防御
	 * @param others 对方的攻击列表
	 * @return 是否切回合
	 */
	public boolean onDefend(Defend mine,List<Attack> others) {
		boolean endRound=false;
		for(Attack i:others) {
			if((i.attribute.equals("Physics")&&!mine.a_Physics)||
				(i.attribute.equals("Wild")&&!mine.a_Wild)||
				(i.attribute.equals("Lightning")&&!mine.a_Lightning)||
				(i.attribute.equals("Fire")&&!mine.a_Fire)||
				(i.attribute.equals("Explosion")&&!mine.a_Explosion)
					) {
				endRound=true;
				this.health-=i.harm;
			}
		}
		return endRound;
	}
	/**
	 * 切回合后检查
	 * @return true if 去世
	 */
	public boolean checkAfterRound() {
		this.biscuits=0;
		return this.health<0;
	}
}
