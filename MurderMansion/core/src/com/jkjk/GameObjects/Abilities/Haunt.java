package com.jkjk.GameObjects.Abilities;

import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.MMHelpers.AssetLoader;

public class Haunt extends Ability {

	public Haunt(GameCharacter gameCharacter) {
		super(gameCharacter);
		cooldown = new Duration(30000);
	}

	@Override
	public void use() {
		//gameCharacter.getBody().setUserData(AssetLoader.ghostHaunt);
		System.out.println("used haunt");
		
	}

	@Override
	public void cooldown() {
		cooldown.startCountdown();
		
	}

}
