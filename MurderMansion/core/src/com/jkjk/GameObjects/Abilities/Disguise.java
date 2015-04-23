package com.jkjk.GameObjects.Abilities;

import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.Murderer;

public class Disguise extends Ability {

	private Duration duration;

	Disguise(GameCharacter gameCharacter) {
		super(gameCharacter);
		cooldown = new Duration(10000); // 10s cooldown
		duration = new Duration(1200);
	}

	@Override
	public void use() {
		duration.startCountdown();
		gameCharacter.setMovable(false);
		if (((Murderer) gameCharacter).isDisguised()) {
			((Murderer) gameCharacter).setDisguise(false);
		} else {
			((Murderer) gameCharacter).setDisguise(true);
		}
	}

	public void update() {
		super.update();
		if (duration.isCountingDown()) {
			duration.update();
			if (!duration.isCountingDown()) {
				gameCharacter.setMovable(true);
			}
		}
	}

	@Override
	public void cooldown() {
		cooldown.startCountdown();
	}

}
