package cn.funnymc.game;

import java.util.*;

import cn.funnymc.game.Game;
/**
 * Clapping Games Manager
 * @author FunnyCrafter
 * TODO: 1.Finish this TODO
 */
public class GamesManager {
    private static Set<Game> games= new HashSet<>();
    private static Wait wait;
    public static void end(Game game) {
        games.remove(game);
    }
    public static void join(Player joiningPlayer){
        wait.addPlayer(joiningPlayer);
        if(wait.isFull()){
            wait.toGame().start();
            wait.reset();
        }
    }
    public static void leave(Player leavingPlayer){
        wait.leavePlayer(leavingPlayer);
    }
}
