package com.jkjk.GameObjects.Items;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameWorld.GameWorld;

public abstract class Item {

	protected GameWorld gWorld;
	protected Body body;
	protected boolean isCompleted;
	private Duration executionTime;
	private boolean wasInUse;
	protected boolean isInterrupted;

	public Item(GameWorld gWorld) {
		this.gWorld = gWorld;
		executionTime = new Duration(2000);
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void startUse() {
		executionTime.startCountdown();
		wasInUse = true;
	}

	public abstract void endUse();

	public void interrupt() {
		isInterrupted = true;
		wasInUse = false;
	}
	
	public void foundTrap(){
		
	}

	public boolean inUse() {
		return executionTime.isCountingDown() && !isInterrupted;
	}

	public void update() {
		if (!isInterrupted) {
			executionTime.update();
			if (wasInUse && !inUse()) {
				endUse();
				wasInUse = false;
			}
		}
	}
}
