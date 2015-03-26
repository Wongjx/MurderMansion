package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.physics.box2d.World;

public class GameCharacterFactory {

	public GameCharacter createCharacter(String newCharacterType, int id, World world) {
		if (newCharacterType.equals("Civilian")) {
			return new Civilian(id, world);
		} else if (newCharacterType.equals("Murderer"))
			return new Murderer(id, world);
		else if (newCharacterType.equals("Ghost"))
			return new Ghost(id, world);
		else
			return null;
	}
}
