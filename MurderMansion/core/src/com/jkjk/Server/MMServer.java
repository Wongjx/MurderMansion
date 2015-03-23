package com.jkjk.Server;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Characters.GameCharacter;

public class MMServer {

	private final int numOfPlayers;
	private Object numOfPlayersSync;

	private final Boolean[] playerIsAlive;
	private final Boolean[] playerIsStun;
	private final GameCharacter[] playerType;
	private final Body[] playerBody; // Location information stored in body

	// private ArrayList<Location> playerLocations;
	private final SpawnBuffer itemSpawnLocations;
	private final SpawnBuffer weaponSpawnLocations;
	private final SpawnBuffer weaponPartSpawnLocations;
	private final SpawnBuffer trapLocations;

	// To Pass: Sprites of objects (items, weapons, players, bat swing, knife stab, shotgun blast, disguise
	// animation
	// HOW?!!?!?!?!?!?!!

	public MMServer(int numOfPlayers) {
		this.numOfPlayers = numOfPlayers;
		playerIsAlive = new Boolean[numOfPlayers];
		playerIsStun = new Boolean[numOfPlayers];
		playerType = new GameCharacter[numOfPlayers];
		playerBody = new Body[numOfPlayers];
		
		itemSpawnLocations = new SpawnBuffer(numOfPlayers);
		weaponSpawnLocations = new SpawnBuffer(numOfPlayers);
		weaponPartSpawnLocations = new SpawnBuffer(numOfPlayers);
		trapLocations = new SpawnBuffer(numOfPlayers);
	}

	public int getNumOfPlayers() {
		synchronized (numOfPlayersSync) {
			return numOfPlayers;
		}
	}

	public Boolean[] getPlayerIsAlive() {
		synchronized (playerIsAlive) {
			return playerIsAlive;
		}
	}

	public void setPlayerIsAlive(int position, boolean value) {
		synchronized (playerIsAlive) {
			playerIsAlive[position] = value;
		}
	}

	public Boolean[] getPlayerIsStun() {
		synchronized (playerIsStun) {
			return playerIsStun;
		}
	}

	public void setPlayerIsStun(int position, boolean value) {
		synchronized (playerIsStun) {
			playerIsStun[position] = value;
		}
	}

	public GameCharacter[] getPlayerType() {
		synchronized (playerType) {
			return playerType;
		}
	}

	public void setPlayerType(int position, GameCharacter value) {
		synchronized (playerType) {
			playerType[position] = value;
		}
	}

	public Body[] getPlayerBody() {
		synchronized (playerBody) {
			return playerBody;
		}
	}

	public void setPlayerBody(int position, Body value) {
		synchronized (playerBody) {
			playerBody[position] = value;
		}
	}

	public SpawnBuffer getItemSpawnLocations() {
		synchronized (itemSpawnLocations) {
			return itemSpawnLocations;
		}
	}

	public void produceItemSpawnLocations(Location location) throws InterruptedException {
		synchronized (itemSpawnLocations) {
			itemSpawnLocations.produce(location);
		}
	}
	
	public void consumeItemSpawnLocations(Location location) throws InterruptedException {
		synchronized (itemSpawnLocations) {
			itemSpawnLocations.consume(location);
		}
	}

	public SpawnBuffer getWeaponSpawnLocations() {
		synchronized (weaponSpawnLocations) {
			return weaponSpawnLocations;
		}
	}

	public void produceWeaponSpawnLocations(Location location) throws InterruptedException {
		synchronized (weaponSpawnLocations) {
			weaponSpawnLocations.produce(location);
		}
	}
	
	public void consumeWeaponSpawnLocations(Location location) throws InterruptedException {
		synchronized (weaponSpawnLocations) {
			weaponSpawnLocations.consume(location);
		}
	}

	public SpawnBuffer getWeaponPartSpawnLocations() {
		synchronized (weaponPartSpawnLocations) {
			return weaponPartSpawnLocations;
		}
	}

	public void produceWeaponPartSpawnLocations(Location location) throws InterruptedException {
		synchronized (weaponPartSpawnLocations) {
			weaponPartSpawnLocations.produce(location);
		}
	}
	
	public void consumeWeaponPartSpawnLocations(Location location) throws InterruptedException {
		synchronized (weaponPartSpawnLocations) {
			weaponPartSpawnLocations.consume(location);
		}
	}

	public SpawnBuffer getTrapLocations() {
		synchronized (trapLocations) {
			return trapLocations;
		}
	}

	public void produceTrapLocations(Location location) throws InterruptedException {
		synchronized (trapLocations) {
			trapLocations.produce(location);
		}
	}
	
	public void consumeTrapLocations(Location location) throws InterruptedException {
		synchronized (trapLocations) {
			trapLocations.consume(location);
		}
	}

}
