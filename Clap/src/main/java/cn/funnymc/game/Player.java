package cn.funnymc.game;

import cn.funnymc.occupations.UnemployedMan;
import org.java_websocket.WebSocket;

public class Player {
    private WebSocket conn;
    private String name;
    public Player(WebSocket conn, String name){
        this.conn = conn;
        this.name = name;
    }
    public void sendMessage(String message){
        conn.send(message);
    }
    public String getName(){
        return name;
    }
    //TODO: Clapper
    private UnemployedMan clapper;
    public UnemployedMan getClapper(){
        return clapper;
    }
    public void setClapper(UnemployedMan clapper){
        this.clapper=clapper;
    }
}
