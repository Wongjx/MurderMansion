package com.jkjk.GameObjects.Weapons;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameWorld.GameWorld;

public abstract class Weapon {

	private Duration cooldown;
	protected Duration hitBoxExposure;
	protected GameWorld gWorld;
	protected Body body;
	protected String name;
	protected float runTime;

	public Weapon(GameWorld gWorld) {
		cooldown = new Duration(5000);
		hitBoxExposure = new Duration(10);
		this.gWorld = gWorld;
		runTime = 0;
	}
	
	public String getName(){
		return name;
	}

	public boolean isOnCooldown() {
		return cooldown.isCountingDown();
	}

	public abstract void use();

	public void cooldown() {
		cooldown.startCountdown();
	}

	public void update() {
		cooldown.update();
		hitBoxExposure.update();
		if (!hitBoxExposure.isCountingDown()) {
			if (body != null){
				gWorld.getWorld().destroyBody(body);
				body = null;
			}
		}

	}
	public void render(){
		
	}

}
