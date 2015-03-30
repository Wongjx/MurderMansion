package com.jkjk.GameObjects.Characters;

import com.jkjk.GameWorld.GameWorld;

public class GameCharacterFactory {

	public GameCharacter createCharacter(String newCharacterType, int id, GameWorld gWorld, boolean isPlayer) {
		if (newCharacterType.equals("Civilian")) {
			return new Civilian(id, gWorld, isPlayer);
		} else if (newCharacterType.equals("Murderer"))
			return new Murderer(id, gWorld, isPlayer);
		else if (newCharacterType.equals("Ghost"))
			return new Ghost(id, gWorld, isPlayer);
		else
			return null;
	}
}
