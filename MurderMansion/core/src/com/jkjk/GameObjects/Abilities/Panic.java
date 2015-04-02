package com.jkjk.GameObjects.Abilities;

import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.MMHelpers.AssetLoader;

public class Panic extends Ability {
	
	private Duration duration;
	private boolean active;
	
	public Panic(GameCharacter gameCharacter) {
		super(gameCharacter);
		cooldown = new Duration(300000);	// 5min cooldown
		duration = new Duration(5000);	// 5s duration
	}
	
	@Override
	public void use() {
		gameCharacter.getBody().setUserData(AssetLoader.civPanicAnimation);
		gameCharacter.setVelocity(gameCharacter.getVelocity()*1.5f);
		duration.startCountdown();
		active = true;
	}
	
	@Override
	public void cooldown() {
		cooldown.startCountdown();
	}
	
	public void update(){
		super.update();
		duration.update();
		if (!duration.isCountingDown() && active){
			gameCharacter.getBody().setUserData(AssetLoader.civAnimation);
			gameCharacter.setVelocity(gameCharacter.getVelocity()/1.5f);
			active = false;
		}
	}


}
