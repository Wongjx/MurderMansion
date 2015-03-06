package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.ItemSlot;
import com.jkjk.GameObjects.Items.Item;


public class Ghost extends GameCharacter implements ItemSlot {

	private Item item;
	private Body body;
	
	public Ghost(Body body) {
		this.body = body;
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
