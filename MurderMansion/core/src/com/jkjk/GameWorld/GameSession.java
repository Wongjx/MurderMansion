package com.jkjk.GameWorld;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jkjk.GameObjects.Characters.GameCharacter;

public interface GameSession {
	boolean getIsGameStart();

	int getId();

	int getNumOfPlayers();

	String[] getParticipantNames();

	ConcurrentHashMap<String, Integer> get_playerIsAlive();

	ConcurrentHashMap<String, Integer> get_playerType();

	void update();

	void render(OrthographicCamera cam, SpriteBatch batch);

	void updatePlayerIsReady();

	void updatePlayerIsStun(int playerID, int value);

	void updatePlayerIsAlive(int playerID, int value);

	void updatePlayerType(int playerID, int value);

	void updatePlayerUseItem();

	void updatePlayerUseWeapon();

	void updatePlayerUseAbility();

	void produceTrapLocation(float x, float y);

	void addWeaponPartCollected();

	void removeTrapLocation(float x, float y);

	void removeItemLocation(Vector2 position);

	void removeWeaponLocation(Vector2 position);

	void removeWeaponPartLocation(Vector2 position);

	GameCharacter createTutorialDummy();

	void endSession() throws IOException;
}
