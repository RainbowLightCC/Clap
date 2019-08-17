package cn.funnymc.game;

import cn.funnymc.actions.Attack;
import cn.funnymc.actions.Bounce;
import cn.funnymc.actions.Defend;
import cn.funnymc.occupations.UnemployedMan;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A Traditional 1v1 Clapping Game
 */
public class Game {
	private Player player1, player2;
    private boolean isGameRunning=false;
	private boolean isGameCompleted=false;
	private boolean isBounce1=false,isBounce2=false;
    private List<Attack> attack1=new ArrayList<>(),attack2=new ArrayList<>();
    private Defend defend1,defend2;
    private HashMap<String,Attack> attackMap1,attackMap2;
    private HashMap<String,Defend> defendMap1,defendMap2;
    private void end() {
    	GamesManager.end(this);
    }
	private void playerAttack(Player player, String input, String to) {
		if(player.equals(player1)){
			attack1.add(attackMap1.get(input).setTo(to));
		}else if(player.equals(player2)) {
			attack2.add(attackMap2.get(input).setTo(to));
		}
	}
	private void playerDefend(Player player, String input) {
		if(player.equals(player1)){
			defend1=defendMap1.get(input);
		}else if(player.equals(player2)) {
			defend2=defendMap2.get(input);
		}
	}

    /**
     * 玩家输入招式
     * @param player 玩家
     * @param inputJson 输入招式的JSON
     */
	public void playerJSONInput(Player player,String inputJson){
        JSONObject jsonObject=JSON.parseObject(inputJson);
        if(jsonObject.containsKey("special")){
			JSONObject special=jsonObject.getJSONObject("special");
			if(special.containsKey("action")&&special.get("action").equals("bounce")){
				if(player.equals(player1))isBounce1=true;
				else if(player.equals(player2))isBounce2=true;
			}
		}
		if(jsonObject.containsKey("defend")){
			JSONObject defend=jsonObject.getJSONObject("defend");
			if(defend.containsKey("action")){
				playerDefend(player,defend.getString("action"));
			}
		}
		if(jsonObject.containsKey("attack")){
			JSONObject attack=jsonObject.getJSONObject("attack");
			if(attack.containsKey("action")){
				playerAttack(player,attack.getString("action"),attack.getString("to"));
			}
		}
    }
	void newPlayer(Player player) {
		if(player1.getClapper()==null) {
			player1.setClapper(new UnemployedMan(player.getName()));
			player1.setGame(this);
		}
		else if(player2.getClapper()==null) {
			player2.setClapper(new UnemployedMan(player.getName()));
			player2.setGame(this);
		}
	}
	private void broadcast(String msg) {
    	if(!player1.isOffline())player1.sendMessage(msg);
    	if(!player2.isOffline())player2.sendMessage(msg);
	}
	/**
     * 五六七走
     * @throws InterruptedException 
     */
    private void clap567() throws InterruptedException {
    	Thread.sleep(500);
    	broadcast("CLAP 567 五");
    	Thread.sleep(500);
    	broadcast("CLAP 567 六");
    	Thread.sleep(500);
    	broadcast("CLAP 567 七");
    	Thread.sleep(500);
    	broadcast("CLAP 567 走");
    }
    /**
     * 攻击防御名称表
     */
    private void buildHashMaps() {
    	attackMap1= new HashMap<>();
    	for(Attack atk:player1.getClapper().getAttackList()) attackMap1.put(atk.name,atk);
    	attackMap2= new HashMap<>();
    	for(Attack atk:player2.getClapper().getAttackList()) attackMap2.put(atk.name,atk);
    	defendMap1= new HashMap<>();
    	for(Defend dfd:player1.getClapper().getDefendList()) defendMap1.put(dfd.name,dfd);
    	defendMap2= new HashMap<>();
    	for(Defend dfd:player2.getClapper().getDefendList()) defendMap2.put(dfd.name,dfd);
    }
    void start() {
    	Thread gameThread=new Thread(){
    		public void run() {
	    		try {
	    			isGameRunning=true;
	    			isGameCompleted=false;
	    			buildHashMaps();
					broadcast("CLAP START [\""+player1.getName()+"\",\""+player2.getName()+"\"]");
					//游戏本体
					player1.getClapper().init();
					player2.getClapper().init();
					while(true) {//回合
						//结束游戏 吗
						if(!isGameRunning) {
							break;
						}
						//报血量
						broadcast("CLAP HEALTH {\""+player1.getName()+"\":"+player1.getClapper().getHealth()+
								",\""+player2.getName()+"\":"+player2.getClapper().getHealth()+"}");
						if(player1.getClapper().checkAfterRound()&&player2.getClapper().checkAfterRound()) {
							broadcast("CLAP END TIE");
							break;
						}
						else if(player1.getClapper().checkAfterRound()) {
							broadcast("CLAP END "+player2.getName());
							break;
						}
						else if(player2.getClapper().checkAfterRound()) {
                            broadcast("CLAP END "+player1.getName());
							break;
						}
						clap567();//五六七走
						while(true) {//拍
							boolean endRound=false;
							//清空输入
							/*
							 远古代码改进
							 (OK) Phase 1:改进Attack写法
							 TODO Phase 2:更好地管理Defend(I类招式)和Attack(II类招式) 
							 */
							attack1.clear();attack2.clear();
							defend1=null;defend2=null;
							isBounce1=false;isBounce2=false;
							//广播饼数
							broadcast("CLAP BISCUIT {\""+player1.getName()+"\":"+player1.getClapper().getBiscuits()+
									",\""+player2.getName()+"\":"+player2.getClapper().getBiscuits()+"}");
							//输入
							broadcast("CLAP INPUT START");
							Thread.sleep(3000);
							broadcast("CLAP INPUT END");
							//自动出饼
							if((!isBounce1)&&defend1==null&&attack1.isEmpty())defend1=defendMap1.get("饼");
							if((!isBounce2)&&defend2==null&&attack2.isEmpty())defend2=defendMap2.get("饼");
							//广播输入结果
							if(isBounce1) {
								new Bounce(player1.getClapper()).onExecuted();
								broadcast("CLAP ACTION {\"special\":{\"action\":\"bounce\"}," +
										"\"sender\":\""+player1.getName()+"\"}");
							}
							else if(defend1!=null){
							    broadcast("CLAP ACTION {\"defend\":{\"action\":\""
                                        +defend1.name+"\"},\"sender\":\""+player1.getName()+"\"}");
                            }
							else {
								broadcast("CLAP ACTION {\"attack\":"+ JSON.toJSONString(attack1)
                                        +",\"sender\":\""+player1.getName()+"\"}");
							}
                            if(isBounce2) {
                                new Bounce(player2.getClapper()).onExecuted();
                                broadcast("CLAP ACTION {\"special\":{\"action\":\"bounce\"}," +
                                        "\"sender\":\""+player2.getName()+"\"}");
                            }
                            else if(defend2!=null){
                                broadcast("CLAP ACTION {\"defend\":{\"action\":\""
                                        +defend2.name+"\"},\"sender\":\""+player2.getName()+"\"}");
                            }
                            else {
                                broadcast("CLAP ACTION {\"attack\":"+ JSON.toJSONString(attack2)
                                        +",\"sender\":\""+player2.getName()+"\"}");
                            }
							//判断爆点
							if((defend1!=null&&player1.getClapper().checkAfterInput(defend1))||
									(defend1==null&&player1.getClapper().checkAfterInput(attack1))) {
								broadcast("CLAP BURST "+player1.getName());
								endRound=true;
							}
							if((defend2!=null&&player2.getClapper().checkAfterInput(defend2))||
									(defend2==null&&player2.getClapper().checkAfterInput(attack2))) {
								broadcast("CLAP BURST "+player2.getName());
								endRound=true;
							}
							if(endRound)break;
							//判断弹 TODO: Think of a better way to process Bounce
							if(isBounce1&&isBounce2)continue;
							if(isBounce1&&defend2==null) {
								for(Attack i:attack2) {
									if(!i.attribute.equals("Explosion"))
										attack1.add(i);
								}
								attack2.clear();
								defend2=new Defend(false,false,false,false,false,false,"空",0);
							}
							else if(isBounce2&&defend1==null) {
								for(Attack i:attack1) {
									if(!i.attribute.equals("Explosion"))
										attack2.add(i);
								}
								attack1.clear();
								defend1=new Defend(false,false,false,false,false,false,"空",0);
							}
							//抵消
							if(defend1!=null&&defend2!=null) {
								//都防御
								//什么也不做
							}
							else if(defend1==null&&defend2!=null) {
								//1攻击 2防御
								endRound=player2.getClapper().onDefend(defend2,attack1);
							}else if(defend2==null&&defend1!=null) {
								//2攻击 1防御
								endRound=player1.getClapper().onDefend(defend1,attack2);
							}else if(defend1==null&&defend2==null) {
								//都攻击
								endRound=player1.getClapper().onCounteract(attack1,attack2);
								endRound=player2.getClapper().onCounteract(attack2,attack1)||endRound;
							}
							//切回合
							if(endRound)break;
						}
					}
					broadcast("CLAP END END");
					isGameCompleted=true;
				} catch (Exception e) {
					//消费过世玩家
					if(player1.isOffline()&&player2.isOffline()) {
						isGameCompleted=true;
						isGameRunning=false;
					}
					else if(player1.isOffline()) {
						isGameCompleted=true;
						isGameRunning=false;
						player2.sendMessage("CLAP END "+player2.getName());
					}
					else if(player2.isOffline()) {
						isGameCompleted=true;
						isGameRunning=false;
						player1.sendMessage("CLAP END "+player1.getName());
					}
					if(!isGameCompleted) {
						e.printStackTrace();
						broadcast("CLAP BUG "+e.getMessage());
					}
				} finally {
					broadcast("CLAP END END");//客户端可能收到多个END END
					isGameRunning=false;
					if(isGameCompleted)broadcast("CLAP END END");
					else broadcast("CLAP END BUG");
					//五秒后关闭拍手
					try {
						Thread.sleep(5000);
						player1.setGame(null);
						player2.setGame(null);
						end();
					} catch (InterruptedException e) {end();}
				}
    		}
    	};
    	gameThread.start();
    }
}
