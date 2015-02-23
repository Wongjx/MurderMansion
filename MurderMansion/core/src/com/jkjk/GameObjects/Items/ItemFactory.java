package com.jkjk.GameObjects.Items;


public class ItemFactory {
	public Item createItem(String newItemType){
		if (newItemType.equals("Trap"))
			return new Trap();
		else if (newItemType.equals("Disarm Trap"))
			return new DisarmTrap();
		else
			return null;
	}
}
