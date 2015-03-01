package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.jkjk.GameObjects.ItemSlot;
import com.jkjk.GameObjects.WeaponSlot;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.GameWorld.GameWorld;


public class Civilian extends GameCharacter implements ItemSlot, WeaponSlot {
	
	private GameWorld gWorld;
	private World world;
	
	private int colour;
	private Weapon weapon;
	private Item item;
	
	Civilian(int colour){
		this.colour = colour;
		world = gWorld.getWorld();
		PolygonShape shape = new PolygonShape();
		Vector2[] vertices = new Vector2[3];
		vertices[0].set(0.0f, 0.0f);
		vertices[1].set(1.0f, 0.0f);
		vertices[2].set(0.0f, 1.0f);
		shape.set(vertices);
	}

	@Override
	public void addWeapon(Weapon weapon) {
		// TODO Auto-generated method stub
		this.weapon = weapon;
	}

	@Override
	public void removeWeapon() {
		// TODO Auto-generated method stub
		this.weapon = null;
	}

	@Override
	public void cooldownWeapon() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void useWeapon() {
		// TODO Auto-generated method stub
		
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
	
	public int getColour(){
		return colour;
	}
}
