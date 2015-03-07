package com.jkjk.GameObjects.Characters;

import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;

/**
 * Handles all things related to characters
 *
 */
public abstract class GameCharacter {
	
	private boolean alive;
	private Weapon weapon;
	private Item item;
	
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
	
	public void addWeapon(Weapon weapon) {
		// TODO Auto-generated method stub
		this.weapon = weapon;
	}
	
	public Weapon getWeapon(){
		return weapon;
	}

	public void cooldownWeapon() {
		// TODO Auto-generated method stub
		
	}

	public void useWeapon() {
		// TODO Auto-generated method stub
		this.weapon = null;
	}

	public void addItem(Item item) {
		// TODO Auto-generated method stub
		this.item = item;
	}
	
	public Item getItem(){
		return item;
	}

	public void cooldownItem() {
		// TODO Auto-generated method stub
		
	}

	public void useItem() {
		this.item = null;
		
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
