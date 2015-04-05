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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jkjk.GameObjects.Obstacles;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Items.ItemSprite;
import com.jkjk.GameObjects.Weapons.WeaponPartSprite;
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
	private static MMClient instance;
	private final String TAG = "MMClient";
	// private final MultiplayerSeissonInfo info;

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

	private final ConcurrentHashMap<String, Integer> playerIsAlive; // If 1 ->true; If 0 -> false;
	private final ConcurrentHashMap<String, Integer> playerIsStun; // If 1 ->
																	// true; If
																	// 0 ->
																	// false;
	private final ConcurrentHashMap<String, Integer> playerType; // If 0 ->
																	// murderer;
																	// If 1 ->
																	// civilian;
																	// If 2
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
	private MMClient(GameWorld gWorld, GameRenderer renderer, String serverAddress, int serverPort)
			throws Exception {

		this.gWorld = gWorld;
		this.renderer = renderer;

		this.serverAddress = serverAddress;
		this.serverPort = serverPort;

		// Connect to server
		initClientSocket(this.serverAddress, this.serverPort);

		// Receive initialzation parameters
		numOfPlayers = Integer.parseInt(clientInput.readLine());
		id = Integer.parseInt(clientInput.readLine());
		murdererId = Integer.parseInt(clientInput.readLine());

//		System.out.println("Creating item spawn buffers");
		itemLocations = new SpawnBuffer(numOfPlayers * 3);
		weaponLocations = new SpawnBuffer(numOfPlayers);
		weaponPartLocations = new SpawnBuffer(numOfPlayers * 2);
		trapLocations = new SpawnBuffer(numOfPlayers);

		String message;
		// Receive item locations
		if ((message = clientInput.readLine()).equals("itemLocations")) {
			System.out.println("get item locations");
			while (!(message = clientInput.readLine()).equals("end")) {
				String[] locations = message.split("_");
				for (String coordinates : locations) {
					String[] XY = coordinates.split(",");
					itemLocations.produce(new Location(new float[] { Float.parseFloat(XY[0]),
							Float.parseFloat(XY[1]) }));
					// Spawn item inside game world
					createItems(Float.parseFloat(XY[0]), Float.parseFloat(XY[1]));
				}
			}
		}
		// Receive weapon locations
		if ((message = clientInput.readLine()).equals("weaponLocations")) {
			System.out.println("get weapon locations");
			while (!(message = clientInput.readLine()).equals("end")) {
				String[] locations = message.split("_");
				for (String coordinates : locations) {
					String[] XY = coordinates.split(",");
					weaponLocations.produce(new Location(new float[] { Float.parseFloat(XY[0]),
							Float.parseFloat(XY[1]) }));
					// Spawn weapon in game world
					createWeapons(Float.parseFloat(XY[0]), Float.parseFloat(XY[1]));
				}
			}
		}
		// Receive weaponPart locations
		if ((message = clientInput.readLine()).equals("weaponPartLocations")) {
			System.out.println("get weapon part locations");
			while (!(message = clientInput.readLine()).equals("end")) {
				String[] locations = message.split("_");
				for (String coordinates : locations) {
					String[] XY = coordinates.split(",");
					weaponPartLocations.produce(new Location(new float[] { Float.parseFloat(XY[0]),
							Float.parseFloat(XY[1]) }));
					// spawn weapon parts in game world
					createWeaponParts(Float.parseFloat(XY[0]), Float.parseFloat(XY[1]));
				}
			}
		}

		playerList = new ArrayList<GameCharacter>(numOfPlayers);
//		System.out.println("Creating concurrent hashmaps for player condition.");
		playerType = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerIsAlive = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerIsStun = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerPosition = new ConcurrentHashMap<String, float[]>(numOfPlayers);
		playerAngle = new ConcurrentHashMap<String, Float>(numOfPlayers);

		// Receive spawn positions
		if ((message = clientInput.readLine()).equals("spawnPositions")) {
			System.out.println("get spawn positions");
			while (!(message = clientInput.readLine()).equals("end")) {
				String[] locations = message.split("_");
				for (int i = 0; i < numOfPlayers; i++) {
					String[] XY = locations[i].split(",");
					playerPosition.put("Player " + i,
							new float[] { Float.parseFloat(XY[0]), Float.parseFloat(XY[1]) });
				}
			}
		}

		// Receive spawn angles
		if ((message = clientInput.readLine()).equals("spawnAngles")) {
			System.out.println("get spawn angles");
			while (!(message = clientInput.readLine()).equals("end")) {
				String[] angles = message.split(",");
				for (int i = 0; i < numOfPlayers; i++) {
					playerAngle.put("Player " + i, Float.parseFloat(angles[i]));
				}
			}
		}

		initPlayers();

		// Create and start extra thread that reads any incoming messages
		Thread thread = new clientListener(clientInput, this);
		thread.start();

		// CREATING OBSTACLES FOR DEBUG PURPOSE
		gWorld.getObstacleList().put(new Vector2(915.2f, 511.8f),
				new Obstacles(gWorld, new Vector2(915.2f, 511.8f), 0));
		gWorld.getObstacleList().put(new Vector2(875.2f, 511.8f),
				new Obstacles(gWorld, new Vector2(875.2f, 511.8f), 1));
	}

	public static MMClient getInstance(GameWorld gWorld, GameRenderer renderer, String serverAddress,
			int serverPort) throws Exception {
		if (instance == null) {
			instance = new MMClient(gWorld, renderer, serverAddress, serverPort);
		}
		return instance;
	}

	// private void createBodies(int i) {
	// if (i == 0) {
	// playerList.add((Murderer)gWorld.getGameCharFac().createCharacter("Murderer", i, gWorld,false));
	// playerList.get(playerList.size-1).getBody().setType(BodyType.KinematicBody);
	// playerList.get(playerList.size-1).spawn(1010 - (((playerList.size - 1)+ 1) * 40), 515, 0);
	//
	// } else {
	// playerList.add((Civilian)gWorld.getGameCharFac().createCharacter("Civilian", i, gWorld,false));
	// playerList.get(playerList.size-1).getBody().setType(BodyType.KinematicBody);
	// playerList.get(playerList.size-1).spawn(1010 - (((playerList.size - 1)+ 1) * 40), 515, 0);
	// }
	// }

	private void initPlayers() {
		System.out.println("Number of players " + numOfPlayers);
		System.out.println("Player list size " + playerList.size());
		for (int i = 0; i < numOfPlayers; i++) {
			playerIsAlive.put("Player " + i, 1);
			playerIsStun.put("Player " + i, 0);
			if (i == id) {
				// If self
				if (i == murdererId) {
					playerType.put("Player " + i, 0);
				} else {
					playerType.put("Player " + i, 1);
				}
				playerList.add(gWorld.createPlayer(playerType.get("Player " + id),
						playerPosition.get("Player " + i)[0], playerPosition.get("Player " + i)[1],
						playerAngle.get("Player " + i)));
			} else {
				// Create opponent bodies
				if (i == murdererId) {
					playerList.add(gWorld.getGameCharFac().createCharacter("Murderer", i, gWorld, false));
					playerList.get(playerList.size() - 1).getBody().setType(BodyType.KinematicBody);
					playerList.get(playerList.size() - 1).spawn(playerPosition.get("Player " + i)[0],
							playerPosition.get("Player " + i)[1], playerAngle.get("Player " + i));
					playerType.put("Player " + i, 0);
				} else {
					playerList.add(gWorld.getGameCharFac().createCharacter("Civilian", i, gWorld, false));
					playerList.get(playerList.size() - 1).getBody().setType(BodyType.KinematicBody);
					playerList.get(playerList.size() - 1).spawn(playerPosition.get("Player " + i)[0],
							playerPosition.get("Player " + i)[1], playerAngle.get("Player " + i));
					playerType.put("Player " + i, 1);
				}

			}
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
		updatePlayerLocation();		
	}
	
	/**Remove item from MMClient item buffer and update server about consumption
	 * @param position
	 */
	public void removeItemLocation(Vector2 position){
		System.out.println("Consume item from buffer");
		itemLocations.consume(new Location(new float[]{position.x,position.y}));
		System.out.println("Send message to server");
		clientOutput.println("item_"+id+"_con_"+Float.toString(position.x)+"_"+Float.toString(position.y));		
	}
	
	private void updatePlayerLocation(){
		//Get player postion
		float angle =gWorld.getPlayer().getBody().getAngle();
		float[] position ={gWorld.getPlayer().getBody().getPosition().x,gWorld.getPlayer().getBody().getPosition().y};
		//if angle and position has changed 
		if ((playerPosition.get("Player "+id)!= position) && (playerAngle.get("Player "+id)!=angle)){
			//Update client Hashmap
			playerPosition.put("Player "+id, position);
			playerAngle.put("Player "+id, angle);
			//Update server
			clientOutput.println("loc_"+id+"_"+Float.toString(position[0])+"_"+Float.toString(position[1])+"_"+Float.toString(angle));
		}
	}
		

	/**
	 * Renders the GameRenderer with other player's move.
	 */
	public void render(OrthographicCamera cam, SpriteBatch batch) {
		for (GameCharacter gc : getPlayerList()) {
			if (gc.isAlive() && !gc.isPlayer())
				gc.render(cam, batch);
		}
	}

	// // FOR DEBUG PURPOSE
	// private void createTrap() {
	// bdef = new BodyDef();
	// fdef = new FixtureDef();
	// bdef.type = BodyType.StaticBody;
	// bdef.position.set(1010, 570);
	// body = gWorld.getWorld().createBody(bdef);
	//
	// CircleShape shape = new CircleShape();
	// shape.setRadius(10);
	// fdef.shape = shape;
	// fdef.isSensor = true;
	// fdef.filter.maskBits = 1;
	//
	// body.createFixture(fdef).setUserData("trap");
	// }
	//

	/**
	 * Create item sprites on the map.
	 * 
	 * @param x
	 *            X coordinate on the map.
	 * @param y
	 *            Y coordinate on the map.
	 */
	private void createItems(float x, float y) {
		ItemSprite is = new ItemSprite(gWorld);
		gWorld.getItemList().put(new Vector2(x, y), is);
		is.spawn(x, y, 0);
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
		WeaponSprite ws = new WeaponSprite(gWorld);
		gWorld.getWeaponList().put(new Vector2(x, y), ws);
		ws.spawn(x, y, 0);
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
		WeaponPartSprite wps = new WeaponPartSprite(gWorld);
		gWorld.getWeaponPartList().put(new Vector2(x, y), wps);
		wps.spawn(x, y, 0);
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
			playerList
					.get(i)
					.getBody()
					.setTransform(playerPosition.get("Player " + i)[0], playerPosition.get("Player " + i)[0],
							playerAngle.get("Player " + i));
		}
	}

	// /**
	// * Updates the locations of all items on the map.
	// */
	// private void itemLocations() {
	// return itemLocations;
	// }
	//
	// /**
	// * Updates the locations of all weapon on the map.
	// */
	// private void weaponLocations() {
	// server.getWeaponLocations();
	// }
	//
	// /**
	// * Updates the locations of all weapon parts on the map.
	// */
	// private void weaponPartLocations() {
	// server.getWeaponPartLocations();
	// }
	//
	// /**
	// * Updates the locations of all traps on the map.
	// */
	// private void trapLocations() {
	// server.getTrapLocations();
	// }

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
		synchronized (clientInput) {
			return clientInput;
		}
	}

	public void setClientInput(BufferedReader clientInput) {
		synchronized (clientInput) {
			this.clientInput = clientInput;
		}
	}

	public PrintWriter getClientOutput() {
		synchronized (clientOutput) {
			return clientOutput;
		}
	}

	public void setClientOutput(PrintWriter clientOutput) {
		synchronized (clientOutput) {
			this.clientOutput = clientOutput;
		}
	}

	public GameWorld getgWorld() {
		return gWorld;
	}

	public void setgWorld(GameWorld gWorld) {
		this.gWorld = gWorld;
	}

	public GameRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}

	public void sendToServer(String message) {
		clientOutput.println(message);
		clientOutput.flush();
	}

	/**
	 * Initialize client socket
	 * 
	 * @throws Exception
	 */
	public void initClientSocket(String address, int port) throws Exception {
		if (address != null) {
			clientSocket = new Socket();
			// Create InetSocketAddress and connect to server socket
			InetAddress addr = InetAddress.getByName(address);
			InetSocketAddress iAddress = new InetSocketAddress(addr, port);
			clientSocket.connect(iAddress);

			setClientInput(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
			setClientOutput(new PrintWriter(clientSocket.getOutputStream(), true));

		} else {
			Gdx.app.log(TAG, "Server Address/Port is null");
			// TODO Request information from server again
		}
	}

	public void closeSocket() throws IOException {
		clientInput.close();
		clientOutput.close();
		clientSocket.close();
	}

	public void handleMessage(String message) {
		String[] msg = message.split("_");
		// if player position update message
		if (msg[0].equals("loc")) {
			float[] position = { Float.parseFloat(msg[2]), Float.parseFloat(msg[3]) };
			float angle = Float.parseFloat(msg[4]);
			playerPosition.put("Player " + msg[1], position);
			playerAngle.put("Player " + msg[1], angle);
			// Get and change position of opponent
			playerList.get(Integer.parseInt(msg[1])).spawn(position[0], position[1], angle);
		}
		
		//If item consumption or production message
		else if(msg[0].equals("item")){
			if (msg[2].equals("con")){
				Vector2 position = new Vector2(Float.parseFloat(msg[3]),Float.parseFloat(msg[4]));
				itemLocations.consume(new Location(new float[]{Float.parseFloat(msg[3]),Float.parseFloat(msg[4])}));
				gWorld.getWorld().destroyBody(gWorld.getItemList().get(position).getBody());
				gWorld.getItemList().remove(position);
			}
		}
		
	}
}

class clientListener extends Thread {
	private BufferedReader input;
	private MMClient client;
	private String msg;

	public clientListener(BufferedReader inputStream, MMClient client) {
		this.input = inputStream;
		this.client = client;
	}

	@Override
	public void run() {
		System.out.println("Starting client listener thread.");
		while (!isInterrupted()) {
			try {
				if ((msg = input.readLine()) != null) {
//					System.out.println("MMClient Message received: "+msg);
//					String message = new String(msg);
					client.handleMessage(msg);
				}
			} catch (Exception e) {
				System.out.println("Error while reading: " + e.getMessage());
			}
		}
	}
}
