package com.jkjk.Host;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;


public class MMServer {
	private final String TAG = "MMServer";
	
	public ServerSocket serverSocket;
	private InetAddress serverAddress;
	private int serverPort;	
	
	private ArrayList<Socket> clients;
	private ArrayList<PrintWriter> serverOutput;
	private ArrayList<BufferedReader> serverInput;

	private long startTime;
	private long runTime;
	private long nextItemSpawnTime;
	private long nextObstacleRemoveTime;

	private final int numOfPlayers;
	private Random randMurderer;
	private int murdererId;

	private final ConcurrentHashMap<String, Integer> playerIsAlive; // If 1 -> true; If 0 -> false;
	private final ConcurrentHashMap<String, Integer> playerIsStun; // If 1 -> true; If 0 -> false;
	private final ConcurrentHashMap<String, Integer> playerType; // If 0 -> murderer; If 1 -> civilian; If 2
																	// -> Ghost
	private final ConcurrentHashMap<String, float[]> playerPosition;
	private final ConcurrentHashMap<String, Float> playerAngle;

	// private ArrayList<Location> playerLocations;
	private final SpawnBuffer itemLocations;
	private final SpawnBuffer weaponLocations;
	private final SpawnBuffer weaponPartLocations;
	private final SpawnBuffer trapLocations;

	private final ItemSpawner itemSpawner;
	private final WeaponSpawner weaponSpawner;
	private final WeaponPartSpawner weaponPartSpawner;

	// To Pass: Sprites of objects (items, weapons, players, bat swing, knife stab, shotgun blast, disguise
	// animation
	// HOW?!!?!?!?!?!?!!

<<<<<<< HEAD
	public MMServer(int numOfPlayers) throws InterruptedException {
		System.out.println("Server instantized.");
		this.numOfPlayers = numOfPlayers;
		
		System.out.println("Initialize fields");
		startTime = System.currentTimeMillis();
		this.numOfPlayers = numOfPlayers;
		playerIsAlive = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerIsStun = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerType = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerPosition = new ConcurrentHashMap<String, float[]>(numOfPlayers);
		playerAngle = new ConcurrentHashMap<String, Float>(numOfPlayers);

		System.out.println("Creating item spawn buffers");
		itemSpawnLocations = new SpawnBuffer(numOfPlayers*3);
		weaponSpawnLocations = new SpawnBuffer(numOfPlayers);
		weaponPartSpawnLocations = new SpawnBuffer(numOfPlayers*2);
		trapLocations = new SpawnBuffer(numOfPlayers);


		itemSpawner = new ItemSpawner();
		weaponSpawner = new WeaponSpawner();
		weaponPartSpawner = new WeaponPartSpawner();
		

		System.out.println("Assigning murderer");
		murdererId = new Random().nextInt(numOfPlayers);
		
		System.out.println("Spawning items");

		spawnItems(numOfPlayers * 2);
		spawnWeapons(numOfPlayers);
		spawnWeaponParts(numOfPlayers);

		nextItemSpawnTime = 10000;
		nextObstacleRemoveTime = 60000;

		initPlayers();

		// Attempt to connect to clients (numOfPlayers)
		System.out.println("Creating server socket");
		initServerSocket();
		acceptServerConnections();
	}

	public void update() {
		runTime = System.currentTimeMillis() - startTime;

		// Item/Weapon/WeaponPart Spawn *NEEDS TO BE BALANCED TO FIT GAMEPLAY
		if (runTime > nextItemSpawnTime) {
			System.out.println("SPAWN!");
			if (!itemLocations.isFull())
				spawnItems(1);
			if (!weaponLocations.isFull())
				spawnWeapons(1);
			if (!weaponPartLocations.isFull())
				spawnWeaponParts(1);
			nextItemSpawnTime = new Random().nextInt(10000) + runTime + 5000;
		}

		// Opens random door in mansion *TO BE IMPLEMENTED
		if (runTime > nextObstacleRemoveTime) {
			System.out.println("NEW DOOR OPENS!");
			nextObstacleRemoveTime = new Random().nextInt(10000) + runTime + 60000;
		}
	}

	private void initPlayers() {
		for (int i = 0; i < numOfPlayers; i++) {
			playerIsAlive.put("Player " + i, 1);
			playerIsStun.put("Player " + i, 0);
			if (i == murdererId) {
				playerType.put("Player " + i, 0);
			} else {
				playerType.put("Player " + i, 1);
			}
			playerPosition.put("Player " + i, new float[] { 1010 - ((i + 1) * 40), 515 });
			playerAngle.put("Player " + i, 0f);
		}
	}

	private void spawnItems(int numOfItems) {
		for (int i = 0; i < numOfItems; i++) {
			produceItem(itemSpawner.spawn());
		}
	}

	private void spawnWeapons(int numOfItems) {
		for (int i = 0; i < numOfItems; i++) {
			produceWeapon(weaponSpawner.spawn());
		}
	}

	private void spawnWeaponParts(int numOfItems) {
		for (int i = 0; i < numOfItems; i++) {
			produceWeaponPart(weaponPartSpawner.spawn());
		}
	}

	public int getNumOfPlayers() {
		return numOfPlayers;
	}

	public ConcurrentHashMap<String, Integer> getPlayerIsAlive() {
		return playerIsAlive;
	}

	public void setPlayerIsAlive(String key, int value) {
		playerIsAlive.put(key, value);
	}

	public ConcurrentHashMap<String, Integer> getPlayerIsStun() {
		return playerIsStun;
	}

	public void setPlayerIsStun(String key, int value) {
		playerIsAlive.put(key, value);
	}

	public ConcurrentHashMap<String, Integer> getPlayerType() {
		return playerType;
	}

	public void setPlayerType(String key, int value) {
		playerType.put(key, value);
	}

	public ConcurrentHashMap<String, float[]> getPlayerPosition() {
		return playerPosition;
	}

	public void setPlayerPosition(String key, float[] value) {
		playerPosition.put(key, value);
	}

	public ConcurrentHashMap<String, Float> getPlayerAngle() {
		return playerAngle;
	}

	public void setPlayerAngle(String key, float value) {
		playerAngle.put(key, value);
	}

	public SpawnBuffer getItemLocations() {
		synchronized (itemLocations) {
			return itemLocations;
		}
	}

	public void produceItem(Location location) {
		synchronized (itemLocations) {
			itemLocations.produce(location);
		}
	}

	public void consumeItem(Location location) throws InterruptedException {
		synchronized (itemLocations) {
			itemLocations.consume(location);
			itemSpawner.restore(location);
		}
	}

	public SpawnBuffer getWeaponLocations() {
		synchronized (weaponLocations) {
			return weaponLocations;
		}
	}

	public void produceWeapon(Location location) {
		synchronized (weaponLocations) {
			weaponLocations.produce(location);
		}
	}

	public void consumeWeapon(Location location) {
		synchronized (weaponLocations) {
			weaponLocations.consume(location);
			weaponSpawner.restore(location);
		}
	}

	public SpawnBuffer getWeaponPartLocations() {
		synchronized (weaponPartLocations) {
			return weaponPartLocations;
		}
	}

	public void produceWeaponPart(Location location) {
		synchronized (weaponPartLocations) {
			weaponPartLocations.produce(location);
		}
	}

	public void consumeWeaponPart(Location location) {
		synchronized (weaponPartLocations) {
			weaponPartLocations.consume(location);
		}
	}

	public SpawnBuffer getTrapLocations() {
		synchronized (trapLocations) {
			return trapLocations;
		}
	}

	public void produceTrap(Location location) throws InterruptedException {
		synchronized (trapLocations) {
			trapLocations.produce(location);
		}
	}

	public void consumeTrap(Location location) throws InterruptedException {
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

