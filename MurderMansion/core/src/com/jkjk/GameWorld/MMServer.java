package com.jkjk.GameWorld;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Characters.GameCharacter;

public class MMServer {

	private int numOfPlayers;
	private Object numOfPlayersSync;

	private ArrayList<Boolean> playerIsAlive;
	private ArrayList<Boolean> playerIsStun;
	private ArrayList<GameCharacter> playerType;
	private ArrayList<Body> playerBody; // Location information stored in body

	// private ArrayList<Location> playerLocations;
	private ArrayList<Location> itemSpawnLocations;
	private ArrayList<Location> weaponSpawnLocations;
	private ArrayList<Location> weaponPartSpawnLocations;
	private ArrayList<Location> trapLocations;

	// To Pass: Sprites of objects (items, weapons, players, bat swing, knife stab, shotgun blast, disguise
	// animation
	// HOW?!!?!?!?!?!?!!

	public MMServer() {

	}

	public int getNumOfPlayers() {
		synchronized (numOfPlayersSync) {
			return numOfPlayers;
		}
	}

	public void setNumOfPlayers(int numOfPlayers) {
		synchronized (numOfPlayersSync) {
			this.numOfPlayers = numOfPlayers;
		}
	}

	public ArrayList<Boolean> getPlayerIsAlive() {
		synchronized (playerIsAlive) {
			return playerIsAlive;
		}
	}

	public void setPlayerIsAlive(ArrayList<Boolean> playerIsAlive) {
		synchronized (playerIsAlive) {
			this.playerIsAlive = playerIsAlive;
		}
	}

	public ArrayList<Boolean> getPlayerIsStun() {
		synchronized (playerIsStun) {
			return playerIsStun;
		}
	}

	public void setPlayerIsStun(ArrayList<Boolean> playerIsStun) {
		synchronized (playerIsStun) {
			this.playerIsStun = playerIsStun;
		}
	}

	public ArrayList<GameCharacter> getPlayerType() {
		synchronized (playerType) {
			return playerType;
		}
	}

	public void setPlayerType(ArrayList<GameCharacter> playerType) {
		synchronized (playerType) {
			this.playerType = playerType;
		}
	}

	public ArrayList<Body> getPlayerBody() {
		synchronized (playerBody) {
			return playerBody;
		}
	}

	public void setPlayerBody(ArrayList<Body> playerBody) {
		synchronized (playerBody) {
			this.playerBody = playerBody;
		}
	}

	public ArrayList<Location> getItemSpawnLocations() {
		synchronized (itemSpawnLocations) {
			return itemSpawnLocations;
		}
	}

	public void setItemSpawnLocations(ArrayList<Location> itemSpawnLocations) {
		synchronized (itemSpawnLocations) {
			this.itemSpawnLocations = itemSpawnLocations;
		}
	}

	public ArrayList<Location> getWeaponSpawnLocations() {
		synchronized (weaponSpawnLocations) {
			return weaponSpawnLocations;
		}
	}

	public void setWeaponSpawnLocations(ArrayList<Location> weaponSpawnLocations) {
		synchronized (weaponSpawnLocations) {
			this.weaponSpawnLocations = weaponSpawnLocations;
		}
	}

	public ArrayList<Location> getWeaponPartSpawnLocations() {
		synchronized (weaponPartSpawnLocations) {
			return weaponPartSpawnLocations;
		}
	}

	public void setWeaponPartSpawnLocations(ArrayList<Location> weaponPartSpawnLocations) {
		synchronized (weaponPartSpawnLocations) {
			this.weaponPartSpawnLocations = weaponPartSpawnLocations;
		}
	}

	public ArrayList<Location> getTrapLocations() {
		synchronized (trapLocations) {
			return trapLocations;
		}
	}

	public void setTrapLocations(ArrayList<Location> trapLocations) {
		synchronized (trapLocations) {
			this.trapLocations = trapLocations;
		}
	}

}

class Location {

	private int x, y;

	Location(int[] a) {
		this.x = a[0];
		this.y = a[1];
	}

	public synchronized int[] get() {
		return new int[] { x, y };
	}

	public synchronized void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

}