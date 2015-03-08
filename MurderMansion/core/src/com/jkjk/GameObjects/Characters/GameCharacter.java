package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;

/**
 * Handles all things related to characters
 *
 */
public abstract class GameCharacter {
	
	private String name;
	
	private boolean alive;
	
	private Weapon weapon;
	private Item item;
	private Body body;
	
	private int colour;
	
	public String getName(){ return name; }
	public void setName(String name){ this.name = name; }
	
	public void spawn(){ alive = true; }
	public void die(){ alive = false; }
	public boolean isAlive(){ return alive; }
	
	public int getColour(){ return colour; }
	public void setColour(int colour){ this.colour = colour; }
	
	public Body getBody(){ return body; } 
	public void setBody(Body body){ this.body = body; }
	
	public void addWeapon(Weapon weapon) { this.weapon = weapon; }
	public Weapon getWeapon(){ return weapon; }
	public void cooldownWeapon() {	}
	public void useWeapon() { this.weapon = null; }
	
	public void addItem(Item item) { this.item = item; }
	public Item getItem(){ return item; }
	public void cooldownItem() {	}
	public void useItem() { this.item = null; }

	public void update(){	}
	
}
