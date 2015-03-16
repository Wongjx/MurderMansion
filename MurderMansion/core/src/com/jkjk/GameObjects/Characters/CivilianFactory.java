package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.physics.box2d.World;

public class CivilianFactory {
	private int RED = 0;
	private int BLUE = 1;
	private int GREEN = 2;
	
	public Civilian createCivilian(int newCivilianType, World world){
		if (newCivilianType == 0)
			return new Civilian(RED, world);
		else if (newCivilianType == 1)
			return new Civilian(BLUE, world);
		else if (newCivilianType == 2)
			return new Civilian(GREEN, world);
		else
			return new Civilian(GREEN, world);
	}
}
