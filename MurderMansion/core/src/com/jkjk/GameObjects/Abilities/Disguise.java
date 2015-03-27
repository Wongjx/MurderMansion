package com.jkjk.GameObjects.Abilities;

import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.MMHelpers.AssetLoader;

public class Disguise extends Ability {

	public Disguise(GameCharacter gameCharacter) {
		super(gameCharacter);
		cooldown = new Duration(10000); // 10s cooldown
	}

	@Override
	public void use() {
		if (gameCharacter.getBody().getUserData() == AssetLoader.murToCivAnimation) {
			gameCharacter.getBody().setUserData(AssetLoader.civToMurAnimation);
		} else {

		}
		if (gameCharacter.isDisguised()) {
			gameCharacter.setDisguise(false);
			System.out.println("Change sprite to murderer");
		} else {
			gameCharacter.setDisguise(true);
			System.out.println("Change sprite to civilian");
		}

	}

	@Override
	public void cooldown() {
		cooldown.startCountdown();
	}

}
