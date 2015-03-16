package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;


public class GameCharacterFactory {
	public GameCharacter createCharacter(String newCharacterType, Body body){
		if (newCharacterType.equals("Murderer"))
			return new Murderer(body);
		else if (newCharacterType.equals("Ghost"))
			return new Ghost(body);
		else
			return null;
	}

	public GameCharacter createCharacter(String newCharacterType, int colour, Body body, World world){
		CivilianFactory civFac = new CivilianFactory();
		if (newCharacterType.equals("Civilian")){
			return civFac.createCivilian(colour, body, world);
		}
		else
			return null;
	}
}
