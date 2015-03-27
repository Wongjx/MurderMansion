package com.jkjk.GameObjects.Items;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameWorld.GameWorld;

public abstract class Item {


	protected GameWorld gWorld;
	protected Body body;
	protected boolean isCompleted;

	public Item(GameWorld gWorld) {
		this.gWorld = gWorld;
	}
	
	public boolean isCompleted(){
		return isCompleted;
	}

	public abstract void use();

	public void update() {
	}
}
