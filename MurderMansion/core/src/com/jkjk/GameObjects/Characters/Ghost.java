package com.jkjk.GameObjects.Characters;

import com.jkjk.GameObjects.ItemSlot;
import com.jkjk.GameObjects.Items.Item;


public class Ghost extends GameCharacter implements ItemSlot {

	private Item item;
	
	public Ghost() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addItem(Item item) {
		// TODO Auto-generated method stub
		this.item = item;
	}

	@Override
	public void removeItem() {
		// TODO Auto-generated method stub
		this.item = null;
	}

	@Override
	public void cooldownItem() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void useItem() {
		// TODO Auto-generated method stub
		
	}

}
