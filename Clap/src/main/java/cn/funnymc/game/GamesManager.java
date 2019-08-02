package cn.funnymc.game;

import java.util.*;

import cn.funnymc.game.Game;
/**
 * Clapping Games Manager
 * @author FunnyCrafter
 * TODO: 1.Finish this TODO
 */
public class GamesManager {
    private static List<Game> games= new LinkedList<>();
    public static void end(Game game) {
        games.remove(game);
    }
}
