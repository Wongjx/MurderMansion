package com.jkjk.GameWorld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.jkjk.MMHelpers.MultiplayerSeissonInfo;

/**
 * @author LeeJunXiang MMClient listens to input from the Server by the host. Inputs include sharable data
 *         such as player position, item spawns and player status. MMClient will also output to the server the
 *         changes made by the player.
 * 
 *         More importantly, client-side processing will handle all actions by the player (movement, contact).
 *         The CONSEQUENCE of the action will be passed to the server, which will retransmit the results to
 *         all other clients. Consequences include the removal of an item when picking it up, or change in
 *         body position due to movement.
 * 
 */
public class MMClient {
	private final String TAG = "MMClient"; 
	private final MultiplayerSeissonInfo info;
	
	public Socket clientSocket;
	private BufferedReader clientInput;
	private PrintWriter clientOutput;
	
	public MMClient(MultiplayerSeissonInfo info){
		this.info=info;
		initClientSocket(info.serverAddress, info.serverPort);
	}

	public void update() {
		playerPositions();
		playerAngles();
		itemLocations();
		weaponLocations();
		weaponPartLocations();
		trapLocations();
	}

	public void render() {
	}

	private void playerPositions() {

	}

	private void playerAngles() {

	}

	private void itemLocations() {

	}

	private void weaponLocations() {

	}

	private void weaponPartLocations() {

	}

	private void trapLocations() {

	}
	
	public BufferedReader getClientInput() {
		synchronized(clientInput){
			return clientInput;
		}		
	}


	public void setClientInput(BufferedReader clientInput) {
		synchronized(clientInput){
		this.clientInput = clientInput;
		}
	}


	public PrintWriter getClientOutput() {
		synchronized(clientOutput){
			return clientOutput;
		}
	}


	public void setClientOutput(PrintWriter clientOutput) {
		synchronized(clientOutput){
			this.clientOutput = clientOutput;
		}
	}
	

	public void sendToServer(String message){
		clientOutput.println(message);
		clientOutput.flush();
	}
	
	
	/**
	 * Precondition: Message containing socket address received from server
	 * Postcondition: MultiplayerSeissonInfo.socket == ClientSocket 
	 */
	public void initClientSocket(InetAddress address, int port){
		if (address != null){
			Thread thread = new clientConnectThread(address,port,this);
			thread.start();
			//TODO change multiplayerroomstatus to PLAY once connected
			
		} else {
			Gdx.app.log(TAG, "Server Address/Port is null");
			//TODO Request information from server again
		}		
	}
	
	
	public void closeSocket() throws IOException{
			clientInput.close();
			clientOutput.close();
			clientSocket.close();
	}
}

/**
 * Thread used to connect to server socket
 * Precondition: GPS room is in play mode && Server has sent out SocketAddress to clients
 * @author Wong
 *
 */
class clientConnectThread extends Thread{
	private final String TAG = "clientConnectThread";
	private InetAddress address;
	private int port;
	private MMClient client;
	
	
	public clientConnectThread(InetAddress address, int port, MMClient client){
		this.address=address;
		this.port = port;
		this.client=client;
	}
	
	@Override
	public void run(){
		try{
			client.clientSocket=new Socket();
			//Create InetSocketAddress then connect to server socket
			InetSocketAddress addr = new InetSocketAddress(address,port);
			client.clientSocket.connect(addr);
			client.setClientInput(new BufferedReader(new InputStreamReader(client.clientSocket.getInputStream())));
			client.setClientOutput(new PrintWriter(client.clientSocket.getOutputStream(),true));
			
			//Create and start extra thread that reads any incoming messages
			Thread thread = new clientListener(client.getClientInput());
			thread.start();
			
		}catch(Exception e){
			Gdx.app.log(TAG, "Error connecting to server socket: " +e.getMessage());
			//TODO Do something about connection error!
		}
	}
}

class clientListener extends Thread{
	private BufferedReader input;
	private String msg;
	private String TAG = "serverListener Thread";
	public clientListener(BufferedReader inputStream){
		this.input=inputStream;
	}
	@Override
	public void run(){
		Gdx.app.log(TAG, "Starting client listener thread.");
		while(!isInterrupted()){
			try{
				if((msg=input.readLine())!=null){
					Gdx.app.log(TAG, "Message received: "+msg);
					//TODO something with message
				}
			}catch(Exception e){
				Gdx.app.log(TAG, "Error while reading: "+e.getMessage());
			}
			
		}
	}
}
