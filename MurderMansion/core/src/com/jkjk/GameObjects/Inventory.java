package com.jkjk.GameObjects;

/**
 *	Handles all things related to the item/weapon slots.
 */
public interface Inventory {
	
	/**
	 * Adds weapon/item into inventory slot
	 */
	public void add();
	
	/**
	 * Removes weapon/item from inventory slot
	 */
	public void remove();
	
	/**
	 * Sets weapon/item on cooldown
	 */
	public void cooldown();

}
