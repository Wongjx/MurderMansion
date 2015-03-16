package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.GameWorld.GameWorld;

/**
 * Handles all things related to characters
 *
 */
public abstract class GameCharacter {
	
	private String name;
	
	private boolean alive;
	private boolean itemChange;
	private boolean weaponChange;
	
	private Weapon weapon;
	private Item item;
	private Body body;
	private Body lightBody;
	
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
	
	public Body getLightBody(){return lightBody;}
	public void setLightBody(Body lightBody){this.lightBody = lightBody;}
	
	public void addWeapon(Weapon weapon) { this.weapon = weapon; weaponChange = true; }
	public Weapon getWeapon(){ return weapon; }
	public void cooldownWeapon() { weapon.cooldown(); }
	public void useWeapon(GameWorld gWorld) { weapon.use(gWorld); weapon = null;  weaponChange = true; }
	public boolean getWeaponChange(){ return weaponChange; }
	public void setWeaponChange(boolean weaponChange){ this.weaponChange = weaponChange; }
	
	public void addItem(Item item) { this.item = item; itemChange = true; }
	public Item getItem(){ return item; }
	public void cooldownItem() {	}
	public void useItem(GameWorld gWorld) { item.use(gWorld); item = null; itemChange = true; }
	public boolean getItemChange(){ return itemChange; }
	public void setItemChange(boolean itemChange){ this.itemChange = itemChange; }

	public void update(){	}
	
}
