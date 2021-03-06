package com.jkjk.GameObjects.Weapons;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameWorld;

public abstract class Weapon {

	private Duration cooldown;
	protected Duration hitBoxExposure;
	protected GameWorld gWorld;
	protected Body body;
	protected String name;
	protected float runTime;
	private boolean isCompleted;
	private GameCharacter character;

	Weapon(GameWorld gWorld, GameCharacter character) {
		cooldown = new Duration(5000);
		this.character = character;
		hitBoxExposure = new Duration(300);
		this.gWorld = gWorld;
		runTime = 0;
		isCompleted = false;
	}

	public String getName() {
		return name;
	}

	public boolean isOnCooldown() {
		return cooldown.isCountingDown();
	}

	public void use() {
		isCompleted = false;
		character.setMovable(false);
	}

	public void cooldown() {
		cooldown.startCountdown();
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void update() {
		cooldown.update();
		hitBoxExposure.update();
		if (!hitBoxExposure.isCountingDown()) {
			if (body.isActive()) {
				body.setActive(false);
				body.setTransform(0, 0, 0);
				isCompleted = true;
				character.setMovable(true);
			}
		}

	}

	public void render() {

	}

}
