package com.jkjk.GameObjects;

import com.jkjk.GameObjects.Weapons.Weapon;

public interface WeaponSlot {

	public void addWeapon(Weapon weapon);

	public void removeWeapon();
	
	public void cooldownWeapon();
	
	public void useWeapon();
}
