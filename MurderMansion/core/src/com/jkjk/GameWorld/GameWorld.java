package com.jkjk.GameWorld;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jkjk.GameObjects.Abilities.AbilityFactory;
import com.jkjk.GameObjects.Characters.Civilian;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.GameCharacterFactory;
import com.jkjk.GameObjects.Characters.Murderer;
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
 */
/**
 * @author LeeJunXiang
 * 
 */
public class GameWorld {
	private GameCharacterFactory gameCharFac;
	private GameCharacter player;
	private Array<GameCharacter> playerList;

	private ItemFactory itemFac;
	private Array<ItemSprite> itemList;

	private WeaponFactory weaponFac;
	private Array<WeaponSprite> weaponList;

	private AbilityFactory abilityFac;

	private World world;
	private MMContactListener cl;
	private int numOfPlayers;
	private int maxItems;
	private int maxWeapons;

	private Array<Body> itemsToRemove, weaponsToRemove, trapToRemove;
	private Body bodyToRemove;

	private float currentPositionX;
	private float currentPositionY;
	private float currentAngle;

	// FOR DEBUG PURPOSE
	private BodyDef bdef;
	private Body body;
	private FixtureDef fdef;

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
		trapToRemove = cl.getTrapToRemove();

		abilityFac = new AbilityFactory();

		gameCharFac = new GameCharacterFactory();
		playerList = new Array<GameCharacter>();
		numOfPlayers = 4;

		itemFac = new ItemFactory();
		itemList = new Array<ItemSprite>();
		maxItems = numOfPlayers * 2;
		weaponFac = new WeaponFactory();
		weaponList = new Array<WeaponSprite>();
		maxWeapons = (int) (numOfPlayers * 1.2);
		createPlayer();
		for (int i = 0; i < numOfPlayers - 1; i++) {
			createOpponents(i);
		}
		for (int i = 0; i < maxItems; i++) {
			createItems(i);
		}
		for (int i = 0; i < maxWeapons; i++) {
			createWeapons(i);
		}

		createTrap(); // FOR DEBUG PURPOSE

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
	public void update(float delta) {
		world.step(delta, 6, 2); // Step size|Steps for each body to check collision|Accuracy of body position
									// after collision

		if (player.isAlive()) {
			player.update();
		} else {
			System.out.println("Creating ghost");
			createGhost();
		}
		checkStairs();
		checkItemSprite();
		checkWeaponSprite();
		checkWeaponPartSprite();
		checkTrap();

	}

	/**
	 * Creates the player in the Box2D world. User data is set as "player" and spawned at defined location.
	 */
	private void createPlayer() {
		player = gameCharFac.createCharacter("Murderer", 0, world);
//		player = gameCharFac.createCharacter("Civilian", 0, world);
		player.getBody().getFixtureList().get(0).setUserData("player");
		player.spawn(1010, 515, 0);
	}

	// FOR DEBUG PURPOSE
	private void createTrap() {
		bdef = new BodyDef();
		fdef = new FixtureDef();
		bdef.type = BodyType.StaticBody;
		bdef.position.set(1010, 570);
		body = world.createBody(bdef);

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
			playerList.add((Murderer) gameCharFac.createCharacter("Murderer", i + 1, world));
			playerList.get(i).getBody().setType(BodyType.KinematicBody);
			playerList.get(i).spawn(1010 - ((i + 1) * 40), 515, 0);
		} else {
			playerList.add((Civilian) gameCharFac.createCharacter("Civilian", i + 1, world));
			playerList.get(i).getBody().setType(BodyType.KinematicBody);
			playerList.get(i).spawn(1010 - ((i + 1) * 40), 515, 0);
		}
	}

	// FOR DEBUG PURPOSE
	private void createItems(int i) {
		itemList.add(new ItemSprite(world));
		itemList.get(i).spawn(1100 - ((i + 1) * 40), 490, 0);
	}

	// FOR DEBUG PURPOSE
	private void createWeapons(int i) {
		weaponList.add(new WeaponSprite(world));
		weaponList.get(i).spawn(1100 - ((i + 1) * 40), 460, 0);
	}

	/**
	 * @return Box2D World
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * @return Number of players playing the game.
	 */
	public int getNumOfPlayers() {
		return numOfPlayers;
	}

	/**
	 * @param i
	 *            Number of players that will be playing the game.
	 */
	public void setNumOfPlayers(int i) {
		numOfPlayers = i;
	}

	/**
	 * @return Obtain list of players.
	 */
	public Array<GameCharacter> getPlayerList() {
		return playerList;
	}

	/**
	 * @return Obtain the player's instance.
	 */
	public GameCharacter getPlayer() {
		return player;
	}

	/**
	 * Creates a ghost by destroying the player's previous body. Sets the user data to "player", and spawns
	 * him at the position and angle of his death.
	 */
	private void createGhost() {
		currentPositionX = player.getBody().getPosition().x;
		currentPositionY = player.getBody().getPosition().y;
		currentAngle = player.getBody().getAngle();

		world.destroyBody(player.getBody());

		player = gameCharFac.createCharacter("Ghost", player.getId(), world);
		player.getBody().getFixtureList().get(0).setUserData("player");
		player.spawn(currentPositionX, currentPositionY, currentAngle);
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
}
