package com.jkjk.GameObjects.Characters;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
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
	protected boolean isPlayer;
	private GameWorld gWorld;

	private boolean alive;
	protected boolean itemChange, weaponChange, abilityChange;
	private boolean stun;
	private boolean haunt;
	private Duration stunDuration;
	private Duration hauntDuration;

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

	protected int id;
	private int weaponUses;
	private Random random;
	private long hauntTime;
	private long nextRandomMovement;

	protected float runTime;
	protected float ambientLightValue;
	private int nextBrightTime;
	private long startTime;
	private long brightTime;

	private ConcurrentLinkedQueue<float[]> positionQueue;
	private ConcurrentLinkedQueue<Float> angleQueue;
	private ConcurrentLinkedQueue<float[]> velocityQueue;

	private float[] nextPosition;
	private Float nextAngle;
	private float[] nextVelocity;

	GameCharacter(String type, int id, GameWorld gWorld, boolean isPlayer) {
		this.isPlayer = isPlayer;
		maxVelocity = 56;
		weaponUses = 3;
		if (type == "Murderer") {
			weaponUses = 2;
		}
		touchpad = AssetLoader.touchpad;
		stunDuration = new Duration(3000);
		hauntDuration = new Duration(4000);

		this.type = type;
		this.id = id;
		this.gWorld = gWorld;
		AbilityFactory af = new AbilityFactory();
		ability = af.createAbility(gWorld, this);

		this.deathPositionX = 0;
		this.deathPositionY = 0;

		if (isPlayer || type == "Ghost") {
			rayHandler = gWorld.getRayHandler();
		} else {
			rayHandler = new RayHandler(gWorld.getWorld());
		}
		ambientLightValue = 0.05f;
		rayHandler.setAmbientLight(ambientLightValue);
		runTime = 0;
		startTime = System.currentTimeMillis();
		brightTime = 0;
		nextBrightTime = 10000;
		random = new Random();
		hauntTime = 0;
		nextRandomMovement = 0;

		positionQueue = new ConcurrentLinkedQueue<float[]>();
		angleQueue = new ConcurrentLinkedQueue<Float>();
		velocityQueue = new ConcurrentLinkedQueue<float[]>();

		nextPosition = new float[2];
		nextAngle = new Float(0);
		nextVelocity = new float[2];

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
		gWorld.getTM().setDisplayMessage("You Hear A Scream... Then Silence");
		AssetLoader.characterDeathSound.play(AssetLoader.VOLUME);
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

	public void stun() {
		stun = true;
		stunDuration.startCountdown();
	}

	public boolean isStun() {
		return stun;
	}

	public void haunt(boolean haunt) {
		this.haunt = haunt;
		hauntDuration.startCountdown();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isPlayer() {
		return isPlayer;
	}

	public Body getBody() {
		return body;
	}

	public Ability getAbility() {
		return ability;
	}

	public boolean useAbility() {
		if (!ability.isOnCoolDown()) {
			ability.use();
			ability.cooldown();
			return true;
		}
		return false;
	}

	public void addWeapon(Weapon weapon) {
		this.weapon = weapon;
		weaponChange = true;
		weaponUses = 3;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public boolean useWeapon() {
		if (!weapon.isOnCooldown()) {
			weapon.use();
			weaponUses--;
			if (weaponUses > 0) {
				weapon.cooldown();
			}
			return true;
		} else {
			if (isPlayer)
				gWorld.getTM().setDisplayMessage("Your hands tire since your last use. Perhaps later?");
		}
		return false;
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
		if (isPlayer)
			if (!item.inUse())
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

	public boolean lightContains(float x, float y) {
		return rayHandler.pointAtLight(x, y);
	}

	public void update() {

		if (!isPlayer) {
			nextPosition = positionQueue.poll();
			nextAngle = angleQueue.poll();
			nextVelocity = velocityQueue.poll();

			if (nextPosition != null && nextAngle != null && nextVelocity != null) {
				body.setTransform(nextPosition[0], nextPosition[1], nextAngle);
				body.setLinearVelocity(nextVelocity[0] / 2, nextVelocity[1] / 2);
			}
		}

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
		if (stun) {
			stunDuration.update();
			if (!stunDuration.isCountingDown())
				stun = false;
		}
		if (haunt) {
			hauntDuration.update();
			if (!hauntDuration.isCountingDown())
				haunt = false;
		}
	}

	public void render(OrthographicCamera cam, SpriteBatch batch) {
		if (isPlayer) {

			brightTime = System.currentTimeMillis() - startTime;

			if (brightTime > nextBrightTime) {
				System.out.println("BRIGHTER!");
				ambientLightValue += 0.009;
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
		if (stun) {
			return false;
		}
		if (item != null)
			if (item.inUse())
				return false;
		return true;
	}

	protected void playerMovement() {
		touchpadX = touchpad.getKnobPercentX();
		touchpadY = touchpad.getKnobPercentY();
		if (haunt) {
			hauntTime = System.currentTimeMillis() - startTime;
			if (hauntTime > nextRandomMovement) {
				if (random.nextBoolean())
					body.setAngularVelocity(random.nextFloat() * 3);
				else
					body.setAngularVelocity(-random.nextFloat() * 3);
				nextRandomMovement += (random.nextInt(1000) + 500);
			}
			body.setLinearVelocity(maxVelocity * (float) Math.cos(body.getAngle()), maxVelocity
					* (float) Math.sin(body.getAngle()));
		} else {
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
	}

	public void setPosition(float x, float y, float angle, float velocityX, float velocityY) {
		for (int i = 0; i < positionQueue.size(); i++) {
			positionQueue.poll();
			angleQueue.poll();
			velocityQueue.poll();
		}
		positionQueue.offer(new float[] { x, y });
		angleQueue.offer(angle);
		velocityQueue.offer(new float[] { velocityX, velocityY });
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
