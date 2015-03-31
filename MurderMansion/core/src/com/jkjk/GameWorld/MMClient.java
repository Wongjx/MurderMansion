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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.jkjk.GameObjects.WeaponPartSprite;
import com.jkjk.GameObjects.Characters.Civilian;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.Murderer;
import com.jkjk.GameObjects.Items.ItemSprite;
import com.jkjk.GameObjects.Weapons.WeaponSprite;
import com.jkjk.Host.MMServer;

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
<<<<<<< HEAD
	private final String TAG = "MMClient"; 
	private final MultiplayerSeissonInfo info;
	
	public Socket clientSocket;
	private BufferedReader clientInput;
	private PrintWriter clientOutput;

	private GameWorld gWorld;
	private GameRenderer renderer;

	private Array<GameCharacter> playerList;
	private int numOfPlayers;
	private int id;

	private BodyDef bdef;
	private Body body;
	private FixtureDef fdef;
	
	/**
	 * Constructs the multiplayer world, including creation of opponents.
	 * 
	 * @param gWorld
	 *            GameWorld instance
	 * @param renderer
	 *            GameRenderer instance
	 */
	public MMClient(GameWorld gWorld, GameRenderer renderer, MultiplayerSeissonInfo info) {
		// Attempt to connect to Server
		this.server = server;
		this.gWorld = gWorld;
		this.renderer = renderer;
		this.info=info;
		id = 0;		

		playerList = new Array<GameCharacter>();
		numOfPlayers = server.getNumOfPlayers();

		gWorld.createPlayer(server.getPlayerType().get("Player " + id));

		for (int i = 1; i < numOfPlayers; i++) {
			createOpponents(server.getPlayerType().get("Player " + i));
		}

		createTrap(); // FOR DEBUG PURPOSE
		for (int i = 0; i < numOfPlayers * 2; i++) {
			createItems(1060 - (i * 40), 490);
			createWeapons(1060 - (i * 40), 460);
			createWeaponParts(1060 - (i * 40), 430);
		}

		initClientSocket(info.serverAddress, info.serverPort);
	}

	/**
	 * Updates the GameWorld with other player's actions, such as player position, item positions and
	 * item/weapon use.
	 */
	public void update() {/*
						 * playerTransform();
						 * 
						 * // Upon receiving socket information, (if item added, etc.), run corresponding
						 * method itemLocations(); weaponLocations(); weaponPartLocations(); trapLocations();
						 * batUsed(); knifeUsed();
						 */
	}

	/**
	 * Renders the GameRenderer with other player's move.
	 */
	public void render() {
		for (GameCharacter gc : getPlayerList()) {
			gc.render(renderer.getCam());
		}
	}

	// FOR DEBUG PURPOSE
	private void createTrap() {
		bdef = new BodyDef();
		fdef = new FixtureDef();
		bdef.type = BodyType.StaticBody;
		bdef.position.set(1010, 570);
		body = gWorld.getWorld().createBody(bdef);

		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;

		body.createFixture(fdef).setUserData("trap");
	}

	// FOR DEBUG PURPOSE
	private void createOpponents(int i) {
		if (i == 0) {
			playerList.add((Murderer) gWorld.getGameCharFac().createCharacter("Murderer", i, gWorld,
					false));
			playerList.get(playerList.size - 1).getBody().setType(BodyType.KinematicBody);
			playerList.get(playerList.size - 1).spawn(1010 - (((playerList.size - 1) + 1) * 40), 515, 0);
		} else {
			playerList.add((Civilian) gWorld.getGameCharFac().createCharacter("Civilian", i, gWorld,
					false));
			playerList.get(playerList.size - 1).getBody().setType(BodyType.KinematicBody);
			playerList.get(playerList.size - 1).spawn(1010 - (((playerList.size - 1) + 1) * 40), 515, 0);
		}
	}

	/**
	 * Create item sprites on the map.
	 * 
	 * @param x
	 *            X coordinate on the map.
	 * @param y
	 *            Y coordinate on the map.
	 */
	private void createItems(float x, float y) {
		gWorld.getItemList().add(new ItemSprite(gWorld));
		gWorld.getItemList().get(gWorld.getItemList().size - 1).spawn(x, y, 0);
	}

	/**
	 * Create weapon sprites on the map.
	 * 
	 * @param x
	 *            X coordinate on the map.
	 * @param y
	 *            Y coordinate on the map.
	 */
	private void createWeapons(float x, float y) {
		gWorld.getWeaponList().add(new WeaponSprite(gWorld));
		gWorld.getWeaponList().get(gWorld.getItemList().size - 1).spawn(x, y, 0);
	}

	/**
	 * Create weapon part sprites on the map.
	 * 
	 * @param x
	 *            X coordinate on the map.
	 * @param y
	 *            Y coordinate on the map.
	 */
	private void createWeaponParts(float x, float y) {
		gWorld.getWeaponPartList().add(new WeaponPartSprite(gWorld));
		gWorld.getWeaponPartList().get(gWorld.getItemList().size - 1).spawn(x, y, 0);
	}

	/**
	 * @return Number of players playing the game.
	 */
	public int getNumOfPlayers() {
		return numOfPlayers;
	}

	/**
	 * @return Obtain list of players.
	 */
	public Array<GameCharacter> getPlayerList() {
		return playerList;
	}

	/**
	 * Updates the locations of all players' position on the map.
	 */
	private void playerTransform() {
		for (int i = 0; i < numOfPlayers; i++) {
			playerList
					.get(i)
					.getBody()
					.setTransform(server.getPlayerPosition().get("Player " + i)[0],
							server.getPlayerPosition().get("Player " + i)[0],
							server.getPlayerAngle().get("Player " + i));
		}
	}

	/**
	 * Updates the locations of all items on the map.
	 */
	private void itemLocations() {
		server.getItemLocations();
	}

	/**
	 * Updates the locations of all weapon on the map.
	 */
	private void weaponLocations() {
		server.getWeaponLocations();
	}

	/**
	 * Updates the locations of all weapon parts on the map.
	 */
	private void weaponPartLocations() {
		server.getWeaponPartLocations();
	}

	/**
	 * Updates the locations of all traps on the map.
	 */
	private void trapLocations() {
		server.getTrapLocations();
	}

	/**
	 * Produces knife body from the player that used the knife.
	 */
	private void knifeUsed() {

	}

	/**
	 * Produces bat body from the player that used the bat.
	 */
	private void batUsed() {

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
