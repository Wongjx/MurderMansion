package com.jkjk.GameObjects.Weapons;

public class WeaponCooldown implements Runnable {
	
	private Weapon weapon;
	
	public WeaponCooldown(Weapon weapon) {
		this.weapon = weapon;
	}
	
	@Override
	public void run() {
		try {
			weapon.setWeaponOnCooldown(true);
			System.out.println("Weapon cooling down");
			Thread.sleep(5000);
			System.out.println("Weapon cooled down");
			weapon.setWeaponOnCooldown(false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
