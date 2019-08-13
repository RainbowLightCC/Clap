package cn.funnymc.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Waiting Room For Basic Clapping Game
 */
class Wait{
    private int maxPlayersNum;
    private List<Player> players;
    Wait(int maxPlayersNum){
        this.maxPlayersNum = maxPlayersNum;
        this.players=new ArrayList<>();
    }
    private void reset(){
        this.players.clear();
    }
    private void broadcast(String msg){
        for(Player p:players){
            p.sendMessage(msg);
        }
    }
    
    void addPlayer(Player newPlayer){
        players.add(newPlayer);
        broadcast("INFO WAIT "+players.size()+" of "+maxPlayersNum);
        if(players.size()==maxPlayersNum){
            if(maxPlayersNum==2)this.toGame().start();
            else this.toMultiplayerGame().start();
            reset();
        }
    }

    void leavePlayer(Player leavingPlayer){
        players.remove(leavingPlayer);
    }

    private Game toGame(){
        Game game=new Game();
        for(Player p:players){
            game.newPlayer(p);
        }
        return game;
    }
    private MultiplayerGame toMultiplayerGame(){
        MultiplayerGame game=new MultiplayerGame();
        for(Player p:players){
            game.newPlayer(p);
        }
        return game;
    }
}
