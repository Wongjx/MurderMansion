package com.jkjk.GameWorld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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
import com.jkjk.MMHelpers.MMContactListener;

public class GameWorld {
	private GameCharacterFactory gameCharFac;
	private Civilian civilian;
	private Murderer murderer;
	private GameCharacter player;
	private Array<GameCharacter> playerList;

	private ItemSprite itemSprite;
	private ItemFactory itemFac;
	private Array<ItemSprite> itemList;
	
	private WeaponSprite weaponSprite;
	private WeaponFactory weaponFac;
	private Array<WeaponSprite> weaponList;

	private GameRenderer renderer;
	private World world;
	private MMContactListener cl;
	private BodyDef bdef;
	private Body body;

	private int numOfPlayers;
	private int numOfItems, maxItems;
	private int numOfWeapons, maxWeapons;

	public GameWorld(float gameWidth, float gameHeight) {
		world = new World(new Vector2(0, 0), true);
		cl = new MMContactListener(this);
		world.setContactListener(cl);
		bdef = new BodyDef();

		gameCharFac = new GameCharacterFactory();
		playerList = new Array<GameCharacter>();
		numOfPlayers = 4;

		itemFac = new ItemFactory();
		itemList = new Array<ItemSprite>();
		maxItems = numOfPlayers * 2;
		numOfItems = 0;
		
		weaponFac = new WeaponFactory();
		weaponList = new Array<WeaponSprite>();
		maxWeapons = (int) (numOfPlayers*1.2);
		numOfWeapons = 0;

		createWall();
		createPlayer();
		for (int i = 0; i < numOfPlayers-1; i++) {
			createOpponents(i);
		}
		for (int i = 0; i < maxItems; i++) {
			createItems(i);
		}
		for (int i = 0; i < maxWeapons; i++) {
			createWeapons(i);
		}
	}

	private void createWall() {
		// create BODY for WORLD
		bdef.position.set(150, 150);
		// static body - don't move, unaffected by forces *eg. ground
		// kinematic body - don't get affected by forces, but can move *eg. moving platform
		// dynamic body - always get affected by forces *eg. player
		bdef.type = BodyType.StaticBody;
		body = world.createBody(bdef);

		// create FIXTURE for BODY for WORLD
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(100, 10);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("wall");
	}

	private void createPlayer() {
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(100, 100); // Spawn position
		body = world.createBody(bdef);
		player = gameCharFac.createCharacter("Civilian", 0, body);
		player.spawn();
	}

	private void createOpponents(int i) {
		if (i+1 == numOfPlayers) {
			bdef.type = BodyType.KinematicBody;
			bdef.position.set(100 - ((i + 1) * 40), 100); // Spawn position
			body = world.createBody(bdef);
			playerList.add((Murderer) gameCharFac.createCharacter("Murderer", body));
			playerList.get(i).spawn();
		} else {
			bdef.type = BodyType.KinematicBody;
			bdef.position.set(100 - ((i + 1) * 40), 100); // Spawn position
			body = world.createBody(bdef);
			playerList.add((Civilian) gameCharFac.createCharacter("Civilian", i, body));
			playerList.get(i).spawn();
		}
	}

	private void createItems(int i) {
		bdef.type = BodyType.StaticBody;
		bdef.position.set(100 - ((i + 1) * 40), 60); // Spawn position
		body = world.createBody(bdef);
		itemList.add(new ItemSprite(body));
		numOfItems++;
	}
	
	private void createWeapons(int i){
		bdef.type = BodyType.StaticBody;
		bdef.position.set(100 - ((i + 1) * 40), 20); // Spawn position
		body = world.createBody(bdef);
		weaponList.add(new WeaponSprite(body));
		numOfWeapons++;
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

	public GameCharacter getPlayer() {
		return player;
	}

	public void update(float delta) {
		world.step(delta, 6, 2); // Step size|Steps for each body to check collision|Accuracy of body position
									// after collision

		// check for collected items
		Array<Body> itemsToRemove = cl.getItemsToRemove();
		for (int i = 0; i < itemsToRemove.size; i++) {
			Body b = itemsToRemove.get(i);
			itemList.removeValue((ItemSprite) b.getUserData(), true);
			world.destroyBody(itemsToRemove.get(i));
			if (player.getName().equals("Civilian"))
				player.addItem(itemFac.createItem("Disarm Trap"));
			else if (player.getName().equals("Murderer"))
				player.addItem(itemFac.createItem("Trap"));
		}
		itemsToRemove.clear();
		
		Array<Body> weaponsToRemove = cl.getWeaponsToRemove();
		for (int i = 0; i < weaponsToRemove.size; i++) {
			Body b = weaponsToRemove.get(i);
			itemList.removeValue((ItemSprite) b.getUserData(), true);
			world.destroyBody(weaponsToRemove.get(i));
			if (player.getName().equals("Civilian"))
				player.addWeapon(weaponFac.createWeapon("Bat"));
			else if (player.getName().equals("Murderer"))
				player.addWeapon(weaponFac.createWeapon("Knife"));
		}
		weaponsToRemove.clear();
	}

	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}
}
