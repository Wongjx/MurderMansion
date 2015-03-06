package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.physics.box2d.Body;


public class GameCharacterFactory {
	public GameCharacter createCharacter(String newCharacterType, Body body){
		if (newCharacterType.equals("Murderer"))
			return new Murderer(body);
		else if (newCharacterType.equals("Ghost"))
			return new Ghost(body);
		else
			return null;
	}

	public GameCharacter createCharacter(String newCharacterType, int colour, Body body){
		CivilianFactory civFac = new CivilianFactory();
		if (newCharacterType.equals("Civilian")){
			return civFac.createCivilian(colour, body);
		}
		else
			return null;
	}
}
