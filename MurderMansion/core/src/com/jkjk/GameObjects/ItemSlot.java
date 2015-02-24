package com.jkjk.GameObjects;

import com.jkjk.GameObjects.Items.Item;

public interface ItemSlot {

	public void addItem(Item item);

	public void removeItem();
	
	public void cooldownItem();
	
	public void useItem();	

}
