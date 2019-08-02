package cn.funnymc.game;

import cn.funnymc.occupations.Player;
/**
 * TODO: 1.new Wait(p1)
 * TODO: 2.wait.add(p2)
 * TODO: 3.wait.isPrepared()
 * TODO: 4.wait.leave(p1)
 * TODO: 5.wait.startGame() returns Game
 */
public class Wait{
    private int maxPlayersNum,playersNum;
    private Player p1,p2;
    public Wait(){
        this.playersNum = 0;
        this.maxPlayersNum = 2;

    }
}
