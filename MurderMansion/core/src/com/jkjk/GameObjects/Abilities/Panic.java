package com.jkjk.GameObjects.Abilities;

import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Characters.GameCharacter;

public class Panic extends Ability {
	
	private Duration duration;
	private boolean active;
	
	public Panic(GameCharacter gameCharacter) {
		super(gameCharacter);
		cooldown = new Duration(300000);
		duration = new Duration(5000);
	}
	
	@Override
	public void use() {
		gameCharacter.setVelocity(gameCharacter.getVelocity()*1.5f);
		duration.startCooldown();
		active = true;
	}
	
	@Override
	public void cooldown() {
		cooldown.startCooldown();
	}
	
	public void update(){
		super.update();
		duration.update();
		if (!duration.isOnCooldown() && active){
			gameCharacter.setVelocity(gameCharacter.getVelocity()/1.5f);
			active = false;
		}
	}


}
