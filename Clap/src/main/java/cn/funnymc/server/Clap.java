package cn.funnymc.server;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import cn.funnymc.game.Player;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import cn.funnymc.game.Game;
import cn.funnymc.game.GamesManager;
import cn.funnymc.login.Checker;

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
		System.out.println(player.getName()+" has left!");
	}

	/**
	 * Client -> Server
	 * SAY <Text>
	 * PLAY //<Setup>(Json)
	 * CLAP <Action>(Json)
	 *
	 * Server -> Client
	 * CHAT <Username> <Message>
	 * INFO <Type> <Message>
	 * - INFO WAIT <...>
	 */
	@Override
	public void onMessage( WebSocket conn, String message ) {
		Player player=(Player) conn;
		System.out.println( player.getName() + ": " + message );
		String[] splitedMessage=message.split(" ",2);
		String cmd=splitedMessage[0],text=splitedMessage[1];
		switch (cmd) {
			case "SAY":
				broadcast("CHAT "+player.getName()+" "+text);
				break;
			case "CLAP":
				/*
				 * TODO: [19.8.04] Send the command to Game.java
				 */
				break;
			case "PLAY":
				GamesManager.join(player);
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