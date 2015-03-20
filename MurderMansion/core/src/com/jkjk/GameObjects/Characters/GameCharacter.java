package com.jkjk.GameObjects.Characters;

import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

/**
 * Handles all things related to characters
 * 
 */
public abstract class GameCharacter {

	private String name;

	protected boolean alive;
	private boolean itemChange, weaponChange;
	private boolean stun;

	private float maxVelocity;
	private float touchpadX;
	private float touchpadY;
	private double angleDiff;

	private Weapon weapon;
	private Item item;
	protected Body body;
	protected RayHandler rayHandler;
	
	private Touchpad touchpad;

	private int colour;

	public GameCharacter() {
		maxVelocity = 64;
		touchpad = AssetLoader.touchpad;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void spawn(float x, float y, float angle) {
		alive = true;
		body.setTransform(x, y, angle); // Spawn position
	}

	public void die() {
		alive = false;
	}

	public boolean isAlive() {
		return alive;
	}

	public void stun(boolean stun) {
		this.stun = stun;
	}

	public boolean isStun() {
		return stun;
	}

	public int getColour() {
		return colour;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public Body getBody() {
		return body;
	}

	public void addWeapon(Weapon weapon) {
		this.weapon = weapon;
		weaponChange = true;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void useWeapon() {
		if (!weapon.isOnCooldown()) {
			weapon.use();
			weapon.cooldown();
		}
	}

	public boolean getWeaponChange() {
		return weaponChange;
	}

	public void setWeaponChange(boolean weaponChange) {
		this.weaponChange = weaponChange;
	}

	public void addItem(Item item) {
		this.item = item;
		itemChange = true;
	}

	public Item getItem() {
		return item;
	}

	public void useItem() {
		item.use();
	}

	public boolean getItemChange() {
		return itemChange;
	}

	public void setItemChange(boolean itemChange) {
		this.itemChange = itemChange;
	}

	public void update() {
		if (weapon != null)
			weapon.update();
		if (item != null) {
			item.update();
			if (item.isDestroy()) {
				item = null;
				itemChange = true;
			}
		}
	}

	public void render(OrthographicCamera cam) {
		touchpadX = touchpad.getKnobPercentX();
		touchpadY = touchpad.getKnobPercentY();
		if (!touchpad.isTouched()) {
			body.setAngularVelocity(0);
		} else {
			angleDiff = (Math.atan2(touchpadY, touchpadX) - (body.getAngle())) % (Math.PI * 2);
			if (angleDiff > 0) {
				if (angleDiff >= 3.14) {
					if (angleDiff > 6.2)
						body.setAngularVelocity((float) -angleDiff / 7);
					else
						body.setAngularVelocity(-5);
				} else if (angleDiff < 0.4)
					body.setAngularVelocity((float) angleDiff * 3);
				else
					body.setAngularVelocity(5);
			} else if (angleDiff < 0) {
				if (angleDiff <= -3.14) {
					if (angleDiff < -6.2)
						body.setAngularVelocity((float) -angleDiff / 7);
					else
						body.setAngularVelocity(5);
				} else if (angleDiff > -0.4)
					body.setAngularVelocity((float) angleDiff * 3);
				else
					body.setAngularVelocity(-5);
			} else
				body.setAngularVelocity(0);
		}

		body.setLinearVelocity(touchpadX * maxVelocity, touchpadY * maxVelocity);

		cam.position.set(body.getPosition(), 0); // Set cam position to be on player

		rayHandler.setCombinedMatrix(cam.combined);
		rayHandler.updateAndRender();
	}

	public void dispose() {
		rayHandler.dispose();
	}

}
