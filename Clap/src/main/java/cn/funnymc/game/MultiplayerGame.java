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
import java.util.Map;
import java.util.stream.Collectors;

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
    private void playerAttack(Player player, String input,String to) {
        attacks.get(player.getName()).add(attackMaps.get(player.getName()).get(input).setTo(to));
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
                playerAttack(player,attack.getString("action"),attack.getString("to"));
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
                            for(String s:players.keySet()){
                                if((defends.get(s)!=null&&players.get(s).getClapper().checkAfterInput(defends.get(s)))||
                                    (defends.get(s)==null&&players.get(s).getClapper().checkAfterInput(attacks.get(s)))){
                                    broadcast("CLAP BURST "+players.get(s).getName());
                                    endRound=true;
                                }
                            }
                            if(endRound)break;
                            //判断弹
                            for (Map.Entry<String, Player> entry : players.entrySet()) {
                                String s1 = entry.getKey();
                                Player player1 = entry.getValue();
                                for (Map.Entry<String, Player> e2 : players.entrySet()) {
                                    String s2 = e2.getKey();
                                    Player player2 = e2.getValue();
                                    if(player1.equals(player2))continue;
                                    boolean isBounce1 = isBounce.get(s1), isBounce2 = isBounce.get(s2);
                                    Defend defend1 = defends.get(s1), defend2 = defends.get(2);
                                    List<Attack> attack1 = attacks.get(s1), attack2 = attacks.get(s2);
                                    if (isBounce1 && (!isBounce2) && defend2 == null) {
                                        //1出弹，2出攻击 --> 2被弹了
                                        player2.getClapper().onDefend(Defend.empty, attack2.stream().
                                                filter(a -> !a.attribute.equals("Explosion"))
                                                .filter(a -> a.getTo().equals(player2.getName()))
                                                .collect(Collectors.toList()));
                                    } else if ((!isBounce1) && defend1 != null && defend2 != null) {
                                        //1出攻击，2出防御 --> 2被打了
                                        player2.getClapper().onDefend(defend2, attack1.stream()
                                                .filter(a -> a.getTo().equals(player2.getName()))
                                                .collect(Collectors.toList()));
                                    } else if ((!isBounce1) && defend1 != null && (!isBounce2) && defend2 != null) {
                                        //都攻击
                                        endRound = player2.getClapper().onCounteract(attack2, attack1) || endRound;
                                    }
                                }
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
