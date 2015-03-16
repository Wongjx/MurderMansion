package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class CivilianFactory {
	private int RED = 0;
	private int BLUE = 1;
	private int GREEN = 2;
	
	public Civilian createCivilian(int newCivilianType, Body body, World world){
		if (newCivilianType == 0)
			return new Civilian(RED, body, world);
		else if (newCivilianType == 1)
			return new Civilian(BLUE, body, world);
		else if (newCivilianType == 2)
			return new Civilian(GREEN, body, world);
		else
			return new Civilian(GREEN, body, world);
	}
}
