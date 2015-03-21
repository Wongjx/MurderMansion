package com.jkjk.GameObjects.Abilities;

import com.jkjk.GameObjects.Characters.GameCharacter;

public class AbilityFactory {
	public Ability createAbility(GameCharacter gameCharacter){
		if (gameCharacter.getType().equals("Civilian"))
			return new Panic(gameCharacter);
		else if (gameCharacter.getType().equals("Murderer"))
			return new Disguise(gameCharacter);
		else
			return null;
	}
}
