package com.jkjk.GameObjects.Abilities;


public abstract class Ability {
	
	private boolean onCooldown;
	
	public Ability(){
		onCooldown = false;
	}
	
	public boolean isOnCoolDown(){
		return onCooldown;
	}
	
	public void setCooldown(boolean onCooldown){
		this.onCooldown = onCooldown;
	}
	
	public abstract void use();
	public abstract void cooldown();
	
}
