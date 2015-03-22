package com.jkjk.GameObjects.Items;

import com.jkjk.GameWorld.GameWorld;


public class ItemFactory {
	public Item createItem(String newItemType, GameWorld gWorld){
		if (newItemType.equals("Trap"))
			return new Trap(gWorld);
		else if (newItemType.equals("Disarm Trap"))
			return new DisarmTrap(gWorld);
		else
			return null;
	}
}
