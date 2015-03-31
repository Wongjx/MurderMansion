package com.jkjk.Host;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class MMServer {

	private long startTime;
	private long runTime;
	private long nextItemSpawnTime;
	private long nextObstacleRemoveTime;

	private final int numOfPlayers;
	private Random randMurderer;
	private int murdererId;

	private final ConcurrentHashMap<String, Integer> playerIsAlive; // If 1 -> true; If 0 -> false;
	private final ConcurrentHashMap<String, Integer> playerIsStun; // If 1 -> true; If 0 -> false;
	private final ConcurrentHashMap<String, Integer> playerType; // If 0 -> murderer; If 1 -> civilian; If 2
																	// -> Ghost
	private final ConcurrentHashMap<String, float[]> playerPosition;
	private final ConcurrentHashMap<String, Float> playerAngle;

	// private ArrayList<Location> playerLocations;
	private final SpawnBuffer itemLocations;
	private final SpawnBuffer weaponLocations;
	private final SpawnBuffer weaponPartLocations;
	private final SpawnBuffer trapLocations;

	private final ItemSpawner itemSpawner;
	private final WeaponSpawner weaponSpawner;
	private final WeaponPartSpawner weaponPartSpawner;

	// To Pass: Sprites of objects (items, weapons, players, bat swing, knife stab, shotgun blast, disguise
	// animation
	// HOW?!!?!?!?!?!?!!

	public MMServer(int numOfPlayers) {
		startTime = System.currentTimeMillis();
		this.numOfPlayers = numOfPlayers;
		playerIsAlive = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerIsStun = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerType = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		playerPosition = new ConcurrentHashMap<String, float[]>(numOfPlayers);
		playerAngle = new ConcurrentHashMap<String, Float>(numOfPlayers);

		itemLocations = new SpawnBuffer(numOfPlayers * 3);
		weaponLocations = new SpawnBuffer(numOfPlayers);
		weaponPartLocations = new SpawnBuffer(numOfPlayers * 2);
		trapLocations = new SpawnBuffer(numOfPlayers);

		itemSpawner = new ItemSpawner();
		weaponSpawner = new WeaponSpawner();
		weaponPartSpawner = new WeaponPartSpawner();

		randMurderer = new Random();
		murdererId = randMurderer.nextInt(numOfPlayers);

		spawnItems(numOfPlayers * 2);
		spawnWeapons(numOfPlayers);
		spawnWeaponParts(numOfPlayers);

		nextItemSpawnTime = 10000;
		nextObstacleRemoveTime = 60000;

		initPlayers();

		// Attempt to connect to clients (numOfPlayers)
	}

	public void update() {
		runTime = System.currentTimeMillis() - startTime;

		// Item/Weapon/WeaponPart Spawn *NEEDS TO BE BALANCED TO FIT GAMEPLAY
		if (runTime > nextItemSpawnTime) {
			System.out.println("SPAWN!");
			if (!itemLocations.isFull())
				spawnItems(1);
			if (!weaponLocations.isFull())
				spawnWeapons(1);
			if (!weaponPartLocations.isFull())
				spawnWeaponParts(1);
			nextItemSpawnTime = new Random().nextInt(10000) + runTime + 5000;
		}

		// Opens random door in mansion *TO BE IMPLEMENTED
		if (runTime > nextObstacleRemoveTime) {
			System.out.println("NEW DOOR OPENS!");
			nextObstacleRemoveTime = new Random().nextInt(10000) + runTime + 60000;
		}
	}

	private void initPlayers() {
		for (int i = 0; i < numOfPlayers; i++) {
			playerIsAlive.put("Player " + i, 1);
			playerIsStun.put("Player " + i, 0);
			if (i == murdererId) {
				playerType.put("Player " + i, 0);
			} else {
				playerType.put("Player " + i, 1);
			}
			playerPosition.put("Player " + i, new float[] { 1010 - ((i + 1) * 40), 515 });
			playerAngle.put("Player " + i, 0f);
		}
	}

	private void spawnItems(int numOfItems) {
		for (int i = 0; i < numOfItems; i++) {
			produceItem(itemSpawner.spawn());
		}
	}

	private void spawnWeapons(int numOfItems) {
		for (int i = 0; i < numOfItems; i++) {
			produceWeapon(weaponSpawner.spawn());
		}
	}

	private void spawnWeaponParts(int numOfItems) {
		for (int i = 0; i < numOfItems; i++) {
			produceWeaponPart(weaponPartSpawner.spawn());
		}
	}

	public int getNumOfPlayers() {
		return numOfPlayers;
	}

	public ConcurrentHashMap<String, Integer> getPlayerIsAlive() {
		return playerIsAlive;
	}

	public void setPlayerIsAlive(String key, int value) {
		playerIsAlive.put(key, value);
	}

	public ConcurrentHashMap<String, Integer> getPlayerIsStun() {
		return playerIsStun;
	}

	public void setPlayerIsStun(String key, int value) {
		playerIsAlive.put(key, value);
	}

	public ConcurrentHashMap<String, Integer> getPlayerType() {
		return playerType;
	}

	public void setPlayerType(String key, int value) {
		playerType.put(key, value);
	}

	public ConcurrentHashMap<String, float[]> getPlayerPosition() {
		return playerPosition;
	}

	public void setPlayerPosition(String key, float[] value) {
		playerPosition.put(key, value);
	}

	public ConcurrentHashMap<String, Float> getPlayerAngle() {
		return playerAngle;
	}

	public void setPlayerAngle(String key, float value) {
		playerAngle.put(key, value);
	}

	public SpawnBuffer getItemLocations() {
		synchronized (itemLocations) {
			return itemLocations;
		}
	}

	public void produceItem(Location location) {
		synchronized (itemLocations) {
			itemLocations.produce(location);
		}
	}

	public void consumeItem(Location location) throws InterruptedException {
		synchronized (itemLocations) {
			itemLocations.consume(location);
			itemSpawner.restore(location);
		}
	}

	public SpawnBuffer getWeaponLocations() {
		synchronized (weaponLocations) {
			return weaponLocations;
		}
	}

	public void produceWeapon(Location location) {
		synchronized (weaponLocations) {
			weaponLocations.produce(location);
		}
	}

	public void consumeWeapon(Location location) {
		synchronized (weaponLocations) {
			weaponLocations.consume(location);
			weaponSpawner.restore(location);
		}
	}

	public SpawnBuffer getWeaponPartLocations() {
		synchronized (weaponPartLocations) {
			return weaponPartLocations;
		}
	}

	public void produceWeaponPart(Location location) {
		synchronized (weaponPartLocations) {
			weaponPartLocations.produce(location);
		}
	}

	public void consumeWeaponPart(Location location) {
		synchronized (weaponPartLocations) {
			weaponPartLocations.consume(location);
		}
	}

	public SpawnBuffer getTrapLocations() {
		synchronized (trapLocations) {
			return trapLocations;
		}
	}

	public void produceTrap(Location location) throws InterruptedException {
		synchronized (trapLocations) {
			trapLocations.produce(location);
		}
	}

	public void consumeTrap(Location location) throws InterruptedException {
		synchronized (trapLocations) {
			trapLocations.consume(location);
		}
	}

}
