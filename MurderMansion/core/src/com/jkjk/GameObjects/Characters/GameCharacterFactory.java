package com.jkjk.GameObjects.Characters;


public class GameCharacterFactory {
	public GameCharacter createCharacter(String newCharacterType){
		if (newCharacterType.equals("Murderer"))
			return new Murderer();
		else if (newCharacterType.equals("Ghost"))
			return new Ghost();
		else
			return null;
	}

	public GameCharacter createCharacter(String newCharacterType, String colour){
		CivilianFactory civFac = new CivilianFactory();
		if (newCharacterType.equals("Civilian")){
			return civFac.createCivilian(colour);
		}
		else
			return null;
	}
}
