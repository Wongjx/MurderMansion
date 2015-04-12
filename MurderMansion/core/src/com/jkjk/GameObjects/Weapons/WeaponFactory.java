package com.jkjk.GameObjects.Weapons;

import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameWorld;


public class WeaponFactory {
	public Weapon createWeapon(String newWeaponType, GameWorld gWorld, GameCharacter character){
		if (newWeaponType.equals("Bat"))
			return new Bat(gWorld, character);
		else if (newWeaponType.equals("Knife"))
			return new Knife(gWorld, character);
		else if (newWeaponType.equals("Shotgun"))
			return new Shotgun(gWorld, character);
		else
			return null;
	}
}
