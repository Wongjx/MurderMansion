package com.jkjk.Host;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class MMServer {
	private final String TAG = "MMServer";
	
	private ItemSpawner itemSpawner;
	private WeaponSpawner weaponSpawner;
	private WeaponPartSpawner weaponPartSpawner;
	
	public ServerSocket serverSocket;
	private InetAddress serverAddress;
	private int serverPort;	
	
	private ArrayList<Socket> clients;
	private ArrayList<PrintWriter> serverOutput;
	private ArrayList<BufferedReader> serverInput;

	
	private final int numOfPlayers;
	private Random randMurderer;
	private int murdererId;

	private final int[] playerIsAlive; // If 1 -> true; If 0 -> false;
	private final int[] playerIsStun; // If 1 -> true; If 0 -> false;
	private final int[] playerType; // If 0 -> civilian; If 1 -> murderer; If 2 -> Ghost
	private final float[] playerPosition;
	private final float[] playerAngle;

	// private ArrayList<Location> playerLocations;
	private final SpawnBuffer itemSpawnLocations;
	private final SpawnBuffer weaponSpawnLocations;
	private final SpawnBuffer weaponPartSpawnLocations;
	private final SpawnBuffer trapLocations;

	// To Pass: Sprites of objects (items, weapons, players, bat swing, knife stab, shotgun blast, disguise
	// animation
	// HOW?!!?!?!?!?!?!!

	public MMServer(int numOfPlayers) throws InterruptedException {
		System.out.println("Server instantized.");
		this.numOfPlayers = numOfPlayers;
		
		System.out.println("Initialize fields");
		playerIsAlive = new int[numOfPlayers];
		playerIsStun = new int[numOfPlayers];
		playerType = new int[numOfPlayers];
		playerPosition = new float[numOfPlayers];
		playerAngle = new float[numOfPlayers];

		System.out.println("Creating item spawn buffers");
		itemSpawnLocations = new SpawnBuffer(numOfPlayers*3);
		weaponSpawnLocations = new SpawnBuffer(numOfPlayers);
		weaponPartSpawnLocations = new SpawnBuffer(numOfPlayers*2);
		trapLocations = new SpawnBuffer(numOfPlayers);
		
		System.out.println("Assigning murderer");
		murdererId = randMurderer.nextInt(numOfPlayers);
		
		System.out.println("Spawning items");
		spawnItems(numOfPlayers*3);
		spawnWeapons(numOfPlayers);
		spawnWeaponParts(numOfPlayers*2);
		
		// Attempt to connect to clients (numOfPlayers)
		System.out.println("Creating server socket");
		initServerSocket();
		acceptServerConnections();
	}

	private void spawnItems(int numOfItems) throws InterruptedException {
		for (int i = 0; i < numOfItems; i++) {
			produceItemSpawnLocations(itemSpawner.spawn());
		}
	}
	
	private void spawnWeapons(int numOfItems) throws InterruptedException {
		for (int i = 0; i < numOfItems; i++) {
			produceWeaponPartSpawnLocations(weaponSpawner.spawn());
		}
	}
	
	private void spawnWeaponParts(int numOfItems) throws InterruptedException {
		for (int i = 0; i < numOfItems; i++) {
			produceWeaponPartSpawnLocations(weaponPartSpawner.spawn());
		}
	}

	public int getNumOfPlayers() {
			return numOfPlayers;
	}

	public int[] getPlayerIsAlive() {
		synchronized (playerIsAlive) {
			return playerIsAlive;
		}
	}

	public void setPlayerIsAlive(int position, int value) {
		synchronized (playerIsAlive) {
			playerIsAlive[position] = value;
		}
	}

	public int[] getPlayerIsStun() {
		synchronized (playerIsStun) {
			return playerIsStun;
		}
	}

	public void setPlayerIsStun(int position, int value) {
		synchronized (playerIsStun) {
			playerIsStun[position] = value;
		}
	}

	public int[] getPlayerType() {
		synchronized (playerType) {
			return playerType;
		}
	}

	public void setPlayerType(int position, int value) {
		synchronized (playerType) {
			playerType[position] = value;
		}
	}

	public float[] getPlayerPosition() {
		synchronized (playerPosition) {
			return playerPosition;
		}
	}

	public void setPlayerPosition(int position, float value) {
		synchronized (playerPosition) {
			playerPosition[position] = value;
		}
	}	
	
	public float[] getPlayerAngle() {
		synchronized (playerAngle) {
			return playerAngle;
		}
	}

	public void setPlayerAngle(int position, float value) {
		synchronized (playerAngle) {
			playerAngle[position] = value;
		}
	}

	public SpawnBuffer getItemSpawnLocations() {
		synchronized (itemSpawnLocations) {
			return itemSpawnLocations;
		}
	}

	public void produceItemSpawnLocations(Location location) throws InterruptedException {
		synchronized (itemSpawnLocations) {
			itemSpawnLocations.produce(location);
		}
	}

	public void consumeItemSpawnLocations(Location location) throws InterruptedException {
		synchronized (itemSpawnLocations) {
			itemSpawnLocations.consume(location);
			itemSpawner.restore(location);
		}
	}

	public SpawnBuffer getWeaponSpawnLocations() {
		synchronized (weaponSpawnLocations) {
			return weaponSpawnLocations;
		}
	}

	public void produceWeaponSpawnLocations(Location location) throws InterruptedException {
		synchronized (weaponSpawnLocations) {
			weaponSpawnLocations.produce(location);
		}
	}

	public void consumeWeaponSpawnLocations(Location location) throws InterruptedException {
		synchronized (weaponSpawnLocations) {
			weaponSpawnLocations.consume(location);
			weaponSpawner.restore(location);
		}
	}

	public SpawnBuffer getWeaponPartSpawnLocations() {
		synchronized (weaponPartSpawnLocations) {
			return weaponPartSpawnLocations;
		}
	}

	public void produceWeaponPartSpawnLocations(Location location) throws InterruptedException {
		synchronized (weaponPartSpawnLocations) {
			weaponPartSpawnLocations.produce(location);
		}
	}

	public void consumeWeaponPartSpawnLocations(Location location) throws InterruptedException {
		synchronized (weaponPartSpawnLocations) {
			weaponPartSpawnLocations.consume(location);
		}
	}

	public SpawnBuffer getTrapLocations() {
		synchronized (trapLocations) {
			return trapLocations;
		}
	}

	public void produceTrapLocations(Location location) throws InterruptedException {
		synchronized (trapLocations) {
			trapLocations.produce(location);
		}
	}

	public void consumeTrapLocations(Location location) throws InterruptedException {
		synchronized (trapLocations) {
			trapLocations.consume(location);
		}
	}
	
	public InetAddress getServerAddress() {
		synchronized(serverAddress){
			return serverAddress;
		}
	}

	public void setServerAddress(InetAddress socketAddress) {
		synchronized(serverAddress){
			this.serverAddress = socketAddress;
		}
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int socketPort) {
		this.serverPort = socketPort;
	}

	public ArrayList<Socket> getClients() {
		synchronized(clients){
			return new ArrayList<Socket>(clients);
		}
	}

	public void setClients(ArrayList<Socket> clients) {
		synchronized(clients){
			this.clients = new ArrayList<Socket>(clients);
		}
	}

	public ArrayList<PrintWriter> getServerOutput() {
		synchronized(serverOutput){
			return new ArrayList<PrintWriter>(serverOutput);
		}
	}

	public void setServerOutput(ArrayList<PrintWriter> serverOutput) {
		synchronized(serverOutput){
			this.serverOutput = new ArrayList<PrintWriter>(serverOutput);
		}
	}

	public ArrayList<BufferedReader> getServerInput() {
		synchronized(serverInput){
			ArrayList<BufferedReader> ret = new ArrayList<BufferedReader>(serverInput);
			return ret;
		}		
	}

	public void setServerInput(ArrayList<BufferedReader> serverInput) {
		synchronized(serverInput){
			this.serverInput = new ArrayList<BufferedReader>(serverInput);
		}
	}
	
	//Initialize server socket 
	public void initServerSocket() {
		try{
			System.out.println("Creating server");
			//Randomly server to an open port
			ServerSocket sock = new ServerSocket(0);
			System.out.println("Server created at port "+sock.getLocalPort());
			this.serverSocket=sock;
			setServerAddress(sock.getInetAddress());
			setServerPort(sock.getLocalPort());
			System.out.println("Server Socket created. Port: "+serverPort+" address: "+serverAddress);
			
			
		}catch(Exception e){
			System.out.println("Error creating server socket: " +e.getMessage());
		}
	}
	
	// Start accepting client connections
	public void acceptServerConnections(){
		if (serverSocket!=null){
			System.out.println("Server accepting client connections");
			Thread thread = new serverAcceptThread(this);
			thread.start();
		} else{
			System.out.println( "Server is not ready");
		}
	}
	//Send a string message out to all connected clients
	public void sendToClients(String Message){
		for (PrintWriter write: this.serverOutput){
			write.println(Message);
			write.flush();
		}
	}
	
//	public void sendPlayerPosition(int position, float x_position, float y_position, float player_angle  ){
//		String message="pp_"+position+"_"+Float.toString(x_position);
//		sendToClients
//	}
	
}


/**
 * Sub Thread to for server to accept connections, stops when all players in gps room connects successfully
 * Precondition: MultiPlayerSeissonInfo.server != null;
 * @author Wong
 *
 */
class serverAcceptThread extends Thread{
	private MMServer server;
	private String TAG = "ServerAcceptThread";
	public serverAcceptThread(MMServer server){
		this.server=server;
	}
	@Override
	public void run(){
		while(server.getClients().size()<server.getNumOfPlayers()){
			try{
				Socket socket = server.serverSocket.accept();
				//Add in client socket
				ArrayList<Socket>temp =server.getClients(); 
				temp.add(socket);
				server.setClients(temp);
				//Add input stream
				ArrayList<BufferedReader> tempInput = server.getServerInput();
				tempInput.add(new BufferedReader(new InputStreamReader(socket.getInputStream())));
				server.setServerInput(tempInput);
				//Add output stream
				ArrayList<PrintWriter> tempOutput = server.getServerOutput();
				tempOutput.add(new PrintWriter(socket.getOutputStream(),true));
				server.setServerOutput(tempOutput);
				
			}catch(Exception e){
				Gdx.app.log(TAG, "Error creating server socket: " +e.getMessage());
			}
		}
		//Start a listener thread for each client socket connected
		for (BufferedReader read:server.getServerInput()){
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

