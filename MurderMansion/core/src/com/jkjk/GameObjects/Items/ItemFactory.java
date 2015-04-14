package com.jkjk.GameObjects.Items;

import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.MMClient;


public class ItemFactory {
	public Item createItem(String newItemType, GameWorld gWorld,MMClient client, GameCharacter character){
		if (newItemType.equals("Trap"))
			return new Trap(gWorld,client, character);
		else if (newItemType.equals("Disarm Trap"))
			return new DisarmTrap(gWorld, character);
		else
			return null;
	}
}
