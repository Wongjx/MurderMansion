package com.jkjk.GameObjects.Weapons;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.HitBoxExposure;
import com.jkjk.GameWorld.GameWorld;

public abstract class Weapon {

	private Duration cooldown;
	protected HitBoxExposure hitBoxExposure;
	protected GameWorld gWorld;
	protected Body body;

	public Weapon(GameWorld gWorld) {
		cooldown = new Duration(5000);
		hitBoxExposure = new HitBoxExposure(10);
		this.gWorld = gWorld;
	}

	public boolean isOnCooldown() {
		return cooldown.isOnCooldown();
	}

	public abstract void use();

	public abstract void postUse(GameWorld gWorld);

	public void cooldown() {
		cooldown.startCooldown();
	}

	public void update() {
		cooldown.update();
		hitBoxExposure.update();
		if (!hitBoxExposure.isHitBoxExposed()) {
			if (body != null){
				gWorld.getWorld().destroyBody(body);
				body = null;
			}
		}

	}

}
