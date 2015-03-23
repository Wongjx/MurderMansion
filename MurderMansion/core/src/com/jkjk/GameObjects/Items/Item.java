package com.jkjk.GameObjects.Items;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.HitBoxExposure;
import com.jkjk.GameWorld.GameWorld;

public abstract class Item {

	protected HitBoxExposure hitBoxExposure;
	protected GameWorld gWorld;
	protected Body body;
	private boolean isDestroy;

	public Item(GameWorld gWorld) {
		hitBoxExposure = new HitBoxExposure(10);
		this.gWorld = gWorld;
	}

	public boolean isHitBoxExposed() {
		return hitBoxExposure.isHitBoxExposed();
	}
	
	public boolean isDestroy(){
		return isDestroy;
	}

	public abstract void use();

	public void update() {
		hitBoxExposure.update();
		if (!hitBoxExposure.isHitBoxExposed()) {
			if (body != null) {
				gWorld.getWorld().destroyBody(body);
				body = null;
				isDestroy = true;
			}
		}

	}
}
