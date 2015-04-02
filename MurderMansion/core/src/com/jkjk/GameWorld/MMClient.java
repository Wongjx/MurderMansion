package com.jkjk.GameWorld;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.jkjk.GameObjects.WeaponPartSprite;
import com.jkjk.GameObjects.Characters.Civilian;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.Murderer;
import com.jkjk.GameObjects.Items.ItemSprite;
import com.jkjk.GameObjects.Items.Trap;
import com.jkjk.GameObjects.Weapons.WeaponSprite;
import com.jkjk.Host.MMServer;

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

	private MMServer server;
	private GameWorld gWorld;
	private GameRenderer renderer;

	private Array<GameCharacter> playerList;
	private int numOfPlayers;
	private int id;

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
	 */
	public MMClient(MMServer server, GameWorld gWorld, GameRenderer renderer) {
		// Attempt to connect to Server
		this.server = server;
		this.gWorld = gWorld;
		this.renderer = renderer;
		id = 0;

		playerList = new Array<GameCharacter>();
		numOfPlayers = server.getNumOfPlayers();

		gWorld.createPlayer(server.getPlayerType().get("Player " + id));
//		gWorld.createPlayer(1);

		for (int i = 1; i < numOfPlayers; i++) {
			createOpponents(server.getPlayerType().get("Player " + i));
		}

		for (int i = 0; i < numOfPlayers * 2; i++) {
			createItems(1060 - (i * 40), 490);
			createWeapons(1060 - (i * 40), 460);
			createWeaponParts(1060 - (i * 40), 430);
		}
		

		// DEBUG
		Trap trap = new Trap(gWorld);
		trap.endUse();
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
	public void render(OrthographicCamera cam, SpriteBatch batch) {
		for (GameCharacter gc : getPlayerList()) {
			gc.render(cam, batch);
		}
	}

	// FOR DEBUG PURPOSE
	private void createOpponents(int i) {
		if (i == 0) {
			playerList.add((Murderer) gWorld.getGameCharFac().createCharacter("Murderer", i, gWorld,
					false));
			playerList.get(playerList.size - 1).getBody().setType(BodyType.KinematicBody);
			playerList.get(playerList.size - 1).spawn(1010 - (((playerList.size - 1) + 1) * 40), 515, 0);
		} else {
			playerList.add((Civilian) gWorld.getGameCharFac().createCharacter("Civilian", i, gWorld,
					false));
			playerList.get(playerList.size - 1).getBody().setType(BodyType.KinematicBody);
			playerList.get(playerList.size - 1).spawn(1010 - (((playerList.size - 1) + 1) * 40), 515, 0);
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
		gWorld.getItemList().put(new Vector2(x,y), is);
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
		gWorld.getWeaponList().put(new Vector2(x,y), ws);
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
		gWorld.getWeaponPartList().put(new Vector2(x,y), wps);
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
	public Array<GameCharacter> getPlayerList() {
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
					.setTransform(server.getPlayerPosition().get("Player " + i)[0],
							server.getPlayerPosition().get("Player " + i)[0],
							server.getPlayerAngle().get("Player " + i));
		}
	}

	/**
	 * Updates the locations of all items on the map.
	 */
	private void itemLocations() {
		server.getItemLocations();
	}

	/**
	 * Updates the locations of all weapon on the map.
	 */
	private void weaponLocations() {
		server.getWeaponLocations();
	}

	/**
	 * Updates the locations of all weapon parts on the map.
	 */
	private void weaponPartLocations() {
		server.getWeaponPartLocations();
	}

	/**
	 * Updates the locations of all traps on the map.
	 */
	private void trapLocations() {
		server.getTrapLocations();
	}

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

}
