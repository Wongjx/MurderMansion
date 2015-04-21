package com.jkjk.GameWorld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.GameObjects.Duration;
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
import com.jkjk.MMHelpers.AssetLoader;

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

	private float selfAngle;
	private float[] selfPosition;
	private float selfVelocityX;
	private float selfVelocityY;
	private float[] selfVelocity;

	private float currentPositionX;
	private float currentPositionY;

	private int numOfPlayers;
	private int id;
	private final String mName;
	private String[] clientNames;
	private int murdererId;
	private ArrayList<GameCharacter> playerList;

	private final long UPDATES_PER_SEC = 10;
	private long lastUpdated;

	private final ConcurrentHashMap<String, Integer> playerIsAlive; // If 1 ->true; If 0 -> false;
	private final ConcurrentHashMap<String, Integer> playerType; // If 0 -> murderer;If 1 -> civilian; If 2 ->
																	// Ghost
	private final ConcurrentHashMap<String, float[]> playerPosition;
	private final ConcurrentHashMap<String, Float> playerAngle;
	private final ConcurrentHashMap<String, float[]> playerVelocity;
	private boolean playerIsInSafeArea;

	private ObstaclesHandler obstaclesHandler;

	private boolean tutorial;
	private GameCharacter dummy;

	private float storeLightValue;
	private Duration lightningDuration;
	private Random random;

	private ConcurrentLinkedQueue<float[]> itemSpawnQueue;
	private ConcurrentLinkedQueue<float[]> weaponSpawnQueue;
	private ConcurrentLinkedQueue<float[]> weaponPartSpawnQueue;
	private ConcurrentLinkedQueue<float[]> trapSpawnQueue;
	private ConcurrentLinkedQueue<Vector2> itemConsumeQueue;
	private ConcurrentLinkedQueue<Vector2> weaponConsumeQueue;
	private ConcurrentLinkedQueue<Vector2> weaponPartConsumeQueue;
	private ConcurrentLinkedQueue<Vector2> obstacleConsumeQueue;
	private ConcurrentLinkedQueue<Boolean> lightningQueue;

	/**
	 * Constructs the multiplayer world, including creation of opponents.
	 * 
	 * @param gWorld
	 *            GameWorld instance
	 * @param renderer
	 *            GameRenderer instance
	 * @throws Exception
	 */
	public MMClient(GameWorld gWorld, GameRenderer renderer, String serverAddress, int serverPort,
			String participantId, String mName, boolean tutorial) throws Exception {

		this.gWorld = gWorld;
		this.renderer = renderer;
		this.tutorial = tutorial;
		itemFac = new ItemFactory();
		weaponFac = new WeaponFactory();
		gameCharFac = new GameCharacterFactory();
		random = new Random();

		this.mName = mName;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.isGameStart = false;
		// Connect to server
		initClientSocket(this.serverAddress, this.serverPort);

		// Set cuurent time to last updated time
		this.lastUpdated = System.currentTimeMillis();

		// Send client participant id to server
		this.clientOutput.println(mName);
		System.out.println("My name is : " + mName);

		// Receive initialzation parameters
		numOfPlayers = Integer.parseInt(clientInput.readLine());
		id = Integer.parseInt(clientInput.readLine());
		murdererId = Integer.parseInt(clientInput.readLine());

		// Intialize String[] for participant names
		this.clientNames = new String[numOfPlayers];

		lightningDuration = new Duration(500);

		obstaclesHandler = new ObstaclesHandler();
		itemSpawnQueue = new ConcurrentLinkedQueue<float[]>();
		weaponSpawnQueue = new ConcurrentLinkedQueue<float[]>();
		weaponPartSpawnQueue = new ConcurrentLinkedQueue<float[]>();
		trapSpawnQueue = new ConcurrentLinkedQueue<float[]>();
		itemConsumeQueue = new ConcurrentLinkedQueue<Vector2>();
		weaponConsumeQueue = new ConcurrentLinkedQueue<Vector2>();
		weaponPartConsumeQueue = new ConcurrentLinkedQueue<Vector2>();
		obstacleConsumeQueue = new ConcurrentLinkedQueue<Vector2>();
		lightningQueue = new ConcurrentLinkedQueue<Boolean>();

		String message;
		// Receive item locations
		if ((message = clientInput.readLine()).equals("itemLocations")) {
			// System.out.println("get item locations");
			while (!(message = clientInput.readLine()).equals("end")) {
				String[] locations = message.split("_");
				for (String coordinates : locations) {
					String[] XY = coordinates.split(",");
					// Spawn item inside game world
					createItems(new float[] { Float.parseFloat(XY[0]), Float.parseFloat(XY[1]) });
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
					// Spawn weapon in game world
					createWeapons(new float[] { Float.parseFloat(XY[0]), Float.parseFloat(XY[1]) });
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
					// spawn weapon parts in game world
					createWeaponParts(new float[] { Float.parseFloat(XY[0]), Float.parseFloat(XY[1]) });
				}
			}
		}

		playerList = new ArrayList<GameCharacter>(numOfPlayers);
		// System.out.println("Creating concurrent hashmaps for player condition.");
		playerType = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerIsAlive = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerPosition = new ConcurrentHashMap<String, float[]>(numOfPlayers);
		playerAngle = new ConcurrentHashMap<String, Float>(numOfPlayers);
		playerVelocity = new ConcurrentHashMap<String, float[]>(numOfPlayers);
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
		
		System.out.println("Try to init players.");
		initPlayers();
		System.out.println("Player initialised.");
		createObstacles();


		// Get participant ids from server
		if ((message = clientInput.readLine()).equals("clientNames")) {
			System.out.println("Get client names");
			while (!(message = clientInput.readLine()).equals("end")) {
				String[] ids = message.split("_");
				for (int i = 0; i < numOfPlayers; i++) {
					clientNames[i] = ids[i];
				}
			}
		}
		

		// Create and start extra thread that reads any incoming messages
		Thread thread = new clientListener(clientInput, this);
		this.clientListenerThread = thread;
		thread.start();

	}

	/**
	 * Initialize client socket
	 * 
	 * @throws Exception
	 */
	public void initClientSocket(String address, int port) throws Exception {
		if (address != null) {
			clientSocket = new Socket();

			// Time out in 60 seconds
			clientSocket.setSoTimeout(10000);
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
//		System.out.println("Player list size " + playerList.size());
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
				System.out.println("Player made");
			} else {
				// Create opponent bodies
				if (i == murdererId) {
					playerList.add(gWorld.getGameCharFac().createCharacter("Murderer", i, gWorld, false));
					playerList.get(playerList.size() - 1).getBody().setType(BodyType.KinematicBody);
					playerList.get(playerList.size() - 1).spawn(playerPosition.get("Player " + i)[0],
							playerPosition.get("Player " + i)[1], playerAngle.get("Player " + i));
					playerType.put("Player " + i, 0);
					System.out.println("Player made");
				} else {
					playerList.add(gWorld.getGameCharFac().createCharacter("Civilian", i, gWorld, false));
					playerList.get(playerList.size() - 1).getBody().setType(BodyType.KinematicBody);
					playerList.get(playerList.size() - 1).spawn(playerPosition.get("Player " + i)[0],
							playerPosition.get("Player " + i)[1], playerAngle.get("Player " + i));
					playerType.put("Player " + i, 1);
					System.out.println("Player made");
				}
			}
		}
		System.out.println("Init players end" );
	}

	public GameCharacter createTutorialDummy() {
		playerType.put("Player " + 100, 1);
		dummy = gameCharFac.createCharacter("Civilian", 100, gWorld, false);
		dummy.getBody().setType(BodyType.KinematicBody);
		dummy.getBody().getFixtureList().get(0).setUserData("dummy");
		dummy.spawn(gWorld.getPlayer().getBody().getPosition().x - 60, gWorld.getPlayer().getBody()
				.getPosition().y, 0);
		playerList.add(dummy);
		gWorld.setDummy(dummy);
		return dummy;
	}

	/**
	 * If 0 = DEAD; If 1 = ALIVE;
	 */
	public ConcurrentHashMap<String, Integer> get_playerIsAlive() {
		return playerIsAlive;
	}

	/**
	 * If 0 = MURDERER; If 1 = CIVILIAN; If 2 = GHOST;
	 */
	public ConcurrentHashMap<String, Integer> get_playerType() {
		return playerType;
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

	public String[] getParticipantNames() {
		return clientNames;
	}

	/**
	 * Updates the GameWorld with other player's actions, such as player position, item positions and
	 * item/weapon use.
	 */
	public void update() {
		for (GameCharacter gc : playerList) {
			if (gc.isAlive() && !gc.isPlayer()) {
				gc.update();
			} else if (!gc.isAlive() && !gc.isPlayer()) {
				if (!tutorial) {
					currentPositionX = playerList.get(gc.getId()).getBody().getPosition().x;
					currentPositionY = playerList.get(gc.getId()).getBody().getPosition().y;

					gWorld.getWorld().destroyBody(playerList.get(gc.getId()).getBody());
					playerList.set(gc.getId(),
							gameCharFac.createCharacter("Ghost", gc.getId(), gWorld, false));
					playerList.get(gc.getId()).spawn(playerPosition.get("Player " + gc.getId())[0],
							playerPosition.get("Player " + gc.getId())[1],
							playerAngle.get("Player " + gc.getId()));

					playerList.get(gc.getId()).set_deathPositionX(currentPositionX);
					playerList.get(gc.getId()).set_deathPositionY(currentPositionY);
				} else {
					System.out.println("DUMMY IS DYING");
					playerList.remove(1);
					currentPositionX = dummy.getBody().getPosition().x;
					currentPositionY = dummy.getBody().getPosition().y;

					gWorld.getWorld().destroyBody(dummy.getBody());
					dummy = gameCharFac.createCharacter("Ghost", dummy.getId(), gWorld, false);
					dummy.set_deathPositionX(currentPositionX);
					dummy.set_deathPositionY(currentPositionY);
					dummy.getBody().getFixtureList().get(0).setUserData("dummy");
					dummy.getBody().setType(BodyType.KinematicBody);
					dummy.spawn(0, 0, 0);
					gWorld.setDummy(dummy);
					playerList.add(dummy);
				}
			}
		}

		if (lightningDuration.isCountingDown()) {
			lightningDuration.update();
			if (!lightningDuration.isCountingDown()) {
				gWorld.getPlayer().setAmbientLightValue(storeLightValue);
			}
		}

		if (!itemSpawnQueue.isEmpty()) {
			createItems(itemSpawnQueue.poll());
		}
		if (!weaponSpawnQueue.isEmpty()) {
			createWeapons(weaponSpawnQueue.poll());
		}
		if (!weaponPartSpawnQueue.isEmpty()) {
			createWeaponParts(weaponPartSpawnQueue.poll());
		}
		if (!trapSpawnQueue.isEmpty()) {
			gWorld.createTrap(trapSpawnQueue.poll());
		}
		if (!itemConsumeQueue.isEmpty()) {
			consumeItems(itemConsumeQueue.poll());
		}
		if (!weaponConsumeQueue.isEmpty()) {
			consumeWeapons(weaponConsumeQueue.poll());
		}
		if (!weaponPartConsumeQueue.isEmpty()) {
			consumeWeaponParts(weaponPartConsumeQueue.poll());
		}
		if (!obstacleConsumeQueue.isEmpty()) {
			gWorld.removeObstacle(obstacleConsumeQueue.poll());
		}
		if (!lightningQueue.isEmpty()) {
			lightningStrike();
			lightningQueue.poll();
		}
		updatePlayerLocation();
		updatePlayerIsinSafeArea();
	}

	public void consumeItems(Vector2 position) {
		gWorld.getWorld().destroyBody(gWorld.getItemList().get(position).getBody());
		gWorld.getItemList().remove(position);
	}

	public void consumeWeapons(Vector2 position) {
		gWorld.getWorld().destroyBody(gWorld.getWeaponList().get(position).getBody());
		gWorld.getWeaponList().remove(position);
	}

	public void consumeWeaponParts(Vector2 position) {
		gWorld.getWorld().destroyBody(gWorld.getWeaponPartList().get(position).getBody());
		gWorld.getWeaponPartList().remove(position);
	}

	public void lightningStrike() {
		lightningDuration = new Duration(300 + random.nextInt(500));
		lightningDuration.startCountdown();
		storeLightValue = gWorld.getPlayer().getAmbientLightValue();
		gWorld.getPlayer().setAmbientLightValue(0.8f);
		AssetLoader.lightningSound.play(AssetLoader.VOLUME);
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
		// itemLocations.consume(new Location(new float[] { position.x, position.y }));
		clientOutput.println("item_" + id + "_con_" + Float.toString(position.x) + "_"
				+ Float.toString(position.y));
	}

	/**
	 * Remove item from MMClient weapon buffer and update server about consumption
	 * 
	 * @param position
	 */
	public void removeWeaponLocation(Vector2 position) {
		// weaponLocations.consume(new Location(new float[] { position.x, position.y }));
		clientOutput.println("weapon_" + id + "_con_" + Float.toString(position.x) + "_"
				+ Float.toString(position.y));
	}

	/**
	 * Remove item from MMClient weapon part buffer and update server about consumption
	 * 
	 * @param position
	 */
	public void removeWeaponPartLocation(Vector2 position) {
		// weaponPartLocations.consume(new Location(new float[] { position.x, position.y }));
		clientOutput.println("weaponpart_" + id + "_con_" + Float.toString(position.x) + "_"
				+ Float.toString(position.y));
	}

	/**
	 * Update MMServer that player is at game screen and ready to start game
	 * 
	 */
	public void updatePlayerIsReady() {
		clientOutput.println("ready_" + id);
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
		selfAngle = gWorld.getPlayer().getBody().getAngle();
		selfPosition = new float[] { gWorld.getPlayer().getBody().getPosition().x,
				gWorld.getPlayer().getBody().getPosition().y };
		selfVelocityX = gWorld.getPlayer().getBody().getLinearVelocity().x;
		selfVelocityY = gWorld.getPlayer().getBody().getLinearVelocity().y;
		selfVelocity = new float[] { selfVelocityX, selfVelocityY };
		// if angle and position has changed
		if ((playerPosition.get("Player " + id) != selfPosition)
				&& (playerAngle.get("Player " + id) != selfAngle)
				|| (playerVelocity.get("Player" + id) != selfVelocity)) {
			// Update client Hashmap
			playerPosition.put("Player " + id, selfPosition);
			playerAngle.put("Player " + id, selfAngle);
			playerVelocity.put("Player " + id, new float[] { selfVelocityX, selfVelocityY });
			// Update server
			clientOutput.println("loc_" + id + "_" + Float.toString(selfPosition[0]) + "_"
					+ Float.toString(selfPosition[1]) + "_" + Float.toString(selfAngle) + "_"
					+ Float.toString(selfVelocityX) + "_" + Float.toString(selfVelocityY));
			clientOutput.flush();
			lastUpdated = System.currentTimeMillis();
		}
	}

	private void updatePlayerIsinSafeArea() {
		if (gWorld.isInSafeArea() != playerIsInSafeArea) {
			playerIsInSafeArea = gWorld.isInSafeArea();
			if (playerIsInSafeArea)
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
	private void createItems(float[] loc) {
		ItemSprite is = new ItemSprite(gWorld);
		gWorld.getItemList().put(new Vector2(loc[0], loc[1]), is);
		is.spawn(loc[0], loc[1], 0);
	}

	/**
	 * Create weapon sprites on the map.
	 * 
	 * @param x
	 *            X coordinate on the map.
	 * @param y
	 *            Y coordinate on the map.
	 */
	private void createWeapons(float[] loc) {
		WeaponSprite ws = new WeaponSprite(gWorld);
		gWorld.getWeaponList().put(new Vector2(loc[0], loc[1]), ws);
		ws.spawn(loc[0], loc[1], 0);
	}

	/**
	 * Create weapon part sprites on the map.
	 * 
	 * @param x
	 *            X coordinate on the map.
	 * @param y
	 *            Y coordinate on the map.
	 */
	private void createWeaponParts(float[] loc) {
		WeaponPartSprite wps = new WeaponPartSprite(gWorld);
		gWorld.getWeaponPartList().put(new Vector2(loc[0], loc[1]), wps);
		wps.spawn(loc[0], loc[1], 0);
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
		playerList.get(id).useAbility();
	}

	public boolean getIsGameStart() {
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

	public int getMurdererId() {
		return murdererId;
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
		playerList.get(id).die();
	}

	public void handleMessage(String message) {
		String[] msg = message.split("_");
		// if start game message
		if (msg[0].equals("startgame")) {
			this.isGameStart = true;
			System.out.println("All players ready. Start GAME!!");
		} else if (msg[0].equals("connection")) {
			if (msg[1].equals("server")) {
				if (msg[2].equals("check")) {
					System.out.println("Client " + id + " received server connection check. Replying now.");
					this.clientOutput.println("connection_" + id + "_ok");
					this.clientOutput.flush();
				}
			}
		}

		// if player position update message
		else if (msg[0].equals("loc")) {
			float[] position = { Float.parseFloat(msg[2]), Float.parseFloat(msg[3]) };
			float angle = Float.parseFloat(msg[4]);
			float velocityX = Float.parseFloat(msg[5]);
			float velocityY = Float.parseFloat(msg[6]);
			playerPosition.put("Player " + msg[1], position);
			playerAngle.put("Player " + msg[1], angle);
			playerVelocity.put("Player " + msg[1], new float[] { velocityX, velocityY });
			// Get and change position of opponent
			playerList.get(Integer.parseInt(msg[1])).setPosition(position[0], position[1], angle, velocityX,
					velocityY);

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
			float velocityX = Float.parseFloat(msg[2]);
			float velocityY = Float.parseFloat(msg[3]);
			playerVelocity.put("Player " + Integer.parseInt(msg[1]), new float[] { velocityX, velocityY });
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
				System.out.println("Client: Consume item");
				Vector2 position = new Vector2(Float.parseFloat(msg[3]), Float.parseFloat(msg[4]));
				itemConsumeQueue.offer(position);

			} else if (msg[2].equals("pro")) {
				System.out.println("Client: Produce item");
				itemSpawnQueue.add(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) });
			}
		} else if (msg[0].equals("weapon")) {
			if (msg[2].equals("con")) {
				System.out.println("Client: Consume weapon");
				Vector2 position = new Vector2(Float.parseFloat(msg[3]), Float.parseFloat(msg[4]));
				weaponConsumeQueue.offer(position);
			} else if (msg[2].equals("pro")) {
				System.out.println("Client: Produce weapon");
				weaponSpawnQueue.add(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) });
			}
		} else if (msg[0].equals("weaponpart")) {
			if (msg[2].equals("con")) {
				System.out.println("Client: Consume WP");
				Vector2 position = new Vector2(Float.parseFloat(msg[3]), Float.parseFloat(msg[4]));
				weaponPartConsumeQueue.offer(position);
			} else if (msg[2].equals("pro")) {
				System.out.println("Client: Produce weaponpart");
				weaponPartSpawnQueue.add(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) });
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
					trapSpawnQueue.add(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) });
				}
			}
		}

		else if (msg[0].equals("obstacle")) {
			System.out.println("Remove obstacle @ x:" + msg[1] + " y: " + msg[2]);
			Vector2 location = new Vector2(Float.parseFloat(msg[1]), Float.parseFloat(msg[2]));
			obstacleConsumeQueue.offer(location);

			if (gWorld.getObstacleList().isEmpty())
				AssetLoader.obstacleSoundmd.play();
			else
				AssetLoader.obstacleSFX();
		}

		else if (msg[0].equals("lightning")) {
			lightningQueue.offer(true);
		}

		else if (msg[0].equals("win")) {
			if (msg[1].equals("civilian")) {
				gWorld.setCivWin(true);
			} else if (msg[1].equals("murderer")) {
				gWorld.setMurWin(true);
			}
		}
	}

	public void endSession() throws IOException {
		if (!clientListenerThread.isAlive() && clientListenerThread != null)
			this.clientListenerThread.interrupt();
		if (!clientSocket.isClosed() && clientSocket != null)
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
					client.handleMessage(msg);
				} else {
					System.out.println("Client listener " + client.getId()
							+ " received null message from server. Terminating now.");
					break;
				}
			} catch (SocketTimeoutException e) {
				System.out.println("Client listener " + client.getId() + " timeout. check for server status");
				client.getClientOutput().println("connection_" + client.getId() + "_check");
				client.getClientOutput().flush();
				try {
					msg = input.readLine();
					System.out.println("Client listener " + client.getId() + " received message: " + msg);
				} catch (IOException e1) {
					System.out.println("IO exception on client listener " + client.getId() + " e1");
					e1.printStackTrace();
					break;
				} catch (NullPointerException e1) {
					System.out.println("Client listener " + client.getId()
							+ " received null in message. Terminating now.");
					break;
				}
				client.handleMessage(msg);
			} catch (SocketException E) {
				System.out.println("Client error: Socket error: " + E.getMessage());
				E.printStackTrace();
				System.out.println("Client notifying server to close.");
				client.getClientOutput().println("connection_" + client.getId() + "_close");
				client.getClientOutput().flush();
				break;

			} catch (Exception e) {
				System.out.println("Client error: While reading: " + e.getMessage());
				e.printStackTrace();
				System.out.println("Other exception thrown on client listener. continuing.");
				continue;
			}
			if (client.getgWorld().isCivWin() || client.getgWorld().isMurWin()) {
				System.out.println("Client " + client.getId() + " terminating due to win condition.");
				break;
			}
		}

		System.out.println("Client listener " + client.getId() + " thread closed.");
		if (!(client.getgWorld().isCivWin() || client.getgWorld().isMurWin())) {
			client.getgWorld().setDisconnected(true);
			try {
				this.input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
