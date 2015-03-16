package com.jkjk.GameObjects.Characters;

<<<<<<< HEAD
=======
import com.badlogic.gdx.physics.box2d.Body;
>>>>>>> 7a9044a096b7b23eb8ae3af3c2530f98358cea16
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

<<<<<<< HEAD
	public GameCharacter createCharacter(String newCharacterType, int colour, World world){
		CivilianFactory civFac = new CivilianFactory();
		if (newCharacterType.equals("Civilian")){
			return civFac.createCivilian(colour, world);
=======
	public GameCharacter createCharacter(String newCharacterType, int colour, Body body, World world){
		CivilianFactory civFac = new CivilianFactory();
		if (newCharacterType.equals("Civilian")){
			return civFac.createCivilian(colour, body, world);
>>>>>>> 7a9044a096b7b23eb8ae3af3c2530f98358cea16
		}
		else
			return null;
	}
}
