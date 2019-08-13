package cn.funnymc.game;

import cn.funnymc.actions.Attack;
import cn.funnymc.actions.Bounce;
import cn.funnymc.actions.Defend;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A Traditional Multiplayer Clapping Game
 */
public class MultiplayerGame {
    private HashMap<String, Player> players=new HashMap<>();
    private boolean isGameRunning=false;
    private boolean isGameCompleted=false;
    private HashMap<String, Boolean> isBounce=new HashMap<>();
    private HashMap<String, List<Attack>> attacks=new HashMap<>();
    private HashMap<String, Defend> defends=new HashMap<>();
    private HashMap<String, HashMap<String, Attack>> attackMaps=new HashMap<>();
    private HashMap<String, HashMap<String, Defend>> defendMaps=new HashMap<>();
    
    private void end() {
        GamesManager.end(this);
    }
    
    //盯着看十秒，看懂了吗？
    private void playerAttack(Player player, String input) {
        attacks.get(player.getName()).add(attackMaps.get(player.getName()).get(input));
    }
    private void playerDefend(Player player, String input) {
        defends.put(player.getName(),defendMaps.get(player.getName()).get(input));
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
                isBounce.put(player.getName(),true);
            }
        };
        if(jsonObject.containsKey("defend")){
            JSONObject defend=jsonObject.getJSONObject("defend");
            if(defend.containsKey("action")){
                playerDefend(player,defend.getString("action"));
            }
        };
        if(jsonObject.containsKey("attack")){
            JSONObject attack=jsonObject.getJSONObject("attack");
            if(attack.containsKey("action")){
                playerAttack(player,attack.getString("action"));
            }
        }
    }
    void newPlayer(Player player) {
        players.put(player.getName(),player);
        isBounce.put(player.getName(),false);
        attacks.put(player.getName(),new ArrayList<Attack>());
        defends.put(player.getName(),null);
    }
    private void broadcast(String msg) {
        for(Player p: players.values()){
            p.sendMessage(msg);
        }
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
        for(String playerName:players.keySet()) {
            Player player=players.get(playerName);
            HashMap<String,Attack> attackMap=new HashMap<>();
            for (Attack atk : player.getClapper().getAttackList()) attackMap.put(atk.name, atk);
            attackMaps.put(playerName,attackMap);
            HashMap<String,Defend> defendMap=new HashMap<>();
            for (Defend dfd : player.getClapper().getDefendList()) defendMap.put(dfd.name, dfd);
            defendMaps.put(playerName,defendMap);
        }
    }
    void start() {
        Thread gameThread=new Thread(){
            public void run() {
                try {
                    isGameRunning=true;
                    isGameCompleted=false;
                    buildHashMaps();
                    broadcast("CLAP START [\""+StringUtils.join(players.keySet(),"\",\"")+"\"]");
                    //游戏本体
                    players.forEach((k,v)->v.getClapper().init());
                    while(true) {//回合
                        //结束游戏 吗
                        if(!isGameRunning) {
                            break;
                        }
                        //报血量
                        List<String> tempHealthString=new ArrayList<>();
                        for(Player p:players.values()){
                            tempHealthString.add("\""+p.getName()+"\":"+p.getClapper().getHealth());
                        }
                        broadcast("CLAP HEALTH {"+StringUtils.join(tempHealthString,",")+"}");
                        //FIXME: 这样写,死掉的人无法旁观
                        players.entrySet().removeIf(p -> p.getValue().getClapper().checkAfterRound());
                        clap567();//五六七走
                        while(true) {//拍
                            boolean endRound=false;
                            //清空输入
                            attacks.forEach((k,v)->v.clear());
                            defends.clear();
                            isBounce.clear();
                            //广播饼数
                            List<String> tempBiscuitString=new ArrayList<>();
                            for(Player p:players.values()){
                                tempBiscuitString.add("\""+p.getName()+"\":"+p.getClapper().getBiscuits());
                            }
                            broadcast("CLAP HEALTH {"+StringUtils.join(tempBiscuitString,",")+"}");
                            //输入
                            broadcast("CLAP INPUT START");
                            Thread.sleep(3000);
                            broadcast("CLAP INPUT END");
                            //自动出饼
                            players.keySet().stream()
                                    .filter(s -> !isBounce.get(s))
                                    .filter(s -> defends.get(s)==null)
                                    .filter(s -> attacks.get(s).isEmpty())
                                    .forEach(s -> defends.put(s,defendMaps.get(s).get("饼")));
                            //广播输入结果
                            for(String s:players.keySet()){
                                if(isBounce.get(s)){
                                    new Bounce(players.get(s).getClapper()).onExecuted();
                                    broadcast("CLAP ACTION {\"special\":{\"action\":\"bounce\"}," +
                                            "\"sender\":\""+s+"\"}");
                                }else if(defends.get(s)!=null){
                                    broadcast("CLAP ACTION {\"defend\":{\"action\":\""
                                            +defends.get(s).name+"\"},\"sender\":\""+s+"\"}");
                                }else{
                                    broadcast("CLAP ACTION {\"attack\":"+ JSON.toJSONString(attacks.get(s))
                                            +",\"sender\":\""+s+"\"}");
                                }
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
                            //判断弹
                            if(isBounce1&&isBounce2)continue;
                            if(isBounce1&&defend2==null) {
                                for(Attack i:attack2) {
                                    if(!i.attribute.equals("Explosion"))
                                        attack1.add(i);
                                }
                                attack2.clear();
                                defend2=new Defend(false,false,false,false,false,"空",0);
                            }
                            else if(isBounce2&&defend1==null) {
                                for(Attack i:attack1) {
                                    if(!i.attribute.equals("Explosion"))
                                        attack2.add(i);
                                }
                                attack1.clear();
                                defend1=new Defend(false,false,false,false,false,"空",0);
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
                        players.forEach((k,v)->v.setGame(null));
                        end();
                    } catch (InterruptedException e) {end();}
                }
            }
        };
        gameThread.start();
    }
}
