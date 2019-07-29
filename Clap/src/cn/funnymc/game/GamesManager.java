package cn.funnymc.game;

import java.util.*;

import cn.funnymc.game.Game;
/**
 * Clapping Games Manager
 * @author FunnyCrafter
 */
public class GamesManager {
	private static List<Game> games=new LinkedList<Game>();
	public static void end(Game game) {
		games.remove(game);
	}
}
