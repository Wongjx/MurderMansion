package com.jkjk.GameObjects.Characters;

<<<<<<< HEAD
=======
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
>>>>>>> 7a9044a096b7b23eb8ae3af3c2530f98358cea16
import com.badlogic.gdx.physics.box2d.World;

public class CivilianFactory {
	private int RED = 0;
	private int BLUE = 1;
	private int GREEN = 2;
	
<<<<<<< HEAD
	public Civilian createCivilian(int newCivilianType, World world){
		if (newCivilianType == 0)
			return new Civilian(RED, world);
		else if (newCivilianType == 1)
			return new Civilian(BLUE, world);
		else if (newCivilianType == 2)
			return new Civilian(GREEN, world);
		else
			return new Civilian(GREEN, world);
=======
	public Civilian createCivilian(int newCivilianType, Body body, World world){
		if (newCivilianType == 0)
			return new Civilian(RED, body, world);
		else if (newCivilianType == 1)
			return new Civilian(BLUE, body, world);
		else if (newCivilianType == 2)
			return new Civilian(GREEN, body, world);
		else
			return new Civilian(GREEN, body, world);
>>>>>>> 7a9044a096b7b23eb8ae3af3c2530f98358cea16
	}
}
