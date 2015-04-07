package com.jkjk.GameObjects.Characters;

import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Abilities.Ability;
import com.jkjk.GameObjects.Abilities.AbilityFactory;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

/**
 * Handles all things related to characters
 * 
 */
public abstract class GameCharacter {

	private String type;
	private boolean isPlayer;

	protected boolean alive;
	protected boolean itemChange, weaponChange, abilityChange;
	protected boolean stun;
	private Duration stunDuration;

	private float maxVelocity;
	private float touchpadX;
	private float touchpadY;
	private double angleDiff;

	protected Weapon weapon;
	protected Item item;
	protected Ability ability;
	protected Body body;
	protected RayHandler rayHandler;

	private Touchpad touchpad;

	private float deathPositionX;
	private float deathPositionY;

	private int id;
	private int weaponUses;
	
	protected float runTime;
	protected float ambientLightValue;
	private int nextBrightTime;

	GameCharacter(String type, int id, GameWorld gWorld, boolean isPlayer) {
		this.isPlayer = isPlayer;
		maxVelocity = 64;
		weaponUses = 3;
		touchpad = AssetLoader.touchpad;
		stunDuration = new Duration(5000);

		this.type = type;
		this.id = id;
		AbilityFactory af = new AbilityFactory();
		ability = af.createAbility(this);

		this.deathPositionX = 0;
		this.deathPositionY = 0;

		rayHandler = new RayHandler(gWorld.getWorld());
		ambientLightValue = 0.05f;
		rayHandler.setAmbientLight(ambientLightValue);
		runTime = 0;
		nextBrightTime = 10000;

	}

	public float get_deathPositionX() {
		return deathPositionX;
	}

	public float get_deathPositionY() {
		return deathPositionY;
	}

	public void set_deathPositionX(float k) {
		deathPositionX = k;
	}

	public void set_deathPositionY(float k) {
		deathPositionY = k;
	}

	public String getType() {
		return type;
	}

	public void spawn(float x, float y, float angle) {
		alive = true;
		body.setTransform(x, y, angle); // Spawn position
		abilityChange = true;
		addWeapon(null);
		addItem(null);
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
		if(type.equals("Murderer")){
			body.setUserData(AssetLoader.civStunAnimation);
			//body.setUserData(AssetLoader.murStunAnimation);
		}
		else{
			body.setUserData(AssetLoader.civStunAnimation);
		}
		stunDuration.startCountdown();
	}

	public boolean isStun() {
		return stun;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isPlayer(){
		return isPlayer;
	}

	public Body getBody() {
		return body;
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
		weaponUses = 3;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void useWeapon() {
		if (!weapon.isOnCooldown()) {
			weapon.use();
			weaponUses--;
			if (weaponUses > 0) {
				weapon.cooldown();
			}
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
		item.startUse();
	}

	public boolean getItemChange() {
		return itemChange;
	}

	public void setItemChange(boolean itemChange) {
		this.itemChange = itemChange;
	}

	public boolean getAbilityChange() {
		return abilityChange;
	}

	public void setAbilityChange(boolean abilityChange) {
		this.abilityChange = abilityChange;
	}

	public abstract boolean lightContains(float x, float y);

	public void update() {
		if (isPlayer) {
			if (weapon != null) {
				weapon.update();
				if (weapon.isCompleted() && weaponUses == 0) {
					weapon = null;
					weaponChange = true;
				}
			}
			if (item != null) {
				item.update();
				if (!item.inUse() && item.isCompleted()) {
					item = null;
					itemChange = true;
				}
			}
			if (ability != null) {
				ability.update();
			}
			if (stun)
				stunDuration.update();
		}
	}

	public void render(OrthographicCamera cam, SpriteBatch batch) {
		if (isPlayer) {
			runTime += Gdx.graphics.getRawDeltaTime();

			if (runTime > nextBrightTime) {
				ambientLightValue += 0.008;
				rayHandler.setAmbientLight(ambientLightValue);
				nextBrightTime += 10000;
			}

			if (checkMovable()) {
				playerMovement();
			} else {
				body.setAngularVelocity(0);
				body.setLinearVelocity(0, 0);
			}

			cam.position.set(body.getPosition(), 0); // Set cam position to be on player
			
			rayHandler.setCombinedMatrix(cam.combined);
			rayHandler.updateAndRender();
		}

	}

	protected boolean checkMovable() {
		if (stun)
			return false;
		if (item != null)
			if (item.inUse())
				return false;
		return true;
	}

	protected void playerMovement() {
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

	public void setPosition(float x, float y, float angle) {
		body.setTransform(x, y, angle);
	}

	public float getAmbientLightValue() {
		return ambientLightValue;
	}

	public void setAmbientLightValue(float ambientLightValue) {
		this.ambientLightValue = ambientLightValue;
		rayHandler.setAmbientLight(ambientLightValue);
	}

	public void dispose() {
		rayHandler.dispose();
	}

}
