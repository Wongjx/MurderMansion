package com.jkjk.Host;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStatuses implements Subject{
	private final int numOfPlayers;
	private ArrayList<Observer> clients;
	
	private final ConcurrentHashMap<String, Integer> playerIsAlive; // If 1 -> true; If 0 -> false;
	private final ConcurrentHashMap<String, Integer> playerIsStun; // If 1 -> true; If 0 -> false;
	private final ConcurrentHashMap<String, Integer> playerType; // If 0 -> murderer; If 1 -> civilian; If 2-> Ghost
	private final ConcurrentHashMap<String, float[]> playerPosition;
	private final ConcurrentHashMap<String, Float> playerAngle;
	
	private String message;
	
	public PlayerStatuses(int numOfPlayers){
		this.numOfPlayers=numOfPlayers;
		this.clients=new ArrayList<Observer>();
		this.playerIsAlive = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		this.playerIsStun = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		this.playerType = new ConcurrentHashMap<String, Integer>(numOfPlayers);
		this.playerPosition = new ConcurrentHashMap<String, float[]>(numOfPlayers);
		this.playerAngle = new ConcurrentHashMap<String, Float>(numOfPlayers);
	}
	
	/** Update client alive status
	 * @param id Id of client to update
	 * @param status 1 -> true; 0 -> false;
	 */
	public void updateIsAlive(int id,int status){
		playerIsAlive.put("Player "+id, status);
		//TODO change message to update isAlive status message
		message="something";
		updateAll(id);
	}
	
	/** Update player stun state
	 * @param id Id of client to update
	 * @param status 1 -> true; 0 -> false;
	 */
	public void updateIsStun(int id,int status){
		playerIsStun.put("Player "+id, status);
		//TODO change message to update isStuns status message
		message="something";
		updateAll(id);
	}
	
	/**Update player type
	 * @param id	id of client to update
	 * @param status 0 -> murderer; 1 -> civilian; 2-> Ghost
	 */
	public void updateType(int id,int status){
		playerType.put("Player "+id, status);
		//TODO change message to update playerType status message
		message="something";
		updateAll(id);
	}
	
	
	/**Update player position
	 * @param id	id of client to update
	 * @param position position[0]= X coordinate ; positon[1] = Y coordinate 
	 */
	public void updatePosition(int id,float[] position){
		playerPosition.put("Player "+id, position);
		//TODO change message to update playerPostion status message
		message="something";
		updateAll(id);
	}
	
	/**Update player angle
	 * @param id	id of client to update
	 * @param angle Angle of player
	 */
	public void updateAngle(int id,float angle){
		playerAngle.put("Player "+id, angle);
		//TODO change message to update playerAngle status message
		message="something";
		updateAll(id);
	}
	/**Update player angle
	 * @param id	id of client to update
	 * @param angle Angle of player
	 */
	public void updatePositionAndAngle(int id,float[] position ,float angle){
		playerPosition.put("Player "+id, position);
		playerAngle.put("Player "+id, angle);
		message="loc_"+id+"_"+Float.toString(position[0])+"_"+Float.toString(position[1])+"_"+Float.toString(angle);
		updateAll(id);
	}

	
	@Override
	public void updateAll(int origin) {
		for (int i=0;i<clients.size();i++){
			if (i==origin){
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
	
	
	public int getPlayerIsAliveValue(String key){
		return playerIsAlive.get(key);
	}
	public int getPlayerIsStunValue(String key){
		return playerIsStun.get(key);
	}
	public int getPlayerTypeValue(String key){
		return playerType.get(key);
	}
	public float[] getPlayerPositionValue(String key){
		return playerPosition.get(key);
	}
	public float getPlayerAngleValue(String key){
		return playerAngle.get(key);
	}
	public ArrayList<Observer> getClients(){
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


}
