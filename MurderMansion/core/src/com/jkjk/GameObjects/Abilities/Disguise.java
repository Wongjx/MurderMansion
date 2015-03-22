package com.jkjk.GameObjects.Abilities;

import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Characters.GameCharacter;

public class Disguise extends Ability {
	
	public Disguise(GameCharacter gameCharacter) {
		super(gameCharacter);
		cooldown = new Duration(300000);
	}
	
	@Override
	public void use() {
		// if murderer:
		// 		if gameCharacter.sprite == civilian:
		// 			gameCharacter.setSprite = murderer;
		// 		else:
		//			gameCharacter.setSprite = civilian;
		
	}
	
	@Override
	public void cooldown() {
		cooldown.startCooldown();
	}

}
