package com.jkjk.GameObjects.Items;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameWorld.GameWorld;

public abstract class Item {

	protected Duration hitBoxExposure;
	protected GameWorld gWorld;
	protected Body body;
	private boolean isDestroy;

	public Item(GameWorld gWorld) {
		hitBoxExposure = new Duration(10);
		this.gWorld = gWorld;
	}

	public boolean isHitBoxExposed() {
		return hitBoxExposure.isCountingDown();
	}
	
	public boolean isDestroy(){
		return isDestroy;
	}

	public abstract void use();

	public void update() {
		hitBoxExposure.update();
		if (!hitBoxExposure.isCountingDown()) {
			if (body != null) {
				gWorld.getWorld().destroyBody(body);
				body = null;
				isDestroy = true;
			}
		}

	}
}
