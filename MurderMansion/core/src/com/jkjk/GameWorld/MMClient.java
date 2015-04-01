package com.jkjk.GameWorld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.jkjk.GameObjects.WeaponPartSprite;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.GameCharacterFactory;
import com.jkjk.GameObjects.Items.ItemSprite;
import com.jkjk.GameObjects.Weapons.WeaponSprite;
import com.jkjk.Host.Location;
import com.jkjk.Host.SpawnBuffer;

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
//	private final MultiplayerSeissonInfo info;
	
	private GameWorld gWorld;
	private GameRenderer renderer;
	
	private String serverAddress;
	private int serverPort;
	
	public Socket clientSocket;
	private BufferedReader clientInput;
	private PrintWriter clientOutput;
	
	private int numOfPlayers;
	private int id;	
	private int murdererId;
	private ArrayList<GameCharacter> playerList;

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
	 * @throws Exception 
	 */
	public MMClient(GameWorld gWorld, GameRenderer renderer, String serverAddress, int serverPort) throws Exception {

		this.gWorld = gWorld;
		this.renderer = renderer;
		this.serverAddress=serverAddress;
		this.serverPort=serverPort;
				
		//Connect to server
		initClientSocket(this.serverAddress, this.serverPort);
		
		//Receive initialzation parameters
		numOfPlayers=Integer.parseInt(clientInput.readLine());
		id=Integer.parseInt(clientInput.readLine());
		murdererId=Integer.parseInt(clientInput.readLine());
		
		System.out.println("Creating item spawn buffers");
		itemLocations = new SpawnBuffer(numOfPlayers*3);
		weaponLocations = new SpawnBuffer(numOfPlayers);
		weaponPartLocations = new SpawnBuffer(numOfPlayers*2);
		trapLocations = new SpawnBuffer(numOfPlayers);
		
		String message;
		//Receive item locations
		if((message=clientInput.readLine()).equals("itemLocations")){
			while (!(message=clientInput.readLine()).equals("end")){
				String[] locations=message.split("_");
				for (String coordinates:locations){
					String[] XY = coordinates.split(",");
					itemLocations.produce(new Location(new float[] {Float.parseFloat(XY[0]),Float.parseFloat(XY[1])}));
				}
			}
		}
		//Receive weapon locations
		if((message=clientInput.readLine()).equals("weaponLocations")){
			while (!(message=clientInput.readLine()).equals("end")){
				String[] locations=message.split("_");
				for (String coordinates:locations){
					String[] XY = coordinates.split(",");
					weaponLocations.produce(new Location(new float[] {Float.parseFloat(XY[0]),Float.parseFloat(XY[1])}));
				}
			}
		}
		//Receive weaponPart locations
		if((message=clientInput.readLine()).equals("weaponPartLocations")){
			while (!(message=clientInput.readLine()).equals("end")){
				String[] locations=message.split("_");
				for (String coordinates:locations){
					String[] XY = coordinates.split(",");
					weaponPartLocations.produce(new Location(new float[] {Float.parseFloat(XY[0]),Float.parseFloat(XY[1])}));
				}
			}
		}
		System.out.println("Number of players before: "+numOfPlayers);
		playerList= new ArrayList<GameCharacter>(numOfPlayers);
		System.out.println("Creating concurrent hashmaps for player condition.");
		playerType = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerIsAlive = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerIsStun = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerPosition = new ConcurrentHashMap<String, float[]>(numOfPlayers);
		playerAngle = new ConcurrentHashMap<String, Float>(numOfPlayers);
		
		initPlayers();
		
		//Create and start extra thread that reads any incoming messages
		Thread thread = new clientListener(clientInput);
		thread.start();
		
		//Create player's body

//
//		for (int i = 0; i < numOfPlayers; i++) {
//			//Create body for opponents, if not your id
//			if (i!=this.id){
//				createOpponents(this.getPlayerType().get("Player " + i));
//			}			
//		}

//		createTrap(); // FOR DEBUG PURPOSE
//		for (int i = 0; i < numOfPlayers * 2; i++) {
//			createItems(1060 - (i * 40), 490);
//			createWeapons(1060 - (i * 40), 460);
//			createWeaponParts(1060 - (i * 40), 430);
//		}

	}
	
	private void initPlayers() {
		GameCharacterFactory factory = new GameCharacterFactory();
		boolean isPlayer;
		System.out.println("Number of players "+numOfPlayers);
		System.out.println("Player list size "+playerList.size());
		for (int i = 0; i < numOfPlayers; i++) {
			playerIsAlive.put("Player " + i, 1);
			playerIsStun.put("Player " + i, 0);
			if (i == id) {
				playerType.put("Player " + i, 0);
				playerList.add(	gWorld.createPlayer(playerType.get("Player " + id)));
			} else {
				if(i== murdererId){
					playerList.add(factory.createCharacter("Murderer", i, gWorld, false));
				}else{
					playerList.add(factory.createCharacter("Civilian", i, gWorld, false));
				}
				playerType.put("Player " + i, 1);
				
			}
			playerPosition.put("Player " + i, new float[] { 1010 - ((i + 1) * 40), 515 });
			playerAngle.put("Player " + i, 0f);
		}
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

//	// FOR DEBUG PURPOSE
//	private void createTrap() {
//		bdef = new BodyDef();
//		fdef = new FixtureDef();
//		bdef.type = BodyType.StaticBody;
//		bdef.position.set(1010, 570);
//		body = gWorld.getWorld().createBody(bdef);
//
//		CircleShape shape = new CircleShape();
//		shape.setRadius(10);
//		fdef.shape = shape;
//		fdef.isSensor = true;
//		fdef.filter.maskBits = 1;
//
//		body.createFixture(fdef).setUserData("trap");
//	}
//
//	// FOR DEBUG PURPOSE
//	private void createOpponents(int i) {
//		if (i == 0) {
//			playerList.add((Murderer) gWorld.getGameCharFac().createCharacter("Murderer", i, gWorld,
//					false));
//			playerList.get(playerList.size - 1).getBody().setType(BodyType.KinematicBody);
//			playerList.get(playerList.size - 1).spawn(1010 - (((playerList.size - 1) + 1) * 40), 515, 0);
//		} else {
//			playerList.add((Civilian) gWorld.getGameCharFac().createCharacter("Civilian", i, gWorld,
//					false));
//			playerList.get(playerList.size - 1).getBody().setType(BodyType.KinematicBody);
//			playerList.get(playerList.size - 1).spawn(1010 - (((playerList.size - 1) + 1) * 40), 515, 0);
//		}
//	}

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
	public ArrayList<GameCharacter> getPlayerList() {
		return playerList;
	}

	/**
	 * Updates the locations of all players' position on the map.
	 */
	private void playerTransform() {
		for (int i = 0; i < numOfPlayers; i++) {
			playerList.get(i).getBody().setTransform(playerPosition.get("Player " + i)[0],
							playerPosition.get("Player " + i)[0],
							playerAngle.get("Player " + i));
		}
	}

//	/**
//	 * Updates the locations of all items on the map.
//	 */
//	private void itemLocations() {
//		return itemLocations;
//	}
//
//	/**
//	 * Updates the locations of all weapon on the map.
//	 */
//	private void weaponLocations() {
//		server.getWeaponLocations();
//	}
//
//	/**
//	 * Updates the locations of all weapon parts on the map.
//	 */
//	private void weaponPartLocations() {
//		server.getWeaponPartLocations();
//	}
//
//	/**
//	 * Updates the locations of all traps on the map.
//	 */
//	private void trapLocations() {
//		server.getTrapLocations();
//	}

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
	 * @throws Exception 
	 */
	public void initClientSocket(String address, int port) throws Exception{
		if (address != null){
			clientSocket=new Socket();
			//Create InetSocketAddress and connect to server socket
			InetAddress addr = InetAddress.getByName(address);
			InetSocketAddress iAddress = new InetSocketAddress(addr,port);
			clientSocket.connect(iAddress);
			
			setClientInput(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
			setClientOutput(new PrintWriter(clientSocket.getOutputStream(),true));
			
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
