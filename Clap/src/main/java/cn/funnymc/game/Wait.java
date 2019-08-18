package cn.funnymc.game;

import cn.funnymc.occupations.UnemployedMan;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * Waiting Room For Basic Clapping Game
 */
public class Wait{
    private int maxPlayersNum;
    private Set<Player> players=new HashSet<>(),forcePlayer=new HashSet<>();
    Wait(int maxPlayersNum){
        this.maxPlayersNum = maxPlayersNum;
    }
    private void resetAndStart(){
        if(maxPlayersNum==2)this.toGame().start();
        else this.toMultiplayerGame().start();
        this.players.clear();
    }
    private void broadcast(String msg){
        for(Player p:players){
            p.sendMessage(msg);
        }
    }

    /**
     * No occupation
     * @param newPlayer �����
     */
    void addPlayer(Player newPlayer){
        newPlayer.setClapper(new UnemployedMan(newPlayer.getName()));
        players.forEach(p->newPlayer.sendMessage("INFO JOIN "+p.getName()+" : "+p.getClapper().getOccupationName()));
        players.add(newPlayer);
        broadcast("INFO WAIT "+players.size()+" of "+maxPlayersNum);
        broadcast("INFO JOIN "+newPlayer.getName()+" : "+newPlayer.getClapper().getOccupationName());
        newPlayer.setWait(this);
        if(players.size()==maxPlayersNum){
            resetAndStart();
        }
    }
    public void addForce(Player player){
        forcePlayer.add(player);
        if(forcePlayer.size()==players.size())resetAndStart();
    }
    public void cancelForce(Player player){
        forcePlayer.remove(player);
    }
    /**
     * Occupations.
     * @param newPlayer �����
     * @param occupation ְҵ����������
     */
    void addPlayer(Player newPlayer,String occupation){
        try {
            newPlayer.setClapper((UnemployedMan)Class.forName("cn.funnymc."+occupation)
                    .getConstructor(String.class).newInstance(newPlayer.getName()));
            players.forEach(p->newPlayer.sendMessage("INFO JOIN "+p.getName()+" : "+p.getClapper().getOccupationName()));
            players.add(newPlayer);
            broadcast("INFO WAIT "+players.size()+" of "+maxPlayersNum+"\n");
            broadcast("INFO JOIN "+newPlayer.getName()+" : "+newPlayer.getClapper().getOccupationName());
            newPlayer.setWait(this);
            if(players.size()==maxPlayersNum){
                if(maxPlayersNum==2)this.toGame().start();
                else this.toMultiplayerGame().start();
                resetAndStart();
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            System.out.println(e.getMessage());
            newPlayer.sendMessage("INFO OCCUPATION ERROR");
        }
    }
    void leavePlayer(Player leavingPlayer){
        players.remove(leavingPlayer);
        broadcast("INFO LEAVE "+leavingPlayer.getName()+" : "+leavingPlayer.getClapper().getOccupationName());
    }
    private Game toGame(){
        Game game=new Game();
        for(Player p:players){
            p.setWait(null);
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
