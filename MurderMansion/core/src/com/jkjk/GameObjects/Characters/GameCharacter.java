package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.GameWorld.GameWorld;

/**
 * Handles all things related to characters
 *
 */
public abstract class GameCharacter {
	
	private String name;
	
	protected boolean alive;
	private boolean itemChange, weaponChange;
	private boolean stun;
	
	private Weapon weapon;
	private Item item;
	private Body body;
	
	private int colour;
	
	public String getName(){ return name; }
	public void setName(String name){ this.name = name; }
	
	public abstract void spawn(float x, float y, float angle);
	public abstract void die();
	
	public boolean isAlive(){ return alive; }
	public void stun(boolean stun){ this.stun = stun; }
	public boolean isStun(){ return stun; }
	
	public int getColour(){ return colour; }
	public void setColour(int colour){ this.colour = colour; }
	
	public Body getBody(){ return body; } 
	public void setBody(Body body){ this.body = body; }
	
	public void addWeapon(Weapon weapon) { this.weapon = weapon; weaponChange = true; }
	public Weapon getWeapon(){ return weapon; }
	public void useWeapon(GameWorld gWorld) { if (!weapon.isWeaponOnCooldown()){ weapon.use(gWorld); weapon.cooldown();} }
	public boolean getWeaponChange(){ return weaponChange; }
	public void setWeaponChange(boolean weaponChange){ this.weaponChange = weaponChange; }
	
	public void addItem(Item item) { this.item = item; itemChange = true; }
	public Item getItem(){ return item; }
	public void useItem(GameWorld gWorld) { item.use(gWorld); item = null; itemChange = true; }
	public boolean getItemChange(){ return itemChange; }
	public void setItemChange(boolean itemChange){ this.itemChange = itemChange; }

	public void update(){	}
	
}
