package com.jkjk.GameObjects.Abilities;

import com.jkjk.GameObjects.Cooldown;
import com.jkjk.GameObjects.Characters.GameCharacter;


public abstract class Ability {
	
	protected Cooldown cooldown;
	protected GameCharacter gameCharacter;
	
	public Ability(GameCharacter gameCharacter){
		this.gameCharacter = gameCharacter;
	}
	
	public boolean isOnCoolDown(){
		return cooldown.isOnCooldown();
	}
	
	public abstract void use();
	public abstract void cooldown();
	
	public void update(){
		cooldown.update();
	}
	
}
