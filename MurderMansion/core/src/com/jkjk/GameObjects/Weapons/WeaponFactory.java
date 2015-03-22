package com.jkjk.GameObjects.Weapons;

import com.jkjk.GameWorld.GameWorld;


public class WeaponFactory {
	public Weapon createWeapon(String newWeaponType, GameWorld gWorld){
		if (newWeaponType.equals("Bat"))
			return new Bat(gWorld);
		else if (newWeaponType.equals("Knife"))
			return new Knife(gWorld);
		else if (newWeaponType.equals("Shotgun"))
			return new Shotgun(gWorld);
		else
			return null;
	}
}
