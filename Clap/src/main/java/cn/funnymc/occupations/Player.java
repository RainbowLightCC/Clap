package cn.funnymc.occupations;

import cn.funnymc.actions.*;
/**
 * ԭ����
 * @author ������
 */
public class Player {
	private int health=6,biscuits=0;
	private String name="";
	/**
	 * �������ֵ
	 * @return ����ֵ
	 */
	public int getHealth() {return this.health;}
	/**
	 * ��������
	 * @return �����
	 */
	public String getName() {return this.name;}
	/**
	 * ��ñ���
	 * @return ����
	 */
	public int getBiscuits() {return this.biscuits;}
	public void setBiscuits(int biscuits) {this.biscuits=biscuits;}
	/**
	 * ���ְҵ��
	 * @return ְҵ��
	 */
	public String getOccupationName() {return "ԭ����";}
	/**
	 * ����һ��BasicMan(ԭ����)
	 * @param health - ����ֵ
	 * @param name - �����
	 */
	public Player(int health, String name) {
		this.health=health;
		this.name=name;
	}
	/*====================
	    ������������Ϸ�ڷ����ķָ���
	  ====================*/
	//Attack ����
	protected Attack
		sa=new Attack("sa","Physics","Physics",this.name,1,1,3),
		tin=new Attack("tin","Physics","Physics",this.name,1,3,1),
		arrows=new Attack("���","Physics","Magic",this.name,2,2,2),
		wildForce=new Attack("����","Wild","Magic",this.name,3,3,3),
		lightning=new Attack("����","Lightning","Magic",this.name,4,4,4),
		fire=new Attack("����","Fire","Magic",this.name,5,5,5),
		explosion=new Attack("�˱�","Explosion","Magic",this.name,6,6,6);
	
	//Defend ����
	protected Defend
		biscuit=new Defend(false,false,false,false,true,"��",-1),
		defendPhysics=new Defend(true,false,false,false,false,"��",0),
		defendWild=new Defend(false,true,false,false,false,"����",0),
		defendLightning=new Defend(false,false,true,false,false,"����",0),
		defendFire=new Defend(false,false,false,true,false,"����",0);
	
	/**
	 * ��ù����б�
	 * @return �����б�
	 */
	public Attack[] getAttackList() {
		return new Attack[] {sa,tin,arrows,wildForce,lightning,fire,explosion};
	}
	/**
	 * ��÷����б�
	 * @return �����б�
	 */
	public Defend[] getDefendList() {
		return new Defend[] {biscuit,defendPhysics,defendWild,defendLightning,defendFire};
	}
	/**
	 * ��ʼ��
	 * ��ʲôҲ������
	 */
	public void init() {
		
	}
	
	/**
	 * ��鱬��
	 * @return �Ƿ񱬵�
	 * @param mine �ҵĹ����б�
	 */
	public boolean checkAfterInput(Attack[] mine,int mineLength) {
		for(int i=0;i<mineLength;i++) {
			this.biscuits-=mine[i].useB;
		}
		if(this.getBiscuits()<0) {
			if(this.health>0)this.health=0;
			else this.health=-60;
			return true;
		}
		return false;
	}
	/**
	 * ��鱬��
	 * @param mine �ҵķ���
	 * @return �Ƿ񱬵�
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
	 * ������������
	 * @param mineAttack - ���������б�
	 * @param othersAttack - �Է������б�
	 * @return �Ƿ��лغ�
	 */
	public boolean onCounteract(Attack[] mineAttack,Attack[] othersAttack,int mineLength,int othersLength) {
		Attack[] mine=mineAttack.clone(),others=othersAttack.clone();
		int minePos=0,othersPos=0;
		boolean endRound=false;
		while(minePos<mineLength&&othersPos<othersLength) {
			//��ͬ�����
			if(mine[minePos].defend==others[othersPos].defend) {
				minePos++;othersPos++;
				continue;
			}
			//sa����tintin
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
			//sa����tin
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
			//����ֵ���
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
	 * �Լ���ס����
	 * @param mine �ҵķ���
	 * @param othersAttack �Է��Ĺ����б�
	 * @return �Ƿ��лغ�
	 */
	public boolean onDefend(Defend mine,Attack[] othersAttack,int othersLength) {
		Attack[] others=othersAttack.clone();
		boolean endRound=false;
		for(int i=0;i<othersLength;i++) {
			if((others[i].attribute.equals("Physics")&&!mine.a_Physics)||
				(others[i].attribute.equals("Wild")&&!mine.a_Wild)||
				(others[i].attribute.equals("Lightning")&&!mine.a_Lightning)||
				(others[i].attribute.equals("Fire")&&!mine.a_Fire)||
				(others[i].attribute.equals("Explosion")&&!mine.a_Explosion)
					) {
				endRound=true;
				this.health-=others[i].harm;
			}
		}
		return endRound;
	}
	/**
	 * �лغϺ���
	 * @return ��û��ȥ��
	 */
	public boolean checkAfterRound() {
		this.biscuits=0;
		return this.health<0;
	}
}
