package com.jkjk.GameObjects.Characters;

public class CivilianFactory {
	private int RED = 0;
	private int BLUE = 0;
	private int GREEN = 0;
	
	public Civilian createCivilian(String newCivilianType){
		if (newCivilianType.equals("Red"))
			return new Civilian(RED);
		else if (newCivilianType.equals("Blue"))
			return new Civilian(BLUE);
		else if (newCivilianType.equals("Green"))
			return new Civilian(GREEN);
		else
			return null;
	}
}
