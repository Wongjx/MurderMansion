package com.jkjk.GameWorld;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;
import box2dLight.RayHandler;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Obstacles;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.GameCharacterFactory;
import com.jkjk.GameObjects.Items.ItemFactory;
import com.jkjk.GameObjects.Items.ItemSprite;
import com.jkjk.GameObjects.Items.Trap;
import com.jkjk.GameObjects.Weapons.WeaponFactory;
import com.jkjk.GameObjects.Weapons.WeaponPartSprite;
import com.jkjk.GameObjects.Weapons.WeaponSprite;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MMHelpers.MMContactListener;
import com.jkjk.MMHelpers.ToastMessage;

/**
 * GameWorld's primary purpose is to update the results of interactions in the world. It deals with creation
 * and destruction of Box2D bodies, and manages contact listeners.
 * 
 * @author LeeJunXiang
 * 
 */
public class GameWorld {
	private GameCharacterFactory gameCharFac;
	private GameCharacter player;
	private RayHandler rayHandler;

	private ItemFactory itemFac;
	private ConcurrentHashMap<Vector2, ItemSprite> itemList;
	private ConcurrentHashMap<Vector2, Trap> trapList;
	private Vector2 trapLocation;

	private WeaponFactory weaponFac;
	private ConcurrentHashMap<Vector2, WeaponSprite> weaponList;

	private ConcurrentHashMap<Vector2, WeaponPartSprite> weaponPartList;
	private int numOfWeaponPartsCollected;

	private boolean inSafeArea;
	private boolean civWin;
	private boolean murWin;
	private boolean disconnected;
	private Duration gameOverTimer;

	private ConcurrentHashMap<Vector2, Obstacles> obstacleList;

	private World world;
	private MMContactListener cl;

	private ConcurrentLinkedQueue<Body> itemsToRemove, weaponsToRemove, weaponPartsToRemove, trapToRemove;
	private ConcurrentLinkedQueue<Vector2> itemsToAdd, weaponsToAdd;
	private Body bodyToRemove;
	private Vector2 bodyToAdd;
	private Trap trapToCreate;

	private float currentPositionX;
	private float currentPositionY;
	private float currentAngle;
	private float ambientLightValue;

	private ToastMessage TM;
	private boolean tutorial;
	private GameCharacter dummy;
	
	private boolean prevStun;

	/**
	 * Constructs the Box2D world, adding Box2D objects such as players, items and weapons. Attaches the
	 * contact listener in the world to observe any contact between these objects.
	 * 
	 * @param gameWidth
	 *            Accesses the virtual game width.
	 * @param gameHeight
	 *            Accesses the virtual game height.
	 */
	public GameWorld(boolean tutorial) {
		this.tutorial = tutorial;
		world = new World(new Vector2(0, 0), true);
		// cl = MMContactListener.getInstance(this);
		cl = new MMContactListener(this);
		world.setContactListener(cl);

		itemsToRemove = cl.getItemsToRemove();
		weaponsToRemove = cl.getWeaponsToRemove();
		weaponPartsToRemove = cl.getWeaponPartsToRemove();
		trapToRemove = cl.getTrapToRemove();
		itemsToAdd = new ConcurrentLinkedQueue<Vector2>();
		weaponsToAdd = new ConcurrentLinkedQueue<Vector2>();

		gameCharFac = new GameCharacterFactory();
		rayHandler = new RayHandler(world);

		itemFac = new ItemFactory();
		itemList = new ConcurrentHashMap<Vector2, ItemSprite>();
		trapList = new ConcurrentHashMap<Vector2, Trap>();

		weaponFac = new WeaponFactory();
		weaponList = new ConcurrentHashMap<Vector2, WeaponSprite>();

		weaponPartList = new ConcurrentHashMap<Vector2, WeaponPartSprite>();

		obstacleList = new ConcurrentHashMap<Vector2, Obstacles>();

		gameOverTimer = new Duration(3000);

		Box2DMapObjectParser parser = new Box2DMapObjectParser();
		parser.load(world, AssetLoader.tiledMap);

		if (tutorial) {
			TM = new ToastMessage(305, 15000);
		} else
			TM = new ToastMessage(305, 5000);
	}

	/**
	 * Updates the state of Box2D objects, such as the consequence of a player picking up an item, or when a
	 * player dies.
	 * 
	 * @param delta
	 *            The time between each render.
	 */
	public void update(float delta, MMClient client) {
		world.step(delta, 6, 2); // Step size|Steps for each body to check collision|Accuracy of body position
									// after collision
		client.update();

		if (player.isAlive()) {
			player.update();
		} else {
			client.updatePlayerIsAlive(client.getId(), 0);
			TM.setDisplayMessage("You Have Died... Your Spirit Seeks Vengeance.");
			createGhost(client);
		}
		checkStairs();
		checkItemSprite(client);
		checkWeaponSprite(client);
		checkWeaponPartSprite(client);
		checkTrap(client);
		checkStun(client);
	}

	public void createTutorialWeapon() {
		WeaponSprite ws = new WeaponSprite(this);
		Vector2 location2 = new Vector2(player.getBody().getPosition().x - 60,
				player.getBody().getPosition().y);
		getWeaponList().put(location2, ws);
		ws.spawn(location2.x, location2.y, 0);
		player.getBody().setTransform(player.getBody().getPosition(), 3.1427f);
	}

	public void createTutorialItem() {
		ItemSprite is = new ItemSprite(this);
		Vector2 location = new Vector2(player.getBody().getPosition().x - 40,
				player.getBody().getPosition().y);
		getItemList().put(location, is);
		is.spawn(location.x, location.y, 0);
		player.getBody().setTransform(player.getBody().getPosition(), 3.1427f);
	}

	public void createTutorialWP() {
		for (int i = 0; i < 2; i++) {
			WeaponPartSprite wps = new WeaponPartSprite(this);
			Vector2 location = new Vector2(player.getBody().getPosition().x - 40 - (30 * i), player.getBody()
					.getPosition().y);
			getWeaponPartList().put(location, wps);
			wps.spawn(location.x, location.y, 0);
			player.getBody().setTransform(player.getBody().getPosition(), 3.1427f);
		}
	}

	public void createTutorialTrap() {
		Vector2 location = new Vector2(player.getBody().getPosition().x - 40,
				player.getBody().getPosition().y);
		createTrap(location);
		player.getBody().setTransform(player.getBody().getPosition(), 3.1427f);
	}

	/**
	 * Creates the player in the Box2D world. User data is set as "player" and spawned at defined location.
	 * 
	 * @param type
	 *            0 for murderer, 1 for civilian
	 */
	public GameCharacter createPlayer(int type, float x, float y, float angle, int id) {
		if (type == 0) {
			player = gameCharFac.createCharacter("Murderer", id, this, true);
			createDoor();
		} else if (type == 2)
			player = gameCharFac.createCharacter("Ghost", id, this, true);
		else {
			player = gameCharFac.createCharacter("Civilian", id, this, true);
		}
		player.getBody().getFixtureList().get(0).setUserData("player");
		player.spawn(x, y, angle);
		return player;
	}

	/**
	 * To block murderer from exiting into safe area when mansion door opens
	 */
	public void createDoor() {
		if (player.getType() == "Murderer") {
			new Obstacles(this, new Vector2(915.2f, 511.8f), 0);

		}
	}

	/**
	 * Creates a ghost by destroying the player's previous body. Sets the user data to "player", and spawns
	 * him at the position and angle of his death.
	 */
	private void createGhost(MMClient client) {
		currentPositionX = player.getBody().getPosition().x;
		currentPositionY = player.getBody().getPosition().y;
		currentAngle = player.getBody().getAngle();
		ambientLightValue = player.getAmbientLightValue();

		player.getBody().setActive(false);
		player.getBody().setTransform(0, 0, 0);
		player = gameCharFac.createCharacter("Ghost", player.getId(), this, true);
		client.updatePlayerType(client.getId(), 2);
		player.set_deathPositionX(currentPositionX);
		player.set_deathPositionY(currentPositionY);
		player.getBody().getFixtureList().get(0).setUserData("player");
		player.spawn(currentPositionX, currentPositionY, currentAngle);
		player.setAmbientLightValue(ambientLightValue);

	}

	/**
	 * Checks to remove item sprites that have been contacted by the player.
	 */
	private void checkItemSprite(MMClient client) {
		bodyToRemove = itemsToRemove.poll();
		// Call MMclient to remove item
		if (bodyToRemove != null) {
			client.removeItemLocation(bodyToRemove.getPosition());
			System.out.println("Item removed from client.");
			itemList.remove(bodyToRemove.getPosition());
			bodyToRemove.setActive(false);
			bodyToRemove.setTransform(0, 0, 0);
			if (player.getType().equals("Murderer"))
				player.addItem(itemFac.createItem("Trap", this, client, player));
			else
				player.addItem(itemFac.createItem("Disarm Trap", this, client, player));
		}
		bodyToAdd = itemsToAdd.poll();
		if (bodyToAdd != null) {
			client.produceItemLocation(bodyToAdd);
		}
	}

	/**
	 * Checks to remove weapon sprites that have been contacted by the player.
	 */
	private void checkWeaponSprite(MMClient client) {
		bodyToRemove = weaponsToRemove.poll();
		if (bodyToRemove != null) {
			// Call MMclient to remove weapon
			client.removeWeaponLocation(bodyToRemove.getPosition());
			weaponList.remove(bodyToRemove.getPosition());
			bodyToRemove.setActive(false);
			bodyToRemove.setTransform(0, 0, 0);
			if (player.getType().equals("Murderer"))
				player.addWeapon(weaponFac.createWeapon("Knife", this, player));
			else
				player.addWeapon(weaponFac.createWeapon("Bat", this, player));
		}
		bodyToAdd = weaponsToAdd.poll();
		if (bodyToAdd != null) {
			client.produceWeaponLocation(bodyToAdd);
		}
	}

	/**
	 * Checks to remove weapon part sprites that have been contacted by the player.
	 */
	private void checkWeaponPartSprite(MMClient client) {
		bodyToRemove = weaponPartsToRemove.poll();

		if (bodyToRemove != null) {
			// Call MMclient to remove weapon part
			client.removeWeaponPartLocation(bodyToRemove.getPosition());
			weaponPartList.remove(bodyToRemove.getPosition());
			bodyToRemove.setActive(false);
			bodyToRemove.setTransform(0, 0, 0);
			if (player.getType().equals("Civilian")) {
				client.addWeaponPartCollected();
			}
		}
	}

	/**
	 * Checks to remove traps sprites that have been contacted by the player. Also checks to add trap added by
	 */
	private void checkTrap(MMClient client) {
		bodyToRemove = trapToRemove.poll();
		if (bodyToRemove != null) {
			trapList.remove(bodyToRemove.getPosition());
			client.removeTrapLocation(bodyToRemove.getPosition().x, bodyToRemove.getPosition().y);
			bodyToRemove.setActive(false);
			bodyToRemove.setTransform(0, 0, 0);
		}
	}

	private void checkStun(MMClient client) {
		if (player.isStun() && prevStun == false) {
			prevStun = true;
			client.updatePlayerIsStun(client.getId(), 1);
			TM.setDisplayMessage("You have been Stunned");
		}
		if (!player.isStun()){
			prevStun = false;
		}
	}

	/**
	 * Teleports users between level 1 and 2 of the map when player comes in contact with the hitbox at stairs
	 */
	private void checkStairs() {
		if (cl.getAtStairs()) {
			cl.notAtStairs();
			if (cl.getStairsName().equals("L1S1")) {
				TM.setDisplayMessage("Second Level");
				player.getBody().setTransform(2610, 870, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L1S2")) {
				TM.setDisplayMessage("Second Level");
				player.getBody().setTransform(2305, 870, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L1S3")) {
				TM.setDisplayMessage("Second Level");
				player.getBody().setTransform(2285, 269, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L1S4")) {
				TM.setDisplayMessage("Second Level");
				player.getBody().setTransform(2835, 155, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L2S1")) {
				TM.setDisplayMessage("First Level");
				player.getBody().setTransform(645, 870, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L2S2")) {
				TM.setDisplayMessage("First Level");
				player.getBody().setTransform(445, 870, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L2S3")) {
				TM.setDisplayMessage("First Level");
				player.getBody().setTransform(445, 269, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L2S4")) {
				TM.setDisplayMessage("First Level");
				player.getBody().setTransform(910, 180, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L1S5")) {
				TM.setDisplayMessage("Basement");
				player.getBody().setTransform(3515, 640, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("LbS1")) {
				TM.setDisplayMessage("First Level");
				player.getBody().setTransform(310, 250, player.getBody().getAngle());
			}
		}
	}

	/**
	 * Removes an obstacle from the obstacleList and destroys body from world.
	 * 
	 * 
	 * @param location
	 *            Vector2 coordinates of the obstacle
	 */
	public void removeObstacle(Vector2 location) {
		obstacleList.get(location).getBody().setActive(false);
		obstacleList.get(location).getBody().setTransform(0, 0, 0);
		obstacleList.remove(location);
		if (obstacleList.size() == 0) {
			if (player.getType() == "Civilian")
				TM.setDisplayMessage("The mansion door to the East creaks open... Run! Now!");
			else if (player.getType() == "Murderer")
				TM.setDisplayMessage("The mansion door to the East creaks open... Stop them! Now!");
			else if (player.getType() == "Ghost")
				TM.setDisplayMessage("Your spirit is forever trapped in JK's playhouse...");
		} else {
			TM.setDisplayMessage("An Obstacle Mysteriously Disappears...");
		}
	}

	public void createTrap(Vector2 location) {
		trapToCreate = (Trap) itemFac.createItem("Trap", this, null, null);
		trapList.put(location, trapToCreate);
		trapToCreate.spawn(location.x, location.y, 0);
	}

	/**
	 * @return Box2D World
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * @return Obtain the player's instance.
	 */
	public GameCharacter getPlayer() {
		return player;
	}

	/**
	 * @return GameCharacter Factory to create game characters.
	 */
	public GameCharacterFactory getGameCharFac() {
		return gameCharFac;
	}

	/**
	 * @return List of items on the map.
	 */
	public ConcurrentHashMap<Vector2, ItemSprite> getItemList() {
		return itemList;
	}

	/**
	 * @return List of weapons on the map.
	 */
	public ConcurrentHashMap<Vector2, WeaponSprite> getWeaponList() {
		return weaponList;
	}

	/**
	 * @return List of weapon parts on the map.
	 */
	public ConcurrentHashMap<Vector2, WeaponPartSprite> getWeaponPartList() {
		return weaponPartList;
	}

	public ConcurrentHashMap<Vector2, Trap> getTrapList() {
		return trapList;
	}

	public ConcurrentHashMap<Vector2, Obstacles> getObstacleList() {
		return obstacleList;
	}

	public boolean isInSafeArea() {
		return inSafeArea;
	}

	public void setInSafeArea(boolean inSafeArea) {
		if (inSafeArea)
			TM.setDisplayMessage("You're now safe from the murderer");
		else
			TM.setDisplayMessage("Are you crazy? Don't go back in!");
		this.inSafeArea = inSafeArea;
	}

	public ConcurrentLinkedQueue<Vector2> getItemsToAdd() {
		return itemsToAdd;
	}

	public ConcurrentLinkedQueue<Vector2> getWeaponsToAdd() {
		return weaponsToAdd;
	}

	public RayHandler getRayHandler() {
		return rayHandler;
	}

	public boolean isCivWin() {
		return civWin;
	}

	public void setCivWin(boolean civWin) {
		this.civWin = civWin;
		if (civWin) {
			TM.setDisplayMessage("The civilians have prevailed!");
			gameOverTimer.startCountdown();
		}
	}

	public boolean isMurWin() {
		return murWin;
	}

	public void setMurWin(boolean murWin) {
		this.murWin = murWin;
		if (murWin) {
			TM.setDisplayMessage("The murderer's scheme is complete...");
			gameOverTimer.startCountdown();
		}
	}

	public boolean isDisconnected() {
		return disconnected;
	}

	public void setDisconnected(boolean disconnected) {
		this.disconnected = disconnected;
		if (disconnected) {
			TM.setDisplayMessage("Your connection to the real world has been lost...");
			gameOverTimer.startCountdown();
		}
	}

	public Duration getGameOverTimer() {
		return gameOverTimer;
	}

	public void setTrapToRemove(Body value) {
		this.trapToRemove.offer(value);
	}

	public int getNumOfWeaponPartsCollected() {
		return numOfWeaponPartsCollected;
	}

	public void addNumOfWeaponPartsCollected() {
		this.numOfWeaponPartsCollected++;
	}

	public ToastMessage getTM() {
		return TM;
	}

	public boolean isTutorial() {
		return tutorial;
	}

	public void setDummy(GameCharacter dummy) {
		this.dummy = dummy;
	}

	public GameCharacter getDummy() {
		return dummy;
	}

	public void dispose() {
		world.dispose();
		cl = null;

		// world = new World(new Vector2(0, 0), true);
		// cl = MMContactListener.getInstance(this);
		// world.setContactListener(cl);

		itemsToRemove = null;
		weaponsToRemove = null;
		weaponPartsToRemove = null;
		trapToRemove = null;
		itemsToAdd = null;
		weaponsToAdd = null;

		gameCharFac = null;
		rayHandler = null;

		itemFac = null;
		itemList = null;
		trapList = null;

		weaponFac = null;
		weaponList = null;

		weaponPartList = null;

		obstacleList = null;

		gameOverTimer = null;

		Box2DMapObjectParser parser = null;
	}

}
