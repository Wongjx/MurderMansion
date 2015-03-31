package com.jkjk.GameWorld;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jkjk.GameObjects.WeaponPartSprite;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.GameCharacterFactory;
import com.jkjk.GameObjects.Items.ItemFactory;
import com.jkjk.GameObjects.Items.ItemSprite;
import com.jkjk.GameObjects.Weapons.WeaponFactory;
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
	private GameCharacterFactory gameCharFac;
	private GameCharacter player;

	private ItemFactory itemFac;
	private Array<ItemSprite> itemList;

	private WeaponFactory weaponFac;
	private Array<WeaponSprite> weaponList;

	private Array<WeaponPartSprite> weaponPartList;
	private int numOfWeaponPartsCollected;
	private boolean shotgunCreated;

	private World world;
	private MMContactListener cl;

	private Array<Body> itemsToRemove, weaponsToRemove, weaponPartsToRemove, trapToRemove;
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
	public GameWorld(float gameWidth, float gameHeight) {
		world = new World(new Vector2(0, 0), true);
		cl = new MMContactListener(this);
		world.setContactListener(cl);

		itemsToRemove = cl.getItemsToRemove();
		weaponsToRemove = cl.getWeaponsToRemove();
		weaponPartsToRemove = cl.getWeaponPartsToRemove();
		trapToRemove = cl.getTrapToRemove();

		gameCharFac = new GameCharacterFactory();

		itemFac = new ItemFactory();
		itemList = new Array<ItemSprite>();

		weaponFac = new WeaponFactory();
		weaponList = new Array<WeaponSprite>();

		numOfWeaponPartsCollected = 0;
		weaponPartList = new Array<WeaponPartSprite>();

		Box2DMapObjectParser parser = new Box2DMapObjectParser();
		parser.load(world, AssetLoader.tiledMap);
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
		checkItemSprite();
		checkWeaponSprite();
		checkWeaponPartSprite();
		checkTrap();

		if (numOfWeaponPartsCollected == 8 && !shotgunCreated) {
			createShotgun();
			shotgunCreated = true;
		}

	}

	/**
	 * Creates the player in the Box2D world. User data is set as "player" and spawned at defined location.
	 * 
	 * @param type
	 *            0 for murderer, 1 for civilian
	 */
	public void createPlayer(int type) {
		if (type == 0)
			player = gameCharFac.createCharacter("Murderer", 0, this, true);
		else
			player = gameCharFac.createCharacter("Civilian", 0, this, true);
		player.getBody().getFixtureList().get(0).setUserData("player");
		player.spawn(1010, 515, 0);
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
	private void checkItemSprite() {
		for (int i = 0; i < itemsToRemove.size; i++) {
			bodyToRemove = itemsToRemove.get(i);
			itemList.removeValue((ItemSprite) bodyToRemove.getUserData(), true);
			world.destroyBody(bodyToRemove);
			if (player.getType().equals("Civilian"))
				player.addItem(itemFac.createItem("Disarm Trap", this));
			else if (player.getType().equals("Murderer"))
				player.addItem(itemFac.createItem("Trap", this));
		}
		itemsToRemove.clear();
	}

	/**
	 * Checks to remove weapon sprites that have been contacted by the player.
	 */
	private void checkWeaponSprite() {
		for (int i = 0; i < weaponsToRemove.size; i++) {
			bodyToRemove = weaponsToRemove.get(i);
			weaponList.removeValue((WeaponSprite) bodyToRemove.getUserData(), true);
			world.destroyBody(bodyToRemove);
			if (player.getType().equals("Civilian"))
				player.addWeapon(weaponFac.createWeapon("Bat", this));
			else if (player.getType().equals("Murderer"))
				player.addWeapon(weaponFac.createWeapon("Knife", this));
		}
		weaponsToRemove.clear();
	}

	/**
	 * Checks to remove weapon part sprites that have been contacted by the player.
	 */
	private void checkWeaponPartSprite() {
		for (int i = 0; i < weaponPartsToRemove.size; i++) {
			bodyToRemove = weaponPartsToRemove.get(i);
			weaponPartList.removeValue((WeaponPartSprite) bodyToRemove.getUserData(), true);
			world.destroyBody(bodyToRemove);
			if (player.getType().equals("Civilian")) {
				numOfWeaponPartsCollected++;
			}
		}
		weaponPartsToRemove.clear();
	}

	/**
	 * Checks to remove traps sprites that have been contacted by the player.
	 */
	private void checkTrap() {
		for (int i = 0; i < trapToRemove.size; i++) {
			bodyToRemove = trapToRemove.get(i);
			weaponList.removeValue((WeaponSprite) bodyToRemove.getUserData(), true);
			world.destroyBody(bodyToRemove);
		}
		trapToRemove.clear();
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
			}
		}
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
	public Array<ItemSprite> getItemList() {
		return itemList;
	}

	/**
	 * @return List of weapons on the map.
	 */
	public Array<WeaponSprite> getWeaponList() {
		return weaponList;
	}

	/**
	 * @return List of weapon parts on the map.
	 */
	public Array<WeaponPartSprite> getWeaponPartList() {
		return weaponPartList;
	}

}
