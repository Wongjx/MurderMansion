package com.jkjk.GameObjects.Characters;

import java.util.Random;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Abilities.Ability;
import com.jkjk.GameObjects.Abilities.AbilityFactory;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.Input.PlayerInputController;
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
	private boolean movable;
	private boolean stun;
	private boolean haunt;
	private Duration stunDuration;
	private Duration hauntDuration;

	private float maxVelocity;
	private double angleDiff;

	protected Weapon weapon;
	protected Item item;
	protected Ability ability;
	protected Body body;
	protected RayHandler rayHandler;

	private float deathPositionX;
	private float deathPositionY;

	protected int id;
	private int weaponUses;
	private Random random;
	private long hauntTime;
	private long nextRandomMovement;

	protected float runTime;
	private float ambientLightValue;
	private int nextBrightTime;
	private long startTime;
	private long brightTime;
	private final Vector2 movementVector;
	private final Vector2 targetNetworkPosition;
	private final Vector2 targetNetworkVelocity;
	private boolean hasNetworkTarget;
	private boolean remoteMoving;
	private static final float NETWORK_SMOOTHING_SPEED = 12f;
	private static final float NETWORK_SNAP_DISTANCE = 24f;
	private static final float NETWORK_POSITION_DEADBAND = 1.5f;
	private static final float NETWORK_ANGLE_DEADBAND = 0.04f;
	private PlayerInputController inputController;
	private float targetNetworkAngle;

	GameCharacter(String type, int id, GameWorld gWorld, boolean isPlayer) {
		this.isPlayer = isPlayer;
		maxVelocity = 56;
		if ("Murderer".equals(type)) {
			weaponUses = 2;
		} else {
			weaponUses = 3;
		}
		stunDuration = new Duration(3000);
		hauntDuration = new Duration(4000);

		this.type = type;
		this.id = id;
		this.gWorld = gWorld;
		AbilityFactory af = new AbilityFactory();
		ability = af.createAbility(gWorld, this);

		this.deathPositionX = 0;
		this.deathPositionY = 0;

		if (isPlayer || "Ghost".equals(type)) {
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

		movementVector = new Vector2();
		targetNetworkPosition = new Vector2();
		targetNetworkVelocity = new Vector2();
		hasNetworkTarget = false;
		remoteMoving = false;
		targetNetworkAngle = 0f;

		movable = true;

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
		movable = false;
		stun = true;
		stunDuration.startCountdown();
	}

	public boolean isStun() {
		return stun;
	}

	public boolean isMovable() {
		return movable;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
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

	public boolean isVisuallyMoving() {
		if (isPlayer) {
			return body != null && !body.getLinearVelocity().isZero();
		}
		return remoteMoving;
	}

	public Body getBody() {
		return body;
	}

	public Ability getAbility() {
		return ability;
	}

	public boolean useAbility() {
		alignToAimTargetIfAvailable();
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
		if ("Murderer".equals(type)) {
			weaponUses = 2;
		} else {
			weaponUses = 3;
		}

	}

	public Weapon getWeapon() {
		return weapon;
	}

	public boolean useWeapon() {
		alignToAimTargetIfAvailable();
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
		alignToAimTargetIfAvailable();
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
			applyRemoteNetworkTarget();
		}

		if (weapon != null) {
			weapon.update();
			if (weapon.isCompleted() && weaponUses == 0) {
				weapon = null;
				weaponChange = true;
				if (isPlayer){
					gWorld.getTM().setDisplayMessage("Your weapon broke after too much use");
				}
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
			if (!stunDuration.isCountingDown()) {
				movable = true;
				stun = false;
			}
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

			if (movable) {
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

	protected void playerMovement() {
		if (inputController == null) {
			body.setAngularVelocity(0);
			body.setLinearVelocity(0, 0);
			return;
		}
		movementVector.set(inputController.getMovementVector());
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
			if (!inputController.isMovementActive() && !inputController.hasAimTarget()) {
				body.setAngularVelocity(0);
			} else {
				float targetAngle;
				if (inputController.hasAimTarget()) {
					Vector2 aimTarget = inputController.getAimTargetWorld();
					targetAngle = (float) Math.atan2(aimTarget.y - body.getPosition().y,
							aimTarget.x - body.getPosition().x);
				} else {
					targetAngle = (float) Math.atan2(movementVector.y, movementVector.x);
				}
				angleDiff = (targetAngle - (body.getAngle())) % (Math.PI * 2);
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
			body.setLinearVelocity(movementVector.x * maxVelocity, movementVector.y * maxVelocity);
		}
	}

	public void setInputController(PlayerInputController inputController) {
		this.inputController = inputController;
	}

	private void alignToAimTargetIfAvailable() {
		if (inputController == null || !inputController.hasAimTarget() || body == null) {
			return;
		}
		Vector2 aimTarget = inputController.getAimTargetWorld();
		if (aimTarget == null) {
			return;
		}
		float targetAngle = (float) Math.atan2(aimTarget.y - body.getPosition().y,
				aimTarget.x - body.getPosition().x);
		body.setTransform(body.getPosition(), targetAngle);
		body.setAngularVelocity(0f);
	}

	public void setPosition(float x, float y, float angle, float velocityX, float velocityY) {
		targetNetworkPosition.set(x, y);
		targetNetworkAngle = angle;
		targetNetworkVelocity.set(velocityX, velocityY);
		hasNetworkTarget = true;
	}

	private void applyRemoteNetworkTarget() {
		if (!hasNetworkTarget || body == null) {
			return;
		}
		Vector2 currentPosition = body.getPosition();
		float distanceToTarget = currentPosition.dst(targetNetworkPosition);
		float angleDelta = smallestAngleDelta(body.getAngle(), targetNetworkAngle);
		if (distanceToTarget >= NETWORK_SNAP_DISTANCE) {
			remoteMoving = true;
			body.setTransform(targetNetworkPosition.x, targetNetworkPosition.y, targetNetworkAngle);
			body.setLinearVelocity(0f, 0f);
			return;
		}
		if (distanceToTarget <= NETWORK_POSITION_DEADBAND && Math.abs(angleDelta) <= NETWORK_ANGLE_DEADBAND) {
			remoteMoving = false;
			body.setLinearVelocity(0f, 0f);
			return;
		}

		remoteMoving = true;
		float alpha = Math.min(1f, Gdx.graphics.getDeltaTime() * NETWORK_SMOOTHING_SPEED);
		float smoothedX = currentPosition.x + (targetNetworkPosition.x - currentPosition.x) * alpha;
		float smoothedY = currentPosition.y + (targetNetworkPosition.y - currentPosition.y) * alpha;
		float smoothedAngle = lerpAngle(body.getAngle(), targetNetworkAngle, alpha);
		body.setTransform(smoothedX, smoothedY, smoothedAngle);
		body.setLinearVelocity(0f, 0f);
	}

	private float lerpAngle(float currentAngle, float targetAngle, float alpha) {
		float delta = smallestAngleDelta(currentAngle, targetAngle);
		return currentAngle + delta * alpha;
	}

	private float smallestAngleDelta(float currentAngle, float targetAngle) {
		float delta = targetAngle - currentAngle;
		while (delta > Math.PI) {
			delta -= Math.PI * 2f;
		}
		while (delta < -Math.PI) {
			delta += Math.PI * 2f;
		}
		return delta;
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
