package com.jkjk.GameObjects.Characters;

import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.jkjk.GameObjects.Abilities.Ability;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.MMHelpers.AssetLoader;

/**
 * Handles all things related to characters
 * 
 */
public abstract class GameCharacter {

	private String type;

	protected boolean alive;
	private boolean itemChange, weaponChange;
	private boolean stun;

	private float maxVelocity;
	private float touchpadX;
	private float touchpadY;
	private double angleDiff;

	private Weapon weapon;
	private Item item;
	private Ability ability;
	protected Body body;
	protected RayHandler rayHandler;

	private Touchpad touchpad;

	private int colour;
	private SpriteBatch batch;
	private Animation charAnim;
	private float runTime;
	
	public GameCharacter() {
		maxVelocity = 64;
		touchpad = AssetLoader.touchpad;
		batch = new SpriteBatch();
		charAnim = AssetLoader.civAnimation;
		runTime = 0;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public float getVelocity() {
		return maxVelocity;
	}

	public void setVelocity(float velocity) {
		maxVelocity = velocity;
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

	public void addAbility(Ability ability) {
		this.ability = ability;
	}

	public Ability getAbility() {
		return ability;
	}

	public void useAbility() {
		if (!ability.isOnCoolDown()) {
			ability.use();
			ability.cooldown();
		}
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
		if (ability != null) {
			ability.update();
		}
	}

	public void render(OrthographicCamera cam) {
		
		if (!stun) {
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
		}

		cam.position.set(body.getPosition(), 0); // Set cam position to be on player

		rayHandler.setCombinedMatrix(cam.combined);
		rayHandler.updateAndRender();
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		runTime +=Gdx.graphics.getRawDeltaTime();
		batch.draw(charAnim.getKeyFrame(runTime,true), body.getPosition().x-10, body.getPosition().y-10, 10, 10, 20, 20, 1, 1,(float) (body.getAngle()*180/Math.PI)-90);
		Gdx.app.error("getRaw = ", "      "+runTime);
		batch.end();
		
	}

	public void dispose() {
		rayHandler.dispose();
	}

}
