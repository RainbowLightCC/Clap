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
     * 五六七走
     * @throws InterruptedException 
     */
    private void clap567() throws InterruptedException {
    	Thread.sleep(500);
    	broadcast("clap 567 五");
    	Thread.sleep(500);
    	broadcast("clap 567 六");
    	Thread.sleep(500);
    	broadcast("clap 567 七");
    	Thread.sleep(500);
    	broadcast("clap 567 走");
    }
    /**
     * 攻击防御名称表
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
					//游戏本体
					player1.init();
					player2.init();
					while(true) {//回合
						//结束游戏 吗
						if(!isGameRunning) {
							break;
						}
						//报血量
						broadcast("info * "+player1.getName()+" 血量："+player1.getHealth());
						broadcast("info * "+player2.getName()+" 血量："+player2.getHealth());
						if(player1.checkAfterRound()&&player2.checkAfterRound()) {
							broadcast("info ** 两个人都去世——平局了！");
							break;
						}
						else if(player1.checkAfterRound()) {
							broadcast("info ** "+player1.getName()+" 去世了！");
							broadcast("info ** 大赢家："+player2.getName());
							break;
						}
						else if(player2.checkAfterRound()) {
							broadcast("info ** "+player2.getName()+" 去世了！");
							broadcast("info ** 大赢家："+player1.getName());
							break;
						}
						clap567();//五六七走
						while(true) {//拍
							boolean endRound=false;
							//清空输入
							attack1=new Attack[128];attack2=new Attack[128];
							attack1Length=0;attack2Length=0;
							defend1=null;defend2=null;
							isBounce1=false;isBounce2=false;
							//广播饼数
							broadcast("info * "+player1.getName()+" 有 "+player1.getBiscuits()+" 个饼");
							broadcast("info * "+player2.getName()+" 有 "+player2.getBiscuits()+" 个饼");
							//输入
							broadcast("info ** 开始玩家输入 **");
							conn1.send("gameinfo startinput");
							conn2.send("gameinfo startinput");
							isDoingInput=true;
							Thread.sleep(3000);
							conn1.send("gameinfo endinput");
							conn2.send("gameinfo endinput");
							isDoingInput=false;
							broadcast("info ** 玩家输入完成 **");
							//自动出饼
							if((!isBounce1)&&defend1==null&&attack1Length==0)defend1=defendMap1.get("饼");
							if((!isBounce2)&&defend2==null&&attack2Length==0)defend2=defendMap2.get("饼");
							//广播输入结果
							if(isBounce1) {
								new Bounce(player1).onExecuted();
								broadcast("info * "+player1.getName()+" 出了弹");
							}
							else if(defend1!=null)broadcast("info * "+player1.getName()+" 输入了防御："+defend1.name);
							else {
								broadcast("info * "+player1.getName()+" 输入了攻击：");
								for(int i=0;i<attack1Length;i++) {
									broadcast("info - "+attack1[i].name);
								}
							}
							if(isBounce2) {
								new Bounce(player2).onExecuted();
								broadcast("info * "+player2.getName()+" 出了弹");
							}
							else if(defend2!=null)broadcast("info * "+player2.getName()+" 输入了防御："+defend2.name);
							else {
								broadcast("info * "+player2.getName()+" 输入了攻击：");
								for(int i=0;i<attack2Length;i++) {
									broadcast("info - "+attack2[i].name);
								}
							}
							//判断爆点
							if((defend1!=null&&player1.checkAfterInput(defend1))||
									(defend1==null&&player1.checkAfterInput(attack1,attack1Length))) {
								broadcast("info * "+player1.getName()+" 爆点");
								endRound=true;
							}
							if((defend2!=null&&player2.checkAfterInput(defend2))||
									(defend2==null&&player2.checkAfterInput(attack2,attack2Length))) {
								broadcast("info * "+player2.getName()+" 爆点");
								endRound=true;
							}
							if(endRound)break;
							//判断弹
							if(isBounce1&&isBounce2)continue;
							if(isBounce1&&defend2==null) {
								for(int i=0;i<attack2Length;i++) {
									if(!attack2[i].attribute.equals("Explosion"))
										attack1[attack1Length++]=attack2[i];
								}
								attack2Length=0;
								defend2=new Defend(false,false,false,false,false,"空",0);
							}
							else if(isBounce2&&defend1==null) {
								for(int i=0;i<attack1Length;i++) {
									if(!attack1[i].attribute.equals("Explosion"))
										attack2[attack2Length++]=attack1[i];
								}
								attack1Length=0;
								defend1=new Defend(false,false,false,false,false,"空",0);
							}
							//抵消
							if(defend1!=null&&defend2!=null) {
								//都防御
								//什么也不做
							}
							else if(defend1==null&&defend2!=null) {
								//1攻击 2防御
								endRound=player2.onDefend(defend2,attack1,attack1Length);
							}else if(defend2==null&&defend1!=null) {
								//2攻击 1防御
								endRound=player1.onDefend(defend1,attack2,attack2Length);
							}else if(defend1==null&&defend2==null) {
								//都攻击
								endRound=player1.onCounteract(attack1,attack2,attack1Length,attack2Length);
								endRound=player2.onCounteract(attack2,attack1,attack2Length,attack1Length)||endRound;
							}
							//切回合
							if(endRound)break;
						}
					}
					conn1.send("gameinfo end");
					conn2.send("gameinfo end");
					isGameCompleted=true;
				} catch (Exception e) {
					System.out.println("进入catch");
					//消费过世玩家
					if(player1==null&&player2==null) {
						isGameCompleted=true;
						isGameRunning=false;
						broadcast("info ** 两个人都离线——平局了！");
					}
					if(player1==null) {
						isGameCompleted=true;
						isGameRunning=false;
						broadcast("info ** "+player1.getName()+" 掉线了！");
						broadcast("info ** 大赢家："+player2.getName());
					}
					if(player2==null) {
						isGameCompleted=true;
						isGameRunning=false;
						broadcast("info ** "+player2.getName()+" 掉线了！");
						broadcast("info ** 大赢家："+player1.getName());
					}
					if(!isGameCompleted) {
						e.printStackTrace();
						broadcast("info *** 拍手游戏出bug了！");
					}
				} finally {
					conn2.send("gameinfo end");
					conn1.send("gameinfo end");
					conn1=null;
					conn2=null;
					player1=null;
					player2=null;
					isGameRunning=false;
					if(isGameCompleted)broadcast("info 拍手 游戏结束了！");
					else broadcast("info 拍手 游戏非正常结束了！");
					broadcast("info 拍手 游戏将在5秒后关闭");
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
