package com.jkjk.GameObjects.Items;

import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.MMClient;


public class ItemFactory {
	public Item createItem(String newItemType, GameWorld gWorld,MMClient client){
		if (newItemType.equals("Trap"))
			return new Trap(gWorld,client);
		else if (newItemType.equals("Disarm Trap"))
			return new DisarmTrap(gWorld);
		else
			return null;
	}
}
