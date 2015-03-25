package com.jkjk.Host;

import java.util.Random;

public class MMServer {

	private ItemSpawner itemSpawner;
	private WeaponSpawner weaponSpawner;
	private WeaponPartSpawner weaponPartSpawner;

	private final int numOfPlayers;
	private Random randMurderer;
	private int murdererId;

	private final int[] playerIsAlive; // If 1 -> true; If 0 -> false;
	private final int[] playerIsStun; // If 1 -> true; If 0 -> false;
	private final int[] playerType; // If 0 -> civilian; If 1 -> murderer; If 2 -> Ghost
	private final float[] playerPosition;
	private final float[] playerAngle;

	// private ArrayList<Location> playerLocations;
	private final SpawnBuffer itemSpawnLocations;
	private final SpawnBuffer weaponSpawnLocations;
	private final SpawnBuffer weaponPartSpawnLocations;
	private final SpawnBuffer trapLocations;

	// To Pass: Sprites of objects (items, weapons, players, bat swing, knife stab, shotgun blast, disguise
	// animation
	// HOW?!!?!?!?!?!?!!

	public MMServer(int numOfPlayers) throws InterruptedException {
		this.numOfPlayers = numOfPlayers;
		playerIsAlive = new int[numOfPlayers];
		playerIsStun = new int[numOfPlayers];
		playerType = new int[numOfPlayers];
		playerPosition = new float[numOfPlayers];
		playerAngle = new float[numOfPlayers];

		itemSpawnLocations = new SpawnBuffer(numOfPlayers);
		weaponSpawnLocations = new SpawnBuffer(numOfPlayers);
		weaponPartSpawnLocations = new SpawnBuffer(numOfPlayers);
		trapLocations = new SpawnBuffer(numOfPlayers);
		
		murdererId = randMurderer.nextInt(numOfPlayers);
		
		spawnItems(numOfPlayers*3);
		spawnWeapons(numOfPlayers);
		spawnWeaponParts(numOfPlayers*2);
		
		// Attempt to connect to clients (numOfPlayers)
	}

	private void spawnItems(int numOfItems) throws InterruptedException {
		for (int i = 0; i < numOfItems; i++) {
			produceItemSpawnLocations(itemSpawner.spawn());
		}
	}
	
	private void spawnWeapons(int numOfItems) throws InterruptedException {
		for (int i = 0; i < numOfItems; i++) {
			produceWeaponPartSpawnLocations(weaponSpawner.spawn());
		}
	}
	
	private void spawnWeaponParts(int numOfItems) throws InterruptedException {
		for (int i = 0; i < numOfItems; i++) {
			produceWeaponPartSpawnLocations(weaponPartSpawner.spawn());
		}
	}

	public int getNumOfPlayers() {
			return numOfPlayers;
	}

	public int[] getPlayerIsAlive() {
		synchronized (playerIsAlive) {
			return playerIsAlive;
		}
	}

	public void setPlayerIsAlive(int position, int value) {
		synchronized (playerIsAlive) {
			playerIsAlive[position] = value;
		}
	}

	public int[] getPlayerIsStun() {
		synchronized (playerIsStun) {
			return playerIsStun;
		}
	}

	public void setPlayerIsStun(int position, int value) {
		synchronized (playerIsStun) {
			playerIsStun[position] = value;
		}
	}

	public int[] getPlayerType() {
		synchronized (playerType) {
			return playerType;
		}
	}

	public void setPlayerType(int position, int value) {
		synchronized (playerType) {
			playerType[position] = value;
		}
	}

	public float[] getPlayerPosition() {
		synchronized (playerPosition) {
			return playerPosition;
		}
	}

	public void setPlayerPosition(int position, float value) {
		synchronized (playerPosition) {
			playerPosition[position] = value;
		}
	}	
	
	public float[] getPlayerAngle() {
		synchronized (playerAngle) {
			return playerAngle;
		}
	}

	public void setPlayerAngle(int position, float value) {
		synchronized (playerAngle) {
			playerAngle[position] = value;
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
			itemSpawner.restore(location);
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
			weaponSpawner.restore(location);
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
