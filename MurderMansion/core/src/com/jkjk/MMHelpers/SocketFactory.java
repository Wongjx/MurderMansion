package com.jkjk.MMHelpers;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import com.badlogic.gdx.Gdx;

public class SocketFactory {
	private final String TAG = "SocketFactory";
	private final MultiplayerSeissonInfo info;
	public SocketFactory(MultiplayerSeissonInfo info){
		this.info=info;
	}
	public ServerSocket getServerSocket() {
		ServerSocket sock = null;
		try{
			sock = new ServerSocket(0);
			info.socketAddress=sock.getLocalSocketAddress();
			info.server=sock;
			
		}catch(Exception e){
			Gdx.app.debug(TAG, "Error creating server socket: " +e.getMessage());
		}
		
		return sock;
	}
//	public Socket getClientSocket(SocketAddress addr){
//		Socket sock;
//		
//	}
}
/**
 * Thread used to connect to server socket
 * Precondition: GPS room is in play mode && Server has sent out SocketAddress to clients
 * @author Wong
 *
 */
class clientConnectThread extends Thread{
	private String TAG = "clientConnectThread";
	private MultiplayerSeissonInfo info;
	public clientConnectThread(MultiplayerSeissonInfo info,SocketAddress add){
		this.info=info;
	}
	@Override
	public void start(){
		if (info.socketAddress!=null){
			try{
				info.sock=new Socket();
				info.sock.connect(info.socketAddress);
			}catch(Exception e){
				Gdx.app.debug(TAG, "Error connecting to server socket: " +e.getMessage());
			}
		}else{
			Gdx.app.debug(TAG, "SocketAddress is null");
		}
		
	}
}

/**
 * Non-blocking Thread to for server to accept connections
 * Precondition: GPS Room is in play mode && Message with server address sent out to clients 
 * 				&& MultiPlayerSeissonInfo.server != null;
 * @author Wong
 *
 */
class serverAcceptThread extends Thread{
	private MultiplayerSeissonInfo info;
	private String TAG = "ServerAcceptThread";
	public serverAcceptThread(MultiplayerSeissonInfo info){
		this.info=info;
	}
	@Override
	public void start(){
		while(!isInterrupted()){
			try{
				info.clients.add(info.server.accept());			
			}catch(Exception e){
				Gdx.app.debug(TAG, "Error creating server socket: " +e.getMessage());
			}
		}
	}
}
