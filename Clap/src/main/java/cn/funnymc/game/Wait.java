package cn.funnymc.game;

import cn.funnymc.occupations.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Waiting Room For Basic Clapping Game
 * TODO: 1.new Wait() (ok)
 * TODO: 2.wait.add(p1) (ok)
 * TODO: 3.wait.isPrepared() (ok)
 * TODO: 4.wait.leave(p1)
 * TODO: 5.wait.startGame() returns Game
 * TODO: 6.wait.isFull() (ok)
 */
public class Wait{
    private int maxPlayersNum,playersNum;
    private List<String> players;
    public Wait(){
        this.playersNum = 0;
        this.maxPlayersNum = 2;
        this.players=new ArrayList<>();
    }

    /**
     * Need to check isFull() before calling it
     * @param newPlayer
     */
    public void addPlayer(String newPlayer){
        players.add(newPlayer);
        playersNum++;
    }

    public boolean isFull(){
        return playersNum==maxPlayersNum;
    }

    public boolean isPrepared(){
        return isFull();
    }

    public void leavePlayer(String leavingPlayer){
        int numBefore=players.size();
        players.remove(leavingPlayer);
        if(players.size()==numBefore-1)playersNum--;
    }

    public Game startGame(){
        Game game=new Game();
        for(String p:players){
            game.newPlayer(p);
        }
        return game;
    }
}
