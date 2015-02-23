package com.jkjk.GameObjects.Weapons;


public class WeaponFactory {
	public Weapon createWeapon(String newWeaponType){
		if (newWeaponType.equals("Bat"))
			return new Bat();
		else if (newWeaponType.equals("Knife"))
			return new Knife();
		else if (newWeaponType.equals("Shotgun"))
			return new Shotgun();
		else
			return null;
	}
}
