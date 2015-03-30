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
<<<<<<< HEAD
=======
		if (gameCharacter.getBody().getUserData() == AssetLoader.murToCivAnimation) {
			gameCharacter.getBody().setUserData(AssetLoader.civToMurAnimation);
		} else {
>>>>>>> e4f585b1c9b22bf7b005b2698f0e0befc02f2a78

		if (gameCharacter.isDisguised()) {
			gameCharacter.setDisguise(false);
			System.out.println("Change sprite to murderer");
			gameCharacter.getBody().setUserData(AssetLoader.civToMurAnimation);
		} else {
			gameCharacter.setDisguise(true);
			System.out.println("Change sprite to civilian");
			gameCharacter.getBody().setUserData(AssetLoader.murToCivAnimation);
		}

	}

	@Override
	public void cooldown() {
		cooldown.startCountdown();
	}

}
