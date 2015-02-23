package com.jkjk.GameObjects.Characters;

/**
 * Handles all things related to characters
 *
 */
public interface GameCharacter {
	/**
	 * Creates character
	 */
	public void spawn();
	
	/**
	 * Kills character
	 */
	public void die();
	
	/**
	 * Updates movement
	 */
	public void update();
	
	/**
	 * Checks alive status
	 */
	public void isAlive();
	
}
