package com.jkjk.GameWorld;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.GameCharacterFactory;
import com.jkjk.GameObjects.Obstacles;
import com.jkjk.GameObjects.Weapons.WeaponFactory;
import com.jkjk.Host.Helpers.Location;
import com.jkjk.Host.Helpers.ObstaclesHandler;

public class LocalGameSession implements GameSession {
	private static final float TUTORIAL_SPAWN_X = 860f;
	private static final float TUTORIAL_SPAWN_Y = 509.9347f;
	private static final float TUTORIAL_SPAWN_ANGLE = 3.1427f;

	private final GameWorld gWorld;
	private final ConcurrentHashMap<String, Integer> playerIsAlive;
	private final ConcurrentHashMap<String, Integer> playerType;
	private final String[] participantNames;
	private final WeaponFactory weaponFactory;
	private final GameCharacterFactory gameCharacterFactory;
	private final int playerRole;

	private GameCharacter dummy;
	private boolean shotgunCreated;

	public LocalGameSession(GameWorld gWorld, int playerRole) {
		this.gWorld = gWorld;
		this.playerRole = playerRole;
		playerIsAlive = new ConcurrentHashMap<String, Integer>(1);
		playerType = new ConcurrentHashMap<String, Integer>(1);
		participantNames = new String[] { "You" };
		weaponFactory = new WeaponFactory();
		gameCharacterFactory = new GameCharacterFactory();

		playerIsAlive.put("Player 0", 1);
		playerType.put("Player 0", playerRole);
		gWorld.createPlayer(playerRole, TUTORIAL_SPAWN_X, TUTORIAL_SPAWN_Y, TUTORIAL_SPAWN_ANGLE, 0);
		createObstacles();
	}

	private void createObstacles() {
		ObstaclesHandler obstaclesHandler = new ObstaclesHandler();
		int i = 0;
		for (Location ob : obstaclesHandler.getObstacles()) {
			Vector2 location = new Vector2(ob.get()[0], ob.get()[1]);
			if (i == 0) {
				gWorld.getObstacleList().put(location, new Obstacles(gWorld, location, 0));
			} else {
				gWorld.getObstacleList().put(location, new Obstacles(gWorld, location, 1));
			}
			i++;
		}
	}

	@Override
	public boolean getIsGameStart() {
		return true;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public int getNumOfPlayers() {
		return 1;
	}

	@Override
	public String[] getParticipantNames() {
		return participantNames;
	}

	@Override
	public ConcurrentHashMap<String, Integer> get_playerIsAlive() {
		return playerIsAlive;
	}

	@Override
	public ConcurrentHashMap<String, Integer> get_playerType() {
		return playerType;
	}

	@Override
	public void update() {
		if (dummy != null) {
			if (!dummy.isAlive() && !"Ghost".equals(dummy.getType())) {
				float currentPositionX = dummy.getBody().getPosition().x;
				float currentPositionY = dummy.getBody().getPosition().y;
				dummy.getBody().setActive(false);
				dummy.getBody().setTransform(0, 0, 0);
				dummy = gameCharacterFactory.createCharacter("Ghost", dummy.getId(), gWorld, false);
				dummy.set_deathPositionX(currentPositionX);
				dummy.set_deathPositionY(currentPositionY);
				dummy.getBody().getFixtureList().get(0).setUserData("dummy");
				dummy.getBody().setType(BodyType.KinematicBody);
				dummy.spawn(0, 0, 0);
				gWorld.setDummy(dummy);
			} else if (dummy.isAlive()) {
				dummy.update();
			}
		}

		if (!shotgunCreated && gWorld.getNumOfWeaponPartsCollected() >= getNumOfPlayers() * 2
				&& "Civilian".equals(gWorld.getPlayer().getType())) {
			gWorld.getPlayer().addWeapon(weaponFactory.createWeapon("Shotgun", gWorld, gWorld.getPlayer()));
			shotgunCreated = true;
		}
	}

	@Override
	public void render(OrthographicCamera cam, SpriteBatch batch) {
		if (dummy != null && dummy.isAlive()) {
			dummy.render(cam, batch);
		}
	}

	@Override
	public void updatePlayerIsReady() {
	}

	@Override
	public void updatePlayerIsStun(int playerID, int value) {
	}

	@Override
	public void updatePlayerIsAlive(int playerID, int value) {
		playerIsAlive.put("Player " + playerID, value);
	}

	@Override
	public void updatePlayerType(int playerID, int value) {
		playerType.put("Player " + playerID, value);
	}

	@Override
	public void updatePlayerUseItem() {
	}

	@Override
	public void updatePlayerUseWeapon() {
	}

	@Override
	public void updatePlayerUseAbility() {
	}

	@Override
	public void produceTrapLocation(float x, float y) {
	}

	@Override
	public void addWeaponPartCollected() {
		gWorld.addNumOfWeaponPartsCollected();
	}

	@Override
	public void removeTrapLocation(float x, float y) {
	}

	@Override
	public void removeItemLocation(Vector2 position) {
	}

	@Override
	public void removeWeaponLocation(Vector2 position) {
	}

	@Override
	public void removeWeaponPartLocation(Vector2 position) {
	}

	@Override
	public GameCharacter createTutorialDummy() {
		if (dummy != null) {
			return dummy;
		}

		dummy = gameCharacterFactory.createCharacter("Civilian", 100, gWorld, false);
		dummy.getBody().setType(BodyType.KinematicBody);
		dummy.getBody().getFixtureList().get(0).setUserData("dummy");
		dummy.spawn(gWorld.getPlayer().getBody().getPosition().x - 40, gWorld.getPlayer().getBody()
				.getPosition().y, 0);
		gWorld.setDummy(dummy);
		return dummy;
	}

	@Override
	public void endSession() throws IOException {
	}

	public int getPlayerRole() {
		return playerRole;
	}
}
