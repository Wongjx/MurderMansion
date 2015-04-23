package com.jkjk.GameObjects.Items;

import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameWorld;

public abstract class Item {

	protected GameWorld gWorld;
	protected boolean isCompleted;
	private Duration executionTime;
	private boolean wasInUse;
	protected boolean isInterrupted;
	private GameCharacter character;

	Item(GameWorld gWorld, GameCharacter character) {
		this.gWorld = gWorld;
		this.character = character;
		executionTime = new Duration(2000);
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void startUse() {
		executionTime.startCountdown();
		character.setMovable(false);
		wasInUse = true;
	}

	public void endUse(){
		character.setMovable(true);
	}

	public void interrupt() {
		isInterrupted = true;
		wasInUse = false;
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
