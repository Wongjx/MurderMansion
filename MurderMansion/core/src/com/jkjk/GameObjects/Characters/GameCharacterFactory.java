package com.jkjk.GameObjects.Characters;


public class GameCharacterFactory {
	public GameCharacter createCharacter(String newCharacterType){
		if (newCharacterType.equals("Civilian"))
			return new Civilian();
		else if (newCharacterType.equals("Murderer"))
			return new Murderer();
		else if (newCharacterType.equals("Ghost"))
			return new Ghost();
		else
			return null;
	}
}
