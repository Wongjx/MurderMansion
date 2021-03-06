package com.jkjk.Host;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStatuses implements Subject {
	private final int numOfPlayers;
	private ArrayList<Observer> clients;

	private final ConcurrentHashMap<String, Integer> playerIsAlive; // If 1 -> true; If 0 -> false;
	private final ConcurrentHashMap<String, Integer> playerIsStun; // If 1 -> true; If 0 -> false;
	private final ConcurrentHashMap<String, Integer> playerType; // If 0 -> murderer; If 1 -> civilian; If 2->
																	// Ghost
	private final ConcurrentHashMap<String, float[]> playerPosition;
	private final ConcurrentHashMap<String, Float> playerAngle;
	private final ConcurrentHashMap<String, float[]> playerVelocity;
	private final ConcurrentHashMap<String, Integer> playerIsInSafeRegion;

	private String message;

	public PlayerStatuses(int numOfPlayers) {
		this.numOfPlayers = numOfPlayers;
		this.clients = new ArrayList<Observer>();

		this.playerType = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		this.playerIsAlive = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		this.playerIsStun = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		this.playerIsInSafeRegion = new ConcurrentHashMap<String, Integer>(numOfPlayers);

		this.playerPosition = new ConcurrentHashMap<String, float[]>(numOfPlayers);
		this.playerAngle = new ConcurrentHashMap<String, Float>(numOfPlayers);
		this.playerVelocity = new ConcurrentHashMap<String, float[]>(numOfPlayers);

	}

	/**
	 * Update client alive status
	 * 
	 * @param id
	 *            Id of client to update
	 * @param status
	 *            1 -> true; 0 -> false;
	 */
	public void updateIsAlive(int origin, int id, int status) {
		playerIsAlive.put("Player " + id, status);
		message = "alive_" + origin + "_" + id + "_" + status;
		updateAll(origin);
	}

	/**
	 * Update player stun state
	 * 
	 * @param id
	 *            Id of client to update
	 * @param status
	 *            1 -> true; 0 -> false;
	 */
	public void updateIsStun(int origin, int id, int status) {
		playerIsStun.put("Player " + id, status);
		message = "stun_" + origin + "_" + id + "_" + status;
		updateAll(origin);
	}

	/**
	 * Update player use item state
	 * 
	 * @param id
	 *            Id of client to update
	 * @param status
	 *            1 -> using; 0 -> normal;
	 */
	public void updateUseItem(int id) {
		message = "useItem_" + id;
		updateAll(id);
	}

	/**
	 * Update player use weapon state
	 * 
	 * @param id
	 *            Id of client to update
	 * @param status
	 *            1 -> using; 0 -> normal;
	 */
	public void updateUseWeapon(int id) {
		message = "useWeapon_" + id;
		updateAll(id);
	}

	/**
	 * Update player use ability state
	 * 
	 * @param id
	 *            Id of client to update
	 * @param status
	 *            1 -> using; 0 -> normal;
	 */
	public void updateUseAbility(int id) {
		message = "useAbility_" + id;
		updateAll(id);
	}

	/**
	 * Update player to create shotgun
	 */
	public void updateShotgunCreated() {
		message = "createShotgun";
		updateAll(-1);
	}

	public void updateWeaponPartsCollected() {
		message = "weaponPartCollected";
		updateAll(-1);
	}

	/**
	 * Update player type
	 * 
	 * @param origin
	 *            original sender of update
	 * @param id
	 *            id of client to update
	 * @param status
	 *            0 -> murderer; 1 -> civilian; 2-> Ghost
	 */
	public void updateType(int origin, int id, int status) {
		playerType.put("Player " + id, status);
		message = "type_" + origin + "_" + id + "_" + status;
		updateAll(origin);
	}

	/**
	 * Update player position
	 * 
	 * @param id
	 *            id of client to update
	 * @param position
	 *            position[0]= X coordinate ; positon[1] = Y coordinate
	 */
	public void updatePosition(int id, float[] position) {
		playerPosition.put("Player " + id, position);
		message = "pos_" + id + "_" + Float.toString(position[0]) + "_" + Float.toString(position[1]);
		updateAll(id);
	}

	/**
	 * Update player angle
	 * 
	 * @param id
	 *            id of client to update
	 * @param angle
	 *            Angle of player
	 */
	public void updateAngle(int id, float angle) {
		playerAngle.put("Player " + id, angle);
		message = "ang_" + id + "_" + Float.toString(angle);
		updateAll(id);
	}

	public void updateVelocity(int id, float velocityX, float velocityY) {
		playerVelocity.put("Player " + id, new float[] { velocityX, velocityY });
		message = "vel_" + id + "_" + Float.toString(velocityX) + "_" + Float.toString(velocityY);
		updateAll(id);
	}

	/**
	 * Update player angle
	 * 
	 * @param id
	 *            id of client to update
	 * @param angle
	 *            Angle of player
	 */
	public void updatePositionAndAngle(int id, float[] position, float angle, float velocityX, float velocityY) {
		playerPosition.put("Player " + id, position);
		playerAngle.put("Player " + id, angle);
		playerVelocity.put("Player " + id, new float[] { velocityX, velocityY });
		message = "loc_" + id + "_" + Float.toString(position[0]) + "_" + Float.toString(position[1]) + "_"
				+ Float.toString(angle) + "_" + Float.toString(velocityX) + "_" + Float.toString(velocityY);
		updateAll(id);
	}

	/**
	 * Checks if player is in safe region or not
	 * 
	 * @param id
	 *            id of player
	 * @param status
	 *            If player is in safe region or not (1 is true, 0 is false)
	 */
	public void updateIsInSafeRegion(int id, int status) {
		playerIsInSafeRegion.put("Player " + id, status);
	}

	@Override
	public void updateAll(int origin) {
		for (int i = 0; i < clients.size(); i++) {
			if (i == origin) {
				continue;
			}
			clients.get(i).update(message);
		}
	}

	@Override
	public void register(Observer obs) {
		this.clients.add(obs);
	}

	@Override
	public void unregister(Observer obs) {
		this.clients.remove(obs);
	}

	public int getPlayerIsAliveValue(String key) {
		return playerIsAlive.get(key);
	}

	public int getPlayerIsStunValue(String key) {
		return playerIsStun.get(key);
	}

	public int getPlayerTypeValue(String key) {
		return playerType.get(key);
	}

	public float[] getPlayerPositionValue(String key) {
		return playerPosition.get(key);
	}

	public float getPlayerAngleValue(String key) {
		return playerAngle.get(key);
	}

	public float[] getPlayerVelocityValue(String key) {
		return playerVelocity.get(key);
	}

	public int getPlayerIsInSafeRegion(String key) {
		return playerIsInSafeRegion.get(key);
	}

	public ArrayList<Observer> getClients() {
		return clients;
	}

	public ConcurrentHashMap<String, Integer> getPlayerIsAlive() {
		return playerIsAlive;
	}

	public ConcurrentHashMap<String, Integer> getPlayerIsStun() {
		return playerIsStun;
	}

	public ConcurrentHashMap<String, Integer> getPlayerType() {
		return playerType;
	}

	public ConcurrentHashMap<String, float[]> getPlayerPosition() {
		return playerPosition;
	}

	public ConcurrentHashMap<String, Float> getPlayerAngle() {
		return playerAngle;
	}

	public ConcurrentHashMap<String, float[]> getPlayerVelocity() {
		return playerVelocity;
	}

	public ConcurrentHashMap<String, Integer> getPlayerIsInSafeRegion() {
		return playerIsInSafeRegion;
	}

}
