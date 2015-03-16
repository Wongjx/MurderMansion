package com.jkjk.GameObjects.Weapons;

import com.jkjk.GameWorld.GameWorld;


public abstract class Weapon {
	
	protected boolean weaponOnCooldown;
	
	public Weapon(){
		weaponOnCooldown = false;
	}
	
	public boolean isWeaponOnCooldown(){
		return weaponOnCooldown;
	}
	
	public void setWeaponOnCooldown(boolean weaponOnCooldown){
		this.weaponOnCooldown = weaponOnCooldown;
	}
	
	public abstract void use(GameWorld gWorld);
	
	public abstract void postUse(GameWorld gWorld);
	
	public abstract void cooldown();
	
	
}
