package com.jkjk.GameObjects.Weapons;

import com.jkjk.GameWorld.GameWorld;


public abstract class Weapon {
	
	public abstract void use(GameWorld gWorld);
	
	public abstract void cooldown();
	
	
}
