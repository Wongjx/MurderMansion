package com.jkjk.GameObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class HitBoxExposure implements Runnable {

	private World world;
	private Body body;
	
	public HitBoxExposure(World world, Body body) {
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
