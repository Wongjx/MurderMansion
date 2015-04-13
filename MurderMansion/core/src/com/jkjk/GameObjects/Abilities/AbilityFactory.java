package com.jkjk.GameObjects.Abilities;

import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameWorld;

public class AbilityFactory {
	public Ability createAbility(GameWorld gWorld, GameCharacter gameCharacter){
		System.out.println(gameCharacter.getType());
		if (gameCharacter.getType().equals("Civilian"))
			return new Panic(gameCharacter);
		else if (gameCharacter.getType().equals("Murderer"))
			return new Disguise(gameCharacter);
		else if (gameCharacter.getType().equals("Ghost"))
			return new Haunt(gWorld, gameCharacter);
		else
			return null;
	}
}
