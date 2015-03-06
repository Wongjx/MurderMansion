package com.jkjk.GameObjects.Characters;

/**
 * Handles all things related to characters
 *
 */
public abstract class GameCharacter {
	
	private float velocity;
	private boolean alive;
	
	public float getVelocity(){
		return velocity;
	}
	
	/**
	 * Creates character
	 */
	public void spawn(){
		alive = true;
	}
	
	/**
	 * Kills character
	 */
	public void die(){
		alive = false;
	}
	
	/**
	 * Checks alive status
	 */
	public boolean isAlive(){
		return alive;
	}
	
	/**
	 * Updates movement
	 */
	public void update(){
		
	}
	
}
