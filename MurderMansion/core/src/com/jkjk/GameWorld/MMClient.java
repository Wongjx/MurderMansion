package com.jkjk.GameWorld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.GameObjects.Obstacles;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.GameCharacterFactory;
import com.jkjk.GameObjects.Items.ItemFactory;
import com.jkjk.GameObjects.Items.ItemSprite;
import com.jkjk.GameObjects.Weapons.WeaponFactory;
import com.jkjk.GameObjects.Weapons.WeaponPartSprite;
import com.jkjk.GameObjects.Weapons.WeaponSprite;
import com.jkjk.Host.Helpers.Location;
import com.jkjk.Host.Helpers.ObstaclesHandler;
import com.jkjk.Host.Helpers.SpawnBuffer;
import com.jkjk.MMHelpers.AssetLoader;
import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

/**
 * MMClient listens to input from the Server by the host. Inputs include sharable data such as player
 * position, item spawns and player status. MMClient will also output to the server the changes made by the
 * player.
 * 
 * More importantly, client-side processing will handle all actions by the player (movement, contact). The
 * CONSEQUENCE of the action will be passed to the server, which will retransmit the results to all other
 * clients. Consequences include the removal of an item when picking it up, or change in body position due to
 * movement.
 * 
 * @author LeeJunXiang
 * 
 */
public class MMClient {
//	private static MMClient instance;
	// private final MultiplayerSeissonInfo info;

	private GameWorld gWorld;
	private GameRenderer renderer;
	private ItemFactory itemFac;
	private WeaponFactory weaponFac;
	private GameCharacterFactory gameCharFac;

	private String serverAddress;
	private int serverPort;
	private boolean isGameStart;
	public Socket clientSocket;
	private BufferedReader clientInput;
	private PrintWriter clientOutput;
	private Thread clientListenerThread;

	private float currentPositionX;
	private float currentPositionY;

	private int numOfPlayers;
	private int id;
	private int murdererId;
	private ArrayList<GameCharacter> playerList;

	private final long UPDATES_PER_SEC = 50;
	private long lastUpdated;

	private final ConcurrentHashMap<String, Integer> playerIsAlive; // If 1 ->true; If 0 -> false;
	private final ConcurrentHashMap<String, Integer> playerType; // If 0 -> murderer;If 1 -> civilian; If 2 ->
																	// Ghost
	private final ConcurrentHashMap<String, float[]> playerPosition;
	private final ConcurrentHashMap<String, Float> playerAngle;
	private final ConcurrentHashMap<String, Float> playerVelocity;
	private boolean playerIsInSafeArea;

	// private ArrayList<Location> playerLocations;
	private final SpawnBuffer itemLocations;
	private final SpawnBuffer weaponLocations;
	private final SpawnBuffer weaponPartLocations;

	private ObstaclesHandler obstaclesHandler;

	/**
	 * Constructs the multiplayer world, including creation of opponents.
	 * 
	 * @param gWorld
	 *            GameWorld instance
	 * @param renderer
	 *            GameRenderer instance
	 * @throws Exception
	 */
	public MMClient(GameWorld gWorld, GameRenderer renderer, String serverAddress, int serverPort)
			throws Exception {

		this.gWorld = gWorld;
		this.renderer = renderer;
		itemFac = new ItemFactory();
		weaponFac = new WeaponFactory();
		gameCharFac = new GameCharacterFactory();

		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.isGameStart=false;
		// Connect to server
		initClientSocket(this.serverAddress, this.serverPort);

		// Set cuurent time to last updated time
		this.lastUpdated = System.currentTimeMillis();

		// Receive initialzation parameters
		numOfPlayers = Integer.parseInt(clientInput.readLine());
		id = Integer.parseInt(clientInput.readLine());
		murdererId = Integer.parseInt(clientInput.readLine());

		// System.out.println("Creating item spawn buffers");
		itemLocations = new SpawnBuffer(numOfPlayers * 3);
		weaponLocations = new SpawnBuffer(numOfPlayers);
		weaponPartLocations = new SpawnBuffer(numOfPlayers * 2);
		obstaclesHandler = ObstaclesHandler.getInstance();

		String message;
		// Receive item locations
		if ((message = clientInput.readLine()).equals("itemLocations")) {
			// System.out.println("get item locations");
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
			// System.out.println("get weapon locations");
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
			// System.out.println("get weapon part locations");
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
		// System.out.println("Creating concurrent hashmaps for player condition.");
		playerType = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerIsAlive = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerPosition = new ConcurrentHashMap<String, float[]>(numOfPlayers);
		playerAngle = new ConcurrentHashMap<String, Float>(numOfPlayers);
		playerVelocity = new ConcurrentHashMap<String, Float>(numOfPlayers);
		playerIsInSafeArea = false;

		// Receive spawn positions
		if ((message = clientInput.readLine()).equals("spawnPositions")) {
			// System.out.println("get spawn positions");
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
			// System.out.println("get spawn angles");
			while (!(message = clientInput.readLine()).equals("end")) {
				String[] angles = message.split(",");
				for (int i = 0; i < numOfPlayers; i++) {
					playerAngle.put("Player " + i, Float.parseFloat(angles[i]));
				}
			}
		}

		initPlayers();
		createObstacles();

		// Create and start extra thread that reads any incoming messages
		Thread thread = new clientListener(clientInput, this);
		this.clientListenerThread = thread;
		thread.start();
		
		// Send ready message to server
		clientOutput.println("ready_" + id);

		// // CREATE SPRITES FOR TESTING
//		 ItemSprite temporaryItem = new ItemSprite(gWorld);
//		 gWorld.getItemList().put(new Vector2(800f, 490), temporaryItem);
//		 temporaryItem.spawn(800f, 490, 0);
//		 WeaponSprite tempWeap = new WeaponSprite(gWorld);
//		 gWorld.getWeaponList().put(new Vector2(750f, 490), tempWeap);
//		 tempWeap.spawn(750f, 490, 0);
		//
		// for (int i = 0; i < 8; i++) {
		// createWeaponParts(750 + (20 * i), 460);
		// }

		// // CREATING ITEMSPRITE FOR DEBUG PURPOSE
		// ItemSprite is = new ItemSprite(gWorld);
		// Vector2 location = new Vector2(800f, 540);
		// gWorld.getItemList().put(location, is);
		// is.spawn(location.x, location.y, 0);
		// // CREATING WEAPONSPRITE FOR DEBUG PURPOSE
		// WeaponSprite ws = new WeaponSprite(gWorld);
		// Vector2 location2 = new Vector2(750f, 540);
		// gWorld.getWeaponList().put(location2, ws);
		// ws.spawn(location2.x, location2.y, 0);
		//
		// gWorld.createTrap(700f, 540);

	}

//	public static MMClient getInstance(GameWorld gWorld, GameRenderer renderer, String serverAddress,
//			int serverPort) throws Exception {
//		if (instance == null) {
//			System.out.println("New instance of MMClient made!");
//			instance = new MMClient(gWorld, renderer, serverAddress, serverPort);
//		}
//		return instance;
//	}

	/**
	 * Initialize client socket
	 * 
	 * @throws Exception
	 */
	public void initClientSocket(String address, int port) throws Exception {
		if (address != null) {
			clientSocket = new Socket();
			clientSocket.setSoTimeout(30000);
			// Create InetSocketAddress and connect to server socket
			InetAddress addr = InetAddress.getByName(address);
			InetSocketAddress iAddress = new InetSocketAddress(addr, port);
			clientSocket.connect(iAddress);

			setClientInput(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
			setClientOutput(new PrintWriter(clientSocket.getOutputStream(), true));

		} else {
			// TODO Request information from server again
			
		}
	}

	private void initPlayers() {
		System.out.println("Number of players " + numOfPlayers);
		System.out.println("Player list size " + playerList.size());
		for (int i = 0; i < numOfPlayers; i++) {
			playerIsAlive.put("Player " + i, 1);
			if (i == id) {
				System.out.println("I'M PLAYER NUMBER " + id);
				// If self
				if (i == murdererId) {
					playerType.put("Player " + i, 0);
				} else {
					playerType.put("Player " + i, 1);
				}
				playerList.add(gWorld.createPlayer(playerType.get("Player " + id),
						playerPosition.get("Player " + i)[0], playerPosition.get("Player " + i)[1],
						playerAngle.get("Player " + i), i));
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

	public void createObstacles() {
		int i = 0;
		for (Location ob : obstaclesHandler.getObstacles()) {
			if (i == 0)
				gWorld.getObstacleList().put(new Vector2(ob.get()[0], ob.get()[1]),
						new Obstacles(gWorld, new Vector2(ob.get()[0], ob.get()[1]), 0));
			else

				gWorld.getObstacleList().put(new Vector2(ob.get()[0], ob.get()[1]),
						new Obstacles(gWorld, new Vector2(ob.get()[0], ob.get()[1]), 1));
			i++;
		}
	}

	/**
	 * Updates the GameWorld with other player's actions, such as player position, item positions and
	 * item/weapon use.
	 */
	public void update() {
		for (GameCharacter gc : playerList) {
			if (gc.isAlive() && !gc.isPlayer())
				gc.update();
		}
		updatePlayerLocation();
		updatePlayerIsinSafeArea();
	}

	public void produceItemLocation(Vector2 position) {
		clientOutput.println("item_" + id + "_pro_" + Float.toString(position.x) + "_"
				+ Float.toString(position.y));
	}

	public void produceWeaponLocation(Vector2 position) {
		clientOutput.println("weapon_" + id + "_pro_" + Float.toString(position.x) + "_"
				+ Float.toString(position.y));
	}

	public void produceTrapLocation(float x, float y) {
		clientOutput.println("trap_" + id + "_pro_" + Float.toString(x) + "_" + Float.toString(y));
	}

	public void addWeaponPartCollected() {
		clientOutput.println("addWeaponPart");
	}

	public void removeTrapLocation(float x, float y) {
		clientOutput.println("trap_" + id + "_con_" + Float.toString(x) + "_" + Float.toString(y));
	}

	/**
	 * Remove item from MMClient item buffer and update server about consumption
	 * 
	 * @param position
	 */
	public void removeItemLocation(Vector2 position) {
		itemLocations.consume(new Location(new float[] { position.x, position.y }));
		clientOutput.println("item_" + id + "_con_" + Float.toString(position.x) + "_"
				+ Float.toString(position.y));
	}

	/**
	 * Remove item from MMClient weapon buffer and update server about consumption
	 * 
	 * @param position
	 */
	public void removeWeaponLocation(Vector2 position) {
		weaponLocations.consume(new Location(new float[] { position.x, position.y }));
		clientOutput.println("weapon_" + id + "_con_" + Float.toString(position.x) + "_"
				+ Float.toString(position.y));
	}

	/**
	 * Remove item from MMClient weapon part buffer and update server about consumption
	 * 
	 * @param position
	 */
	public void removeWeaponPartLocation(Vector2 position) {
		weaponPartLocations.consume(new Location(new float[] { position.x, position.y }));
		clientOutput.println("weaponpart_" + id + "_con_" + Float.toString(position.x) + "_"
				+ Float.toString(position.y));
	}
	
	/** Update MMServer that player is at game screen and ready to start game
	 * 
	 */
	public void updatePlayerIsReady(){
		clientOutput.println("ready_"+id);
	}

	/**
	 * Called to update MMClients position and angle in from gWorld. Updates server if there is a change.
	 * 
	 */
	private void updatePlayerLocation() {
		if (System.currentTimeMillis() - lastUpdated <= (1 / UPDATES_PER_SEC * 1000)) {
			return;
		}
		// Get player postion
		float angle = gWorld.getPlayer().getBody().getAngle();
		float[] position = { gWorld.getPlayer().getBody().getPosition().x,
				gWorld.getPlayer().getBody().getPosition().y };
		float velocity = gWorld.getPlayer().getBody().getLinearVelocity().x;
		// if angle and position has changed
		if ((playerPosition.get("Player " + id) != position) && (playerAngle.get("Player " + id) != angle)) {
			// Update client Hashmap
			playerPosition.put("Player " + id, position);
			playerAngle.put("Player " + id, angle);
			playerVelocity.put("Player " + id, velocity);
			// Update server
			clientOutput.println("loc_" + id + "_" + Float.toString(position[0]) + "_"
					+ Float.toString(position[1]) + "_" + Float.toString(angle) + "_"
					+ Float.toString(velocity));
			clientOutput.flush();
			lastUpdated = System.currentTimeMillis();
		}
	}

	private void updatePlayerIsinSafeArea() {
		if (gWorld.isInSafeArea() != playerIsInSafeArea) {
			playerIsInSafeArea = gWorld.isInSafeArea();
			if (gWorld.isInSafeArea())
				clientOutput.println("safe_" + id + "_" + 1);
			else
				clientOutput.println("safe_" + id + "_" + 0);
			clientOutput.flush();
		}
	}

	/**
	 * Update server about change in player's stun status
	 * 
	 * @param playerID
	 *            ID of player status to change
	 * @param value
	 *            If 1 -> true; If 0 -> false;
	 */
	public void updatePlayerIsStun(int playerID, int value) {
		clientOutput.println("stun_" + id + "_" + playerID + "_" + value);
	}

	/**
	 * Update server about change in player's alive status
	 * 
	 * @param playerID
	 *            ID of player status to change
	 * @param value
	 *            If 1 -> true; If 0 -> false;
	 */
	public void updatePlayerIsAlive(int playerID, int value) {
		playerIsAlive.put("Player " + id, value);
		clientOutput.println("alive_" + id + "_" + playerID + "_" + value);
	}

	/**
	 * <<<<<<< HEAD ======= <<<<<<< HEAD >>>>>>> 093da5221d56ff4aaead1b053aa98e22d2be4d11 Update server about
	 * change in player's use item
	 * 
	 * @param playerID
	 *            ID of player status to change
	 * @param value
	 *            If 1 -> true; If 0 -> false;
	 */
	public void updatePlayerUseItem() {
		clientOutput.println("useItem_" + id);
	}

	/**
	 * Update server about change in player's use weapon
	 * 
	 * @param playerID
	 *            ID of player status to change
	 * @param value
	 *            If 1 -> true; If 0 -> false;
	 */
	public void updatePlayerUseWeapon() {
		clientOutput.println("useWeapon_" + id);
	}

	/**
	 * Update server about change in player's use weapon
	 * 
	 * @param playerID
	 *            ID of player status to change
	 * @param value
	 *            If 1 -> true; If 0 -> false;
	 */
	public void updatePlayerUseAbility() {
		clientOutput.println("useAbility_" + id);
	}

	/**
	 * <<<<<<< HEAD ======= ======= >>>>>>> Broken_menu >>>>>>> 093da5221d56ff4aaead1b053aa98e22d2be4d11
	 * Update server about change in player's type
	 * 
	 * @param playerID
	 *            ID of player status to change
	 * @param value
	 *            If 0 -> murderer; If 1 -> civilian; If 2-> Ghost;
	 */
	public void updatePlayerType(int playerID, int value) {
		playerType.put("Player " + id, value);
		clientOutput.println("type_" + id + "_" + playerID + "_" + value);
	}

	/**
	 * Renders the GameRenderer with other player's move.
	 */
	public void render(OrthographicCamera cam, SpriteBatch batch) {
		for (GameCharacter gc : playerList) {
			if (gc.isAlive() && !gc.isPlayer())
				gc.render(cam, batch);
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
	 * Produces knife body from the player that used the knife.
	 */
	private void itemUsed(int id) {
		if (playerType.get("Player " + id) == 0) {
			playerList.get(id).addItem(itemFac.createItem("Trap", gWorld, this, playerList.get(id)));
		} else if (playerType.get("Player " + id) == 1) {
			playerList.get(id).addItem(itemFac.createItem("Disarm Trap", gWorld, this, playerList.get(id)));
		}
		playerList.get(id).useItem();
	}

	/**
	 * Player uses weapon
	 */
	private void weaponUsed(int id) {
		if (playerType.get("Player " + id) == 0) {
			playerList.get(id).addWeapon(weaponFac.createWeapon("Knife", gWorld, playerList.get(id)));
		} else if (playerType.get("Player " + id) == 1) {
			if (playerList.get(id).getWeapon() == null) {
				playerList.get(id).addWeapon(weaponFac.createWeapon("Bat", gWorld, playerList.get(id)));
			}
		}
		playerList.get(id).useWeapon();
	}

	/**
	 * Creates shotgun for all players in their weapon.
	 */
	private void createShotgun() {
		for (GameCharacter gc : playerList) {
			if (gc.getType() == "Civilian") {
				gc.addWeapon(weaponFac.createWeapon("Shotgun", gWorld, gc));
			}
		}
	}

	/**
	 * Player uses ability
	 */
	private void abilityUsed(int id) {
		System.out.println("WHO USED ABILITY? PLAYER " + id + " DID!");
		playerList.get(id).useAbility();
	}
	
	public boolean getIsGameStart(){
		return isGameStart;
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

	public int getId() {
		return id;
	}

	public void sendToServer(String message) {
		clientOutput.println(message);
		clientOutput.flush();
	}

	public void closeSocket() throws IOException {
		clientInput.close();
		clientOutput.close();
		clientSocket.close();
	}

	/**
	 * @param parseInt
	 */
	private void killPlayer(int id) {
		currentPositionX = playerList.get(id).getBody().getPosition().x;
		currentPositionY = playerList.get(id).getBody().getPosition().y;

		playerList.get(id).die();
		gWorld.getWorld().destroyBody(playerList.get(id).getBody());
		playerList.set(id, gameCharFac.createCharacter("Ghost", id, gWorld, false));
		playerList.get(id).spawn(playerPosition.get("Player " + id)[0],
				playerPosition.get("Player " + id)[1], playerAngle.get("Player " + id));

		playerList.get(id).set_deathPositionX(currentPositionX);
		playerList.get(id).set_deathPositionY(currentPositionY);
	}

	public void handleMessage(String message) {
		String[] msg = message.split("_");
		//if start game message
		if(msg[0].equals("startgame")){
			this.isGameStart=true;
			System.out.println("All players ready. Start GAME!!");
		}

		// if player position update message
		else if (msg[0].equals("loc")) {
			float[] position = { Float.parseFloat(msg[2]), Float.parseFloat(msg[3]) };
			float angle = Float.parseFloat(msg[4]);
			float velocity = Float.parseFloat(msg[5]);
			playerPosition.put("Player " + msg[1], position);
			playerAngle.put("Player " + msg[1], angle);
			playerVelocity.put("Player " + msg[1], velocity);
			// Get and change position of opponent
			playerList.get(Integer.parseInt(msg[1])).setPosition(position[0], position[1], angle, velocity);

		} else if (msg[0].equals("pos")) {
			// System.out.println("Change player " + msg[1] + " position");
			float[] position = { Float.parseFloat(msg[2]), Float.parseFloat(msg[3]) };
			playerPosition.put("Player " + Integer.parseInt(msg[1]), position);
		} else if (msg[0].equals("ang")) {
			// System.out.println("Change player " + msg[1] + " angle");
			float angle = Float.parseFloat(msg[2]);
			playerAngle.put("Player " + Integer.parseInt(msg[1]), angle);
		} else if (msg[0].equals("vel")) {
			// System.out.println("Change player " + msg[1] + " velocity");
			float velocity = Float.parseFloat(msg[2]);
			playerVelocity.put("Player " + Integer.parseInt(msg[1]), velocity);
		}

		// Player Status updates
		else if (msg[0].equals("type")) {
			playerType.put("Player " + Integer.parseInt(msg[2]), Integer.parseInt(msg[3]));
		} else if (msg[0].equals("alive")) {
			playerIsAlive.put("Player " + Integer.parseInt(msg[2]), Integer.parseInt(msg[3]));
			killPlayer(Integer.parseInt(msg[2]));
		} else if (msg[0].equals("stun")) {
			playerList.get(Integer.parseInt(msg[2])).stun();
		} else if (msg[0].equals("useItem")) {
			itemUsed(Integer.parseInt(msg[1]));
		} else if (msg[0].equals("useWeapon")) {
			weaponUsed(Integer.parseInt(msg[1]));
		} else if (msg[0].equals("useAbility")) {
			abilityUsed(Integer.parseInt(msg[1]));
		} else if (msg[0].equals("weaponPartCollected")) {
			gWorld.addNumOfWeaponPartsCollected();
		} else if (msg[0].equals("createShotgun")) {
			createShotgun();
		}

		// If item consumption or production message
		else if (msg[0].equals("item")) {
			if (msg[2].equals("con")) {
				System.out.println("Consume item");
				Vector2 position = new Vector2(Float.parseFloat(msg[3]), Float.parseFloat(msg[4]));
				itemLocations.consume(new Location(new float[] { Float.parseFloat(msg[3]),
						Float.parseFloat(msg[4]) }));
				gWorld.getWorld().destroyBody(gWorld.getItemList().get(position).getBody());
				gWorld.getItemList().remove(position);

			} else if (msg[2].equals("pro")) {
				System.out.println("Produce item");
				itemLocations.produce(new Location(new float[] { Float.parseFloat(msg[3]),
						Float.parseFloat(msg[4]) }));
				createItems(Float.parseFloat(msg[3]), Float.parseFloat(msg[4]));
			}
		} else if (msg[0].equals("weapon")) {
			if (msg[2].equals("con")) {
				System.out.println("Consume weapon");
				Vector2 position = new Vector2(Float.parseFloat(msg[3]), Float.parseFloat(msg[4]));
				weaponLocations.consume(new Location(new float[] { Float.parseFloat(msg[3]),
						Float.parseFloat(msg[4]) }));
				gWorld.getWorld().destroyBody(gWorld.getWeaponList().get(position).getBody());
				gWorld.getWeaponList().remove(position);
			} else if (msg[2].equals("pro")) {
				System.out.println("Produce weapon");
				weaponLocations.produce(new Location(new float[] { Float.parseFloat(msg[3]),
						Float.parseFloat(msg[4]) }));
				createWeapons(Float.parseFloat(msg[3]), Float.parseFloat(msg[4]));
			}
		} else if (msg[0].equals("weaponpart")) {
			if (msg[2].equals("con")) {
				System.out.println("Consume weaponpart");
				Vector2 position = new Vector2(Float.parseFloat(msg[3]), Float.parseFloat(msg[4]));
				weaponPartLocations.consume(new Location(new float[] { Float.parseFloat(msg[3]),
						Float.parseFloat(msg[4]) }));
				gWorld.getWorld().destroyBody(gWorld.getWeaponPartList().get(position).getBody());
				gWorld.getWeaponPartList().remove(position);
			} else if (msg[2].equals("pro")) {
				System.out.println("Produce weaponpart");
				weaponPartLocations.produce(new Location(new float[] { Float.parseFloat(msg[3]),
						Float.parseFloat(msg[4]) }));
				createWeaponParts(Float.parseFloat(msg[3]), Float.parseFloat(msg[4]));
			}
		} else if (msg[0].equals("trap")) {
			if (msg[2].equals("con")) {
				System.out.println("Consume trap");
				Vector2 position = new Vector2(Float.parseFloat(msg[3]), Float.parseFloat(msg[4]));
				if (gWorld.getTrapList().containsKey(position)) {
					gWorld.setTrapToRemove(gWorld.getTrapList().get(position).getBody());
				}
			} else if (msg[2].equals("pro")) {
				System.out.println("Produce trap");
				if (Integer.parseInt(msg[1]) != id) {
					gWorld.createTrap(Float.parseFloat(msg[3]), Float.parseFloat(msg[4]));
				}
			}
		}

		else if (msg[0].equals("obstacle")) {
			System.out.println("Remove obstacle @ x:" + msg[1] + " y: " + msg[2]);
			Vector2 location = new Vector2(Float.parseFloat(msg[1]), Float.parseFloat(msg[2]));
			gWorld.removeObstacle(location);
			
			if (gWorld.getObstacleList().isEmpty())
				AssetLoader.obstacleSoundmd.play();
			else
				AssetLoader.obstacleSFX();
		}

		else if (msg[0].equals("lightning")) {
			gWorld.lightningStrike();
		}

		else if (msg[0].equals("win")) {
			if (msg[1].equals("civilian")) {
				gWorld.setCivWin(true);
			} else if (msg[1].equals("murderer")) {
				gWorld.setMurWin(true);
			}
		}
	}
	public void endSession() throws IOException{
//		instance=null;
//		System.out.println("Interrupt everythang");
		this.clientListenerThread.interrupt();
//		System.out.println("Closing all lose holes");
		this.clientSocket.close();
		System.out.println("MMClient seisson ended.");

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
					// System.out.println("MMClient Message received: " + msg);
					// String message = new String(msg);
					client.handleMessage(msg);
				}
			}catch(SocketException e){
				System.out.println("Client error while reading: " + e.getMessage());
				break;
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("Client error while reading: " + e.getMessage());
//				break;
			}
		}
		
		System.out.println("Client listener thread closed.");
		client.getgWorld().setCivWin(true);
		
	}
}
