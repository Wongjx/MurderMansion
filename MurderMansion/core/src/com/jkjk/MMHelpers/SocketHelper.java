package com.jkjk.MMHelpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.badlogic.gdx.Gdx;

public class SocketHelper {
	private final String TAG = "SocketFactory";
	private final MultiplayerSeissonInfo info;
	
	
	public SocketHelper(MultiplayerSeissonInfo info){
		this.info=info;		
	}
	
	public void sendToClients(String Message){
		for (PrintWriter write: info.serverOutput){
			write.println(Message);
			write.flush();
		}
	}
	public void sendToServer(String message){
		info.clientOuput.println(message);
		info.clientOuput.flush();
	}
	
	public void initServerSocket() {
		try{
			ServerSocket sock = new ServerSocket(0);
			info.serverSocket=sock;
			info.socketAddress=sock.getInetAddress();
			info.port = sock.getLocalPort();
			Gdx.app.log(TAG, "Server Socket created. Port: "+info.port+" address: "+info.socketAddress);
			
			
		}catch(Exception e){
			Gdx.app.log(TAG, "Error creating server socket: " +e.getMessage());
		}
	}
	
	public void acceptServerConnections(){
		if (info.isServer && info.serverSocket!=null){
			Gdx.app.log(TAG, "Server accepting client connections");
			Thread thread = new serverAcceptThread(info);
			thread.start();
		} else{
			Gdx.app.log(TAG, "Server is not ready");
		}
	}
	
	/**
	 * Precondition: Message containing socket address received from server
	 * Postcondition: MultiplayerSeissonInfo.socket == ClientSocket 
	 */
	public void initClientSocket(){
		if (info.socketAddress!=null){
			Thread thread = new clientConnectThread(info);
			thread.start();
		} else {
			Gdx.app.log(TAG, "Socket Address/Port is null");
		}		
	}
	
	public void setupSocket(){
		if (info.isServer){
			initServerSocket();
		}
		else{
			initClientSocket();
		}
	}
	
	public void closeSocket() throws IOException{
		if (info.isServer){
			for(Socket socket:info.clients){
				socket.close();
			}
			info.serverSocket.close();
		}else{
			info.clientInput.close();
			info.clientOuput.close();
			info.clientSocket.close();
		}
	}
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
	public clientConnectThread(MultiplayerSeissonInfo info){
		this.info=info;
	}
	
	@Override
	public void run(){
		try{
			info.clientSocket=new Socket();
			//Create InetSocketAddress then connect to server socket
			InetSocketAddress addr = new InetSocketAddress(info.socketAddress,info.port);
			info.clientSocket.connect(addr);
			info.clientInput= new BufferedReader(new InputStreamReader(info.clientSocket.getInputStream()));
			info.clientOuput= new PrintWriter(info.clientSocket.getOutputStream(),true);
			
		}catch(Exception e){
			Gdx.app.log(TAG, "Error connecting to server socket: " +e.getMessage());
		}
	}
}

/**
 * Sub Thread to for server to accept connections, stops when all players in gps room connects successfully
 * Precondition: MultiPlayerSeissonInfo.server != null;
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
	public void run(){
		while(info.clients.size()<info.mParticipants.size()){
			try{
				Socket socket = info.serverSocket.accept();
				info.clients.add(socket);
				info.serverInput.add(new BufferedReader(new InputStreamReader(socket.getInputStream())));
				info.serverOutput.add(new PrintWriter(socket.getOutputStream(),true));
			}catch(Exception e){
				Gdx.app.log(TAG, "Error creating server socket: " +e.getMessage());
			}
		}
		
		for (BufferedReader read:info.serverInput){
			Thread thread = new serverListener(read);
			thread.start();
		}
		
	}
}

class serverListener extends Thread{
	private BufferedReader input;
	private String msg;
	private String TAG = "serverListener Thread";
	public serverListener(BufferedReader inputStream){
		this.input=inputStream;
	}
	@Override
	public void run(){
		while(!isInterrupted()){
			try{
				if((msg=input.readLine())!=null){
					Gdx.app.log(TAG, "Message received: "+msg);
					//Do something with message
				}
			}catch(Exception e){
				Gdx.app.log(TAG, "Error while reading: "+e.getMessage());
			}
			
		}
	}
}
