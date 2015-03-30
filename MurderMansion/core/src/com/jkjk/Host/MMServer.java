package com.jkjk.Host;

import java.util.Random;

public class MMServer {

	private long startTime;
	private long runTime;
	private long prevRunTime;

	private final int numOfPlayers;
	private Random randMurderer;
	private int murdererId;

	private final int[] playerIsAlive; // If 1 -> true; If 0 -> false;
	private final int[] playerIsStun; // If 1 -> true; If 0 -> false;
	private final int[] playerType; // If 0 -> civilian; If 1 -> murderer; If 2 -> Ghost
	private final float[] playerPosition;
	private final float[] playerAngle;

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
		playerIsAlive = new int[numOfPlayers];
		playerIsStun = new int[numOfPlayers];
		playerType = new int[numOfPlayers];
		playerPosition = new float[numOfPlayers];
		playerAngle = new float[numOfPlayers];

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

		// Attempt to connect to clients (numOfPlayers)
	}

	public void update() {
		runTime = System.currentTimeMillis() - startTime;
		
		// Item/Weapon/WeaponPart Spawn *NEEDS TO BE BALANCED TO FIT GAMEPLAY
		if (runTime%10000 < prevRunTime%10000) {
			System.out.println("SPAWN!");
			if (!itemLocations.isFull())
				spawnItems(1);
			if (!weaponLocations.isFull())
				spawnWeapons(1);
			if (!weaponPartLocations.isFull())
				spawnWeaponParts(1);
		}
		
		// Opens random door in mansion *TO BE IMPLEMENTED
		if (runTime%60000 < prevRunTime%60000){
			System.out.println("NEW DOOR OPENS!");
		}
		
		prevRunTime = runTime;
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
