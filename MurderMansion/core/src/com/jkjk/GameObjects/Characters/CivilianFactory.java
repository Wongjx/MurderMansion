package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.physics.box2d.Body;

public class CivilianFactory {
	private int RED = 0;
	private int BLUE = 1;
	private int GREEN = 2;
	
	public Civilian createCivilian(String newCivilianType, Body body){
		if (newCivilianType.equals("Red"))
			return new Civilian(RED, body);
		else if (newCivilianType.equals("Blue"))
			return new Civilian(BLUE, body);
		else if (newCivilianType.equals("Green"))
			return new Civilian(GREEN, body);
		else
			return null;
	}
}
