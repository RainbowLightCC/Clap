package cn.funnymc.server;

import cn.funnymc.game.GamesManager;
import cn.funnymc.game.Player;
import cn.funnymc.login.Checker;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Websocket Clap Server.
 */
public class Clap extends WebSocketServer {
	public Clap(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}
	public Clap(InetSocketAddress address){
		super(address);
	}

	//Check token
	@Override
	public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer( WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
		ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer( conn, draft, request );
		String descriptor=request.getResourceDescriptor();
		if(descriptor.indexOf('?')!=1) throw new InvalidDataException(0);
		String[] parameters=descriptor.substring(2).split("=");
		if(parameters.length!=2||!parameters[0].equals("token"))throw new InvalidDataException(1);
		String token=parameters[1];
		//���token
		String name=Checker.checkToken(token);
		if(name==null)throw new InvalidDataException(2);
		conn.setAttachment(new Player(conn,name));
		return builder;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake){
		Player player=(Player)conn.getAttachment();
		System.out.println(player.getName()+" connected!");
	}
	@Override
	public void onClose(WebSocket conn,int code,String reason,boolean remote) {
		Player player=(Player)conn.getAttachment();
		GamesManager.leave(player);
		System.out.println(player.getName()+" has left!");
	}

	/**
	 * IMPORTANT: Websocket Interaction Document
	 * Client -> Server
	 * SAY <Text>
	 * PLAY <Setup>(Json)
	 * CLAP <Action>(Json)
	 * LEAVE <WAIT|GAME>
     * FORCE <START|CANCEL>//multiplayer
     * QUERY <2|8>
     * 
	 * Server -> Client
	 * CHAT <Username> <Message>
	 * INFO <Type> <Message>
	 * -- WAIT <...>
	 * -- JOIN <PLAYER : OCCUPATION>
	 * -- LEAVE <PLAYER : OCCUPATION>
	 * -- OCCUPATION ERROR/USED/%name%
	 * 
	 * CLAP <Type> <Message>
	 * -- 567 <五|六|七|走|START|END>
	 * -- HEALTH <JSON>({"name":health,...})
	 * -- ACTION <ACTION>(Json)
	 * -- START <PLAYER LIST>(Json List)
	 * -- END <TIE|END|BUG|%WINNER%>
	 * -- BISCUIT <JSON>({"name":biscuit,...})
	 * -- INPUT <START|END>
	 * -- BUG <MESSAGE>
	 * -- DIE <PLAYER NAME>
	 * -- LEAVE <PLAYER NAME>
	 * -- BURST <PLAYER NAME>
	 */
	@Override
	public void onMessage( WebSocket conn, String message ) {
		Player player=(Player)conn.getAttachment();
		System.out.println( player.getName() + ": " + message );
		String[] splitedMessage=message.split(" ",2);
		String cmd=splitedMessage[0],text=splitedMessage[1];
		switch (cmd) {
			case "SAY":
				broadcast("CHAT "+player.getName()+" "+text);
				break;
			case "CLAP":
				if(player.getGame()!=null)
				    player.getGame().playerJSONInput(player,text);
				break;
			case "PLAY":
				GamesManager.join(player,text);
				break;
            case "LEAVE":
                if(text.equals("WAIT")){
                    GamesManager.leave(player);
                }else if(text.equals("GAME")){
                    
                }
                break;
			case "FORCE":
				if(text.equals("START")){
					player.getWait().addForce(player);
				}else if(text.equals("CANCEL")){
					player.getWait().cancelForce(player);
				}
				break;
			case "QUERY":
				player.sendMessage("OCCUPATION "+GamesManager.getUsedOccupationFor2pGame());
				break;
		}
	}

	@Override
	public void onError(WebSocket conn,Exception ex) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}
	@Override
	public void onStart() {
		System.out.println("Server started on port: "+this.getPort());
		setConnectionLostTimeout(0);
		setConnectionLostTimeout(100);
	}
}