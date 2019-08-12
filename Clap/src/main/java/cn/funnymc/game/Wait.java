package cn.funnymc.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Waiting Room For Basic Clapping Game
 */
public class Wait{
    private int maxPlayersNum,playersNum;
    private List<Player> players;
    public Wait(){
        this.playersNum = 0;
        this.maxPlayersNum = 2;
        this.players=new ArrayList<>();
    }
    
    public void reset(){
        this.playersNum = 0;
        this.maxPlayersNum = 2;
        this.players.clear();
    }
    /**
     * Need to check isFull() before calling it
     * @param newPlayer
     */
    public void addPlayer(Player newPlayer){
        players.add(newPlayer);
        playersNum++;
        newPlayer.sendMessage("INFO WAIT "+playersNum+" of "+maxPlayersNum);
    }

    public boolean isFull(){
        return playersNum==maxPlayersNum;
    }

    public void leavePlayer(Player leavingPlayer){
        int numBefore=players.size();
        players.remove(leavingPlayer);
        if(players.size()==numBefore-1)playersNum--;
    }

    public Game toGame(){
        Game game=new Game();
        for(Player p:players){
            game.newPlayer(p);
        }
        return game;
    }
}
