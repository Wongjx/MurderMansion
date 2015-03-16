package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.physics.box2d.World;


public class GameCharacterFactory {
	public GameCharacter createCharacter(String newCharacterType, World world){
		if (newCharacterType.equals("Murderer"))
			return new Murderer(world);
		else if (newCharacterType.equals("Ghost"))
			return new Ghost(world);
		else
			return null;
	}

	public GameCharacter createCharacter(String newCharacterType, int colour, World world){
		CivilianFactory civFac = new CivilianFactory();
		if (newCharacterType.equals("Civilian")){
			return civFac.createCivilian(colour, world);
		}
		else
			return null;
	}
}
