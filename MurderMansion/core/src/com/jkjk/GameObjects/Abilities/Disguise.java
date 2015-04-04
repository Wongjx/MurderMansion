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
			System.out.println("Change sprite to murderer");
			//gameCharacter.getBody().setUserData(AssetLoader.civToMurAnimation);
		} 
		else {
			((Murderer)gameCharacter).setDisguise(true);
			System.out.println("Change sprite to civilian");
			//gameCharacter.getBody().setUserData(AssetLoader.murToCivAnimation);
		}


	}

	@Override
	public void cooldown() {
		cooldown.startCountdown();
	}

}
