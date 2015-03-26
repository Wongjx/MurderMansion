package com.jkjk.GameObjects.Characters;

import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Abilities.Ability;
import com.jkjk.GameObjects.Abilities.AbilityFactory;
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
	protected boolean itemChange, weaponChange, abilityChange;
	private boolean stun;
	private Duration stunDuration;
	protected boolean disguised;	// true for civilian, false for murderer

	private float maxVelocity;
	private float touchpadX;
	private float touchpadY;
	private double angleDiff;

	private Weapon weapon;
	private Item item;
	protected Ability ability;
	protected Body body;
	protected RayHandler rayHandler;

	private Touchpad touchpad;
	
	private float deathPositionX;
	private float deathPositionY;

	private int id;
	
	public GameCharacter(String type, int id) {
		maxVelocity = 64;
		touchpad = AssetLoader.touchpad;
		stunDuration = new Duration(5000);
		
		this.type = type;
		this.id = id;
		AbilityFactory af = new AbilityFactory();
		ability = af.createAbility(this);
		
		this.deathPositionX = 0;
		this.deathPositionY = 0;
		
	}
	
	public float get_deathPositionX(){
		return deathPositionX;
	}
	public float get_deathPositionY(){
		return deathPositionY;
	}
	public void set_deathPositionX(float k){
		deathPositionX = k;
	}
	public void set_deathPositionY(float k){
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
	
	public boolean isDisguised(){
		return disguised;
	}
	
	public void setDisguise(boolean disguised){
		this.disguised = disguised;
	}
	
	public boolean getAbilityChange(){
		return abilityChange;
	}
	
	public void setAbilityChange(boolean abilityChange){
		this.abilityChange = abilityChange;
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
		if (stun)
			stunDuration.update();
	}

	public void render(OrthographicCamera cam) {
		
		if (!stun) {
			playerMovement();
		} else {
			body.setAngularVelocity(0);
			body.setLinearVelocity(0,0);
		}

		cam.position.set(body.getPosition(), 0); // Set cam position to be on player

		rayHandler.setCombinedMatrix(cam.combined);
		rayHandler.updateAndRender();
		
	}
	
	private void playerMovement(){
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

	public void dispose() {
		rayHandler.dispose();
	}

}
