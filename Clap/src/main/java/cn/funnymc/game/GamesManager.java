package cn.funnymc.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Clapping Games Manager
 * @author FunnyCrafter
 * TODO: 1.Finish this TODO
 */
public class GamesManager {
    private GamesManager(){}
    private static Set<Game> games= new HashSet<>();
    private static Set<MultiplayerGame> multiplayerGames=new HashSet<>();
    private static Wait wait=new Wait(2),
        multiplayerWait=new Wait(8),
        occupationWait=new Wait(2),
        multiOccupationWait=new Wait(8);
    static void end(Game game) {
        games.remove(game);
    }
    static void end(MultiplayerGame game){multiplayerGames.remove(game);}
    public static void  join(Player joiningPlayer,String json){
        JSONObject jsonObject=JSON.parseObject(json);
        if(!jsonObject.containsKey("type"))return;
        if(jsonObject.getString("type").equals("traditional")){
            if(jsonObject.getInteger("players")==2){
                //单挑
                wait.addPlayer(joiningPlayer);
            }else{
                multiplayerWait.addPlayer(joiningPlayer);
            }
        }else if(jsonObject.getString("type").equals("occupation")){
            if(jsonObject.getInteger("players")==2){
                occupationWait.addPlayer(joiningPlayer,jsonObject.getString("Occupation"));
            }else{
                multiOccupationWait.addPlayer(joiningPlayer,jsonObject.getString("Occupation"));
            }
        }
    }
    public static void leave(Player leavingPlayer){
        wait.leavePlayer(leavingPlayer);
        multiplayerWait.leavePlayer(leavingPlayer);
    }
    public static String getUsedOccupationFor2pGame(){
        return occupationWait.getUsedOccupationFor2pGame();
    }
}
