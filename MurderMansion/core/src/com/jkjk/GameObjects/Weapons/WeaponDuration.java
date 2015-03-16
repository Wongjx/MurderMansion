package com.jkjk.GameObjects.Weapons;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class WeaponDuration implements Runnable {

	private Weapon weapon;
	private World world;
	private Body body;
	
	public WeaponDuration(Weapon weapon, World world, Body body) {
		this.weapon = weapon;
		this.world = world;
		this.body = body;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(10);
			world.destroyBody(body);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
