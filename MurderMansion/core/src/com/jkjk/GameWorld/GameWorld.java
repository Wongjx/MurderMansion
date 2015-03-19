package com.jkjk.GameWorld;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
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

public class GameWorld {
	private GameCharacterFactory gameCharFac;
	private GameCharacter player;
	private Array<GameCharacter> playerList;

	private ItemFactory itemFac;
	private Array<ItemSprite> itemList;

	private WeaponFactory weaponFac;
	private Array<WeaponSprite> weaponList;

	private World world;
	private MMContactListener cl;
	private int numOfPlayers;
	private int maxItems;
	private int maxWeapons;

	private Array<Body> itemsToRemove, weaponsToRemove;
	private Body bodyToRemove;

	public GameWorld(float gameWidth, float gameHeight) {
		world = new World(new Vector2(0, 0), true);
		cl = new MMContactListener(this);
		world.setContactListener(cl);

		itemsToRemove = cl.getItemsToRemove();
		weaponsToRemove = cl.getWeaponsToRemove();

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

		Box2DMapObjectParser parser = new Box2DMapObjectParser();
		parser.load(world, AssetLoader.tiledMap);
	}

	private void createPlayer() {
		player = gameCharFac.createCharacter("Murderer", world);
		player.getBody().getFixtureList().get(0).setUserData("player");
		player.spawn(1010, 515, 0);
	}

	private void createOpponents(int i) {
		if (i == 0) {
			playerList.add((Murderer) gameCharFac.createCharacter("Murderer", world));
			playerList.get(i).getBody().setType(BodyType.KinematicBody);
			playerList.get(i).spawn(1010 - ((i + 1) * 40), 515, 0);
		} else {
			playerList.add((Civilian) gameCharFac.createCharacter("Civilian", i, world));
			playerList.get(i).getBody().setType(BodyType.KinematicBody);
			playerList.get(i).spawn(1010 - ((i + 1) * 40), 515, 0);
		}
	}

	private void createItems(int i) {
		itemList.add(new ItemSprite(world));
		itemList.get(i).spawn(1100 - ((i + 1) * 40), 490, 0);
	}

	private void createWeapons(int i) {
		weaponList.add(new WeaponSprite(world));
		weaponList.get(i).spawn(1100 - ((i + 1) * 40), 460, 0);
	}

	public World getWorld() {
		return world;
	}

	public int getNumOfPlayers() {
		return numOfPlayers;
	}

	public void setNumOfPlayers(int i) {
		numOfPlayers = i;
	}

	public Array<GameCharacter> getPlayerList() {
		return playerList;
	}

	public GameCharacter getPlayer() {
		return player;
	}

	public void update(float delta) {
		world.step(delta, 6, 2); // Step size|Steps for each body to check collision|Accuracy of body position
									// after collision

		checkStairs();

		// check for collected items
		for (int i = 0; i < itemsToRemove.size; i++) {
			bodyToRemove = itemsToRemove.get(i);
			itemList.removeValue((ItemSprite) bodyToRemove.getUserData(), true);
			world.destroyBody(bodyToRemove);
			if (player.getName().equals("Civilian"))
				player.addItem(itemFac.createItem("Disarm Trap"));
			else if (player.getName().equals("Murderer"))
				player.addItem(itemFac.createItem("Trap"));
		}
		itemsToRemove.clear();

		for (int i = 0; i < weaponsToRemove.size; i++) {
			bodyToRemove = weaponsToRemove.get(i);
			weaponList.removeValue((WeaponSprite) bodyToRemove.getUserData(), true);
			world.destroyBody(bodyToRemove);
			if (player.getName().equals("Civilian"))
				player.addWeapon(weaponFac.createWeapon("Bat"));
			else if (player.getName().equals("Murderer"))
				player.addWeapon(weaponFac.createWeapon("Knife"));
		}
		weaponsToRemove.clear();
	}

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
