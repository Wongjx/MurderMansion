package com.jkjk.GameObjects.Abilities;

import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.Murderer;

public class Disguise extends Ability {
	
	Disguise(GameCharacter gameCharacter) {
		super(gameCharacter);
		cooldown = new Duration(10000); // 10s cooldown
	}

	@Override
	public void use() {
		if (((Murderer)gameCharacter).isDisguised()) {
			((Murderer)gameCharacter).setDisguise(false);
		} 
		else {
			((Murderer)gameCharacter).setDisguise(true);
		}


	}

	@Override
	public void cooldown() {
		cooldown.startCountdown();
	}

}
