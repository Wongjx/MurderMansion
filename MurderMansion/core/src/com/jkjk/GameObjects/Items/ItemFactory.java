package com.jkjk.GameObjects.Items;

import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameSession;
import com.jkjk.GameWorld.GameWorld;


public class ItemFactory {
	public Item createItem(String newItemType, GameWorld gWorld, GameSession session,
			GameCharacter character) {
		if (newItemType.equals("Trap"))
			return new Trap(gWorld, session, character);
		else if (newItemType.equals("Disarm Trap"))
			return new DisarmTrap(gWorld, character);
		else
			return null;
	}
}
