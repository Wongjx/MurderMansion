package com.jkjk.GameWorld;

import java.util.HashMap;
import java.util.Iterator;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;
import box2dLight.RayHandler;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
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

/**
 * GameWorld's primary purpose is to update the results of interactions in the world. It deals with creation
 * and destruction of Box2D bodies, and manages contact listeners.
 * 
 * @author LeeJunXiang
 * 
 */
public class GameWorld {
	private static GameWorld instance;

	private GameCharacterFactory gameCharFac;
	private GameCharacter player;
	private RayHandler rayHandler;

	private ItemFactory itemFac;
	private HashMap<Vector2, ItemSprite> itemList;
	private HashMap<Vector2, Trap> trapList;
	private Vector2 trapLocation;

	private WeaponFactory weaponFac;
	private HashMap<Vector2, WeaponSprite> weaponList;

	private HashMap<Vector2, WeaponPartSprite> weaponPartList;
	private int numOfWeaponPartsCollected;
	private boolean shotgunCreated;
	private boolean inSafeArea;
	private boolean civWin;
	private boolean murWin;
	private Duration gameOverTimer;

	private HashMap<Vector2, Obstacles> obstacleList;

	private World world;
	private MMContactListener cl;

	private Array<Body> itemsToRemove, weaponsToRemove, weaponPartsToRemove, trapToRemove;
	private Iterator<Body> itemsRemoveIterator, weaponsRemoveIterator, weaponPartsRemoveIterator,
			trapRemoveIterator;
	private Array<Vector2> itemsToAdd, weaponsToAdd;
	private Iterator<Vector2> itemsAddIterator, weaponsAddIterator;
	private Body bodyToRemove;

	private float currentPositionX;
	private float currentPositionY;
	private float currentAngle;
	private float ambientLightValue;

	/**
	 * Constructs the Box2D world, adding Box2D objects such as players, items and weapons. Attaches the
	 * contact listener in the world to observe any contact between these objects.
	 * 
	 * @param gameWidth
	 *            Accesses the virtual game width.
	 * @param gameHeight
	 *            Accesses the virtual game height.
	 */
	private GameWorld() {
		world = new World(new Vector2(0, 0), true);
		cl = MMContactListener.getInstance(this);
		world.setContactListener(cl);

		itemsToRemove = cl.getItemsToRemove();
		weaponsToRemove = cl.getWeaponsToRemove();
		weaponPartsToRemove = cl.getWeaponPartsToRemove();
		trapToRemove = cl.getTrapToRemove();
		itemsToAdd = new Array<Vector2>();
		weaponsToAdd = new Array<Vector2>();

		itemsRemoveIterator = itemsToRemove.iterator();
		weaponsRemoveIterator = weaponsToRemove.iterator();
		weaponPartsRemoveIterator = weaponPartsToRemove.iterator();
		trapRemoveIterator = trapToRemove.iterator();
		itemsAddIterator = itemsToAdd.iterator();
		weaponsAddIterator = weaponsToAdd.iterator();
		
		
		gameCharFac = new GameCharacterFactory();
		rayHandler = new RayHandler(world);

		itemFac = new ItemFactory();
		itemList = new HashMap<Vector2, ItemSprite>();
		trapList = new HashMap<Vector2, Trap>();

		weaponFac = new WeaponFactory();
		weaponList = new HashMap<Vector2, WeaponSprite>();

		numOfWeaponPartsCollected = 0;
		weaponPartList = new HashMap<Vector2, WeaponPartSprite>();

		obstacleList = new HashMap<Vector2, Obstacles>();

		gameOverTimer = new Duration(5000);

		Box2DMapObjectParser parser = new Box2DMapObjectParser();
		parser.load(world, AssetLoader.tiledMap);

	}

	public static GameWorld getInstance() {
		if (instance == null) {
			instance = new GameWorld();
		}
		return instance;
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
			createGhost();
		}
		checkStairs();
		checkItemSprite(client);
		checkWeaponSprite(client);
		checkWeaponPartSprite(client);
		checkTrap(client);

		if (numOfWeaponPartsCollected == 8 && !shotgunCreated) {
			createShotgun();
			shotgunCreated = true;
		}
<<<<<<< HEAD
		
		if (gameOver){
			gameOverTimer.update();
			if (!gameOverTimer.isCountingDown()){
//				System.out.println("GAMEWORLD UPDATE: GAMEOVER COMPLETE");
			}
		}
		
		System.out.println("umm, num weapon parts: " + numOfWeaponPartsCollected);
=======
>>>>>>> c0e96b4aa1d24268d5c02d95b179f4d0d6ad0dfa

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
		else
			player = gameCharFac.createCharacter("Civilian", id, this, true);
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
	 * If the player is a civilian, his weapon will be replaced with a shotgun
	 */
	private void createShotgun() {
		if (player.getType().equals("Civilian")) {
			player.addWeapon(weaponFac.createWeapon("Shotgun", this));
		}
	}

	/**
	 * Creates a ghost by destroying the player's previous body. Sets the user data to "player", and spawns
	 * him at the position and angle of his death.
	 */
	private void createGhost() {
		currentPositionX = player.getBody().getPosition().x;
		currentPositionY = player.getBody().getPosition().y;
		currentAngle = player.getBody().getAngle();
		ambientLightValue = player.getAmbientLightValue();

		world.destroyBody(player.getBody());

		player = gameCharFac.createCharacter("Ghost", player.getId(), this, true);
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

		while (itemsRemoveIterator.hasNext()) {
			bodyToRemove = itemsRemoveIterator.next();
			itemsRemoveIterator.remove();
			// Call MMclient to remove item
			client.removeItemLocation(bodyToRemove.getPosition());
			System.out.println("Item removed from client.");
			itemList.remove(bodyToRemove.getPosition());
			world.destroyBody(bodyToRemove);
			if (player.getType().equals("Murderer"))
				player.addItem(itemFac.createItem("Trap", this, client));
			else
				player.addItem(itemFac.createItem("Disarm Trap", this, client));
		}

		while (itemsAddIterator.hasNext()) {
			client.produceItemLocation(itemsAddIterator.next());
			itemsAddIterator.remove();
		}
	}

	/**
	 * Checks to remove weapon sprites that have been contacted by the player.
	 */
	private void checkWeaponSprite(MMClient client) {
		while (weaponsRemoveIterator.hasNext()) {
			bodyToRemove = weaponsRemoveIterator.next();
			weaponsRemoveIterator.remove();
			// Call MMclient to remove weapon
			client.removeWeaponLocation(bodyToRemove.getPosition());
			weaponList.remove(bodyToRemove.getPosition());
			world.destroyBody(bodyToRemove);
			if (player.getType().equals("Murderer"))
				player.addWeapon(weaponFac.createWeapon("Knife", this));
			else
				player.addWeapon(weaponFac.createWeapon("Bat", this));
		}
		while (weaponsAddIterator.hasNext()) {
			client.produceWeaponLocation(weaponsAddIterator.next());
			weaponsAddIterator.remove();
		}
	}

	/**
	 * Checks to remove weapon part sprites that have been contacted by the player.
	 */
	private void checkWeaponPartSprite(MMClient client) {
		while (weaponPartsRemoveIterator.hasNext()) {
			bodyToRemove = weaponPartsRemoveIterator.next();
			weaponPartsRemoveIterator.remove();
			// Call MMclient to remove weapon part
			client.removeWeaponPartLocation(bodyToRemove.getPosition());
			weaponPartList.remove(bodyToRemove.getPosition());
			world.destroyBody(bodyToRemove);
			if (player.getType().equals("Civilian")) {
				numOfWeaponPartsCollected++;
			}
		}
	}

	/**
	 * Checks to remove traps sprites that have been contacted by the player. Also checks to add trap added by
	 * other players
	 */
	private void checkTrap(MMClient client) {
		while (trapRemoveIterator.hasNext()) {
			bodyToRemove = trapRemoveIterator.next();
			trapRemoveIterator.remove();
			trapList.remove(bodyToRemove.getPosition());
			world.destroyBody(bodyToRemove);
		}

		while (client.getTrapList().keySet().iterator().hasNext()) {
			trapLocation = client.getTrapList().keySet().iterator().next();
			client.getTrapList().remove(trapLocation).spawn(trapLocation.x, trapLocation.y, 0);
		}
	}

	/**
	 * Teleports users between level 1 and 2 of the map when player comes in contact with the hitbox at stairs
	 */
	private void checkStairs() {
		if (cl.getAtStairs()) {
			cl.notAtStairs();
			if (cl.getStairsName().equals("L1S1")) {
				player.getBody().setTransform(2610, 870, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L1S2")) {
				player.getBody().setTransform(2305, 870, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L1S3")) {
				player.getBody().setTransform(2285, 269, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L1S4")) {
				player.getBody().setTransform(2835, 155, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L2S1")) {
				player.getBody().setTransform(645, 870, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L2S2")) {
				player.getBody().setTransform(445, 870, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L2S3")) {
				player.getBody().setTransform(445, 269, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L2S4")) {
				player.getBody().setTransform(910, 180, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("L1S5")) {
				player.getBody().setTransform(3515, 640, player.getBody().getAngle());
			} else if (cl.getStairsName().equals("LbS1")) {
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
		world.destroyBody(obstacleList.get(location).getBody());
		obstacleList.remove(location);
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
	 * @return Number of Weapon Parts Collected
	 */
	public int getNumOfWeaponPartsCollected() {
		return numOfWeaponPartsCollected;
	}

	/**
	 * Adds 1 to number of weapon parts collected.
	 */
	public void weaponPartsCollected() {
		this.numOfWeaponPartsCollected++;
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
	public HashMap<Vector2, ItemSprite> getItemList() {
		return itemList;
	}

	/**
	 * @return List of weapons on the map.
	 */
	public HashMap<Vector2, WeaponSprite> getWeaponList() {
		return weaponList;
	}

	/**
	 * @return List of weapon parts on the map.
	 */
	public HashMap<Vector2, WeaponPartSprite> getWeaponPartList() {
		return weaponPartList;
	}

	public HashMap<Vector2, Trap> getTrapList() {
		return trapList;
	}

	public HashMap<Vector2, Obstacles> getObstacleList() {
		return obstacleList;
	}

	public boolean isInSafeArea() {
		return inSafeArea;
	}

	public void setInSafeArea(boolean inSafeArea) {
		this.inSafeArea = inSafeArea;
	}

	public Array<Vector2> getItemsToAdd() {
		return itemsToAdd;
	}

	public Array<Vector2> getWeaponsToAdd() {
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
			gameOverTimer.startCountdown();
		}
	}

	public boolean isMurWin() {
		return murWin;
	}

	public void setMurWin(boolean murWin) {
		this.murWin = murWin;
		if (murWin) {
			gameOverTimer.startCountdown();
		}
	}

	public Duration getGameOverTimer() {
		return gameOverTimer;
	}

}
