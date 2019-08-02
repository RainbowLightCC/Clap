package cn.funnymc.game;

import cn.funnymc.occupations.*;

import java.util.HashMap;

import org.java_websocket.WebSocket;

import cn.funnymc.actions.*;

/**
 * TODO: remove "gameId"
 */
public class Game {
	public Player player1=null;
	public Player player2=null;
    public WebSocket conn1=null,conn2=null;
    public boolean isGameRunning=false,isGameCompleted=false,isDoingInput=false;
    public boolean isBounce1=false,isBounce2=false;
    public int attack1Length=0,attack2Length=0;
    private int gameId;
    public Attack[] attack1=new Attack[128],attack2=new Attack[128];
    public Defend defend1,defend2;
    public HashMap<String,Attack> attackMap1,attackMap2;
    public HashMap<String,Defend> defendMap1,defendMap2;
    public int getId() {return gameId;}
    private void end() {
    	GamesManager.end(this);
    }
	public void playerAttack(String playerName,String input) {
		if(playerName.equals(player1.getName())){
			attack1[attack1Length++]=attackMap1.get(input);
		}else if(playerName.equals(player2.getName())) {
			attack2[attack2Length++]=attackMap2.get(input);
		}
	}
	public void playerDefend(String playerName,String input) {
		if(playerName.equals(player1.getName())){
			defend1=defendMap1.get(input);
		}else if(playerName.equals(player2.getName())) {
			defend2=defendMap2.get(input);
		}
	}
	public Game(){
		//TODO
	}
	public void newPlayer(String name) {
		if(player1==null) {
			player1=new Player(6,name);
		}
		else if(player2==null) {
			player2=new Player(6,name);
		}
		if(player1!=null&&player2!=null) {
//			start();
		}
	}
	private void broadcast(String msg) {
		conn1.send(msg);
	}
	/**
     * ��������
     * @throws InterruptedException 
     */
    private void clap567() throws InterruptedException {
    	Thread.sleep(500);
    	broadcast("clap 567 ��");
    	Thread.sleep(500);
    	broadcast("clap 567 ��");
    	Thread.sleep(500);
    	broadcast("clap 567 ��");
    	Thread.sleep(500);
    	broadcast("clap 567 ��");
    }
    /**
     * �����������Ʊ�
     */
    private void buildHashMaps() {
    	attackMap1=new HashMap<String, Attack>();
    	for(Attack atk:player1.getAttackList()) attackMap1.put(atk.name,atk);
    	attackMap2=new HashMap<String, Attack>();
    	for(Attack atk:player2.getAttackList()) attackMap2.put(atk.name,atk);
    	defendMap1=new HashMap<String, Defend>();
    	for(Defend dfd:player1.getDefendList()) defendMap1.put(dfd.name,dfd);
    	defendMap2=new HashMap<String, Defend>();
    	for(Defend dfd:player2.getDefendList()) defendMap2.put(dfd.name,dfd);
    }
    public void start() {
    	Thread gameThread=new Thread(){
    		public void run() {
	    		try {
	    			isGameRunning=true;
	    			isGameCompleted=false;
	    			buildHashMaps();
					conn1.send("gameinfo start");
					conn2.send("gameinfo start");
					//��Ϸ����
					player1.init();
					player2.init();
					while(true) {//�غ�
						//������Ϸ ��
						if(!isGameRunning) {
							break;
						}
						//��Ѫ��
						broadcast("info * "+player1.getName()+" Ѫ����"+player1.getHealth());
						broadcast("info * "+player2.getName()+" Ѫ����"+player2.getHealth());
						if(player1.checkAfterRound()&&player2.checkAfterRound()) {
							broadcast("info ** �����˶�ȥ������ƽ���ˣ�");
							break;
						}
						else if(player1.checkAfterRound()) {
							broadcast("info ** "+player1.getName()+" ȥ���ˣ�");
							broadcast("info ** ��Ӯ�ң�"+player2.getName());
							break;
						}
						else if(player2.checkAfterRound()) {
							broadcast("info ** "+player2.getName()+" ȥ���ˣ�");
							broadcast("info ** ��Ӯ�ң�"+player1.getName());
							break;
						}
						clap567();//��������
						while(true) {//��
							boolean endRound=false;
							//�������
							attack1=new Attack[128];attack2=new Attack[128];
							attack1Length=0;attack2Length=0;
							defend1=null;defend2=null;
							isBounce1=false;isBounce2=false;
							//�㲥����
							broadcast("info * "+player1.getName()+" �� "+player1.getBiscuits()+" ����");
							broadcast("info * "+player2.getName()+" �� "+player2.getBiscuits()+" ����");
							//����
							broadcast("info ** ��ʼ������� **");
							conn1.send("gameinfo startinput");
							conn2.send("gameinfo startinput");
							isDoingInput=true;
							Thread.sleep(3000);
							conn1.send("gameinfo endinput");
							conn2.send("gameinfo endinput");
							isDoingInput=false;
							broadcast("info ** ���������� **");
							//�Զ�����
							if((!isBounce1)&&defend1==null&&attack1Length==0)defend1=defendMap1.get("��");
							if((!isBounce2)&&defend2==null&&attack2Length==0)defend2=defendMap2.get("��");
							//�㲥������
							if(isBounce1) {
								new Bounce(player1).onExecuted();
								broadcast("info * "+player1.getName()+" ���˵�");
							}
							else if(defend1!=null)broadcast("info * "+player1.getName()+" �����˷�����"+defend1.name);
							else {
								broadcast("info * "+player1.getName()+" �����˹�����");
								for(int i=0;i<attack1Length;i++) {
									broadcast("info - "+attack1[i].name);
								}
							}
							if(isBounce2) {
								new Bounce(player2).onExecuted();
								broadcast("info * "+player2.getName()+" ���˵�");
							}
							else if(defend2!=null)broadcast("info * "+player2.getName()+" �����˷�����"+defend2.name);
							else {
								broadcast("info * "+player2.getName()+" �����˹�����");
								for(int i=0;i<attack2Length;i++) {
									broadcast("info - "+attack2[i].name);
								}
							}
							//�жϱ���
							if((defend1!=null&&player1.checkAfterInput(defend1))||
									(defend1==null&&player1.checkAfterInput(attack1,attack1Length))) {
								broadcast("info * "+player1.getName()+" ����");
								endRound=true;
							}
							if((defend2!=null&&player2.checkAfterInput(defend2))||
									(defend2==null&&player2.checkAfterInput(attack2,attack2Length))) {
								broadcast("info * "+player2.getName()+" ����");
								endRound=true;
							}
							if(endRound)break;
							//�жϵ�
							if(isBounce1&&isBounce2)continue;
							if(isBounce1&&defend2==null) {
								for(int i=0;i<attack2Length;i++) {
									if(!attack2[i].attribute.equals("Explosion"))
										attack1[attack1Length++]=attack2[i];
								}
								attack2Length=0;
								defend2=new Defend(false,false,false,false,false,"��",0);
							}
							else if(isBounce2&&defend1==null) {
								for(int i=0;i<attack1Length;i++) {
									if(!attack1[i].attribute.equals("Explosion"))
										attack2[attack2Length++]=attack1[i];
								}
								attack1Length=0;
								defend1=new Defend(false,false,false,false,false,"��",0);
							}
							//����
							if(defend1!=null&&defend2!=null) {
								//������
								//ʲôҲ����
							}
							else if(defend1==null&&defend2!=null) {
								//1���� 2����
								endRound=player2.onDefend(defend2,attack1,attack1Length);
							}else if(defend2==null&&defend1!=null) {
								//2���� 1����
								endRound=player1.onDefend(defend1,attack2,attack2Length);
							}else if(defend1==null&&defend2==null) {
								//������
								endRound=player1.onCounteract(attack1,attack2,attack1Length,attack2Length);
								endRound=player2.onCounteract(attack2,attack1,attack2Length,attack1Length)||endRound;
							}
							//�лغ�
							if(endRound)break;
						}
					}
					conn1.send("gameinfo end");
					conn2.send("gameinfo end");
					isGameCompleted=true;
				} catch (Exception e) {
					System.out.println("����catch");
					//���ѹ������
					if(player1==null&&player2==null) {
						isGameCompleted=true;
						isGameRunning=false;
						broadcast("info ** �����˶����ߡ���ƽ���ˣ�");
					}
					if(player1==null) {
						isGameCompleted=true;
						isGameRunning=false;
						broadcast("info ** "+player1.getName()+" �����ˣ�");
						broadcast("info ** ��Ӯ�ң�"+player2.getName());
					}
					if(player2==null) {
						isGameCompleted=true;
						isGameRunning=false;
						broadcast("info ** "+player2.getName()+" �����ˣ�");
						broadcast("info ** ��Ӯ�ң�"+player1.getName());
					}
					if(!isGameCompleted) {
						e.printStackTrace();
						broadcast("info *** ������Ϸ��bug�ˣ�");
					}
				} finally {
					conn2.send("gameinfo end");
					conn1.send("gameinfo end");
					conn1=null;
					conn2=null;
					player1=null;
					player2=null;
					isGameRunning=false;
					if(isGameCompleted)broadcast("info ���� ��Ϸ�����ˣ�");
					else broadcast("info ���� ��Ϸ�����������ˣ�");
					broadcast("info ���� ��Ϸ����5���ر�");
					try {
						Thread.sleep(6000);
						end();
					} catch (InterruptedException e) {end();}
				}
    		}
    	};
    	gameThread.start();
    }
}
