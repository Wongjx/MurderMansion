package com.jkjk.GameObjects.Characters;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jkjk.GameObjects.Abilities.Panic;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

public class Civilian extends GameCharacter {

	private PointLight pointLight;
	private ConeLight coneLight;
	private Animation currentAnimation;
	private GameWorld gWorld;
	private float animationRunTime;

	// ANIMATIONS
	private TextureRegion civRest;
	private TextureRegion civPanicRest;
	private Animation civWalkAnimation;
	private Animation civPanicAnimation;
	private Animation civStunAnimation;
	private Animation civBatAnimation;
	private Animation civShotgunAnimation;
	private Animation civDisarmAnimation;

	private volatile boolean seen;

	private Music walkSound;
	private Music runSound;

	// private Animation civDropDisarmAnimation; //not yet implemented

	Civilian(int id, GameWorld gWorld, boolean isPlayer) {

		super("Civilian", id, gWorld, isPlayer);
		this.gWorld = gWorld;

		// create body of civilian
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		body = gWorld.getWorld().createBody(bdef);

		// Circular body fixture
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		fdef.shape = shape;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("civilian");

		// Create Light for player
		coneLight = new ConeLight(rayHandler, 100, null, 150, 0, 0, 0, 40);
		coneLight.attachToBody(body, 0, 0);
		Light.setContactFilter((short) 2, (short) 2, (short) 1);
		pointLight = new PointLight(rayHandler, 100, null, 30, 0, 0);
		pointLight.attachToBody(body);

		// INITIATE ANIMATIONS
		animationRunTime = 0;
		switch (id % AssetLoader.NUM_CIVILIAN_TEXTURES) {
		case 0:
			civWalkAnimation = AssetLoader.civAnimation0;
			civPanicAnimation = AssetLoader.civPanicAnimation0;
			civStunAnimation = AssetLoader.civStunAnimation0;
			civBatAnimation = AssetLoader.civBatAnimation0;
			civShotgunAnimation = AssetLoader.civShotgunAnimation0;
			civDisarmAnimation = AssetLoader.civDisarmAnimation0;
			civRest = AssetLoader.civ_rest0;
			civPanicRest = AssetLoader.civ_panic_rest0;
			break;
		case 1:
			civWalkAnimation = AssetLoader.civAnimation1;
			civPanicAnimation = AssetLoader.civPanicAnimation1;
			civStunAnimation = AssetLoader.civStunAnimation1;
			civBatAnimation = AssetLoader.civBatAnimation1;
			civShotgunAnimation = AssetLoader.civShotgunAnimation1;
			civDisarmAnimation = AssetLoader.civDisarmAnimation1;
			civRest = AssetLoader.civ_rest1;
			civPanicRest = AssetLoader.civ_panic_rest1;
			break;
		case 2:
			civWalkAnimation = AssetLoader.civAnimation2;
			civPanicAnimation = AssetLoader.civPanicAnimation2;
			civStunAnimation = AssetLoader.civStunAnimation2;
			civBatAnimation = AssetLoader.civBatAnimation2;
			civShotgunAnimation = AssetLoader.civShotgunAnimation2;
			civDisarmAnimation = AssetLoader.civDisarmAnimation2;
			civRest = AssetLoader.civ_rest2;
			civPanicRest = AssetLoader.civ_panic_rest2;
			break;
		case 3:
			civWalkAnimation = AssetLoader.civAnimation3;
			civPanicAnimation = AssetLoader.civPanicAnimation3;
			civStunAnimation = AssetLoader.civStunAnimation3;
			civBatAnimation = AssetLoader.civBatAnimation3;
			civShotgunAnimation = AssetLoader.civShotgunAnimation3;
			civDisarmAnimation = AssetLoader.civDisarmAnimation3;
			civRest = AssetLoader.civ_rest3;
			civPanicRest = AssetLoader.civ_panic_rest3;
			break;
		default:
			System.out.println("CIVILIAN CLASS ANIMATION ERROR");
		}
		body.setUserData(civWalkAnimation);

		walkSound = AssetLoader.walkSound;
		runSound = AssetLoader.runSound;

	}

	@Override
	public void render(OrthographicCamera cam, SpriteBatch batch) {

		super.render(cam, batch);

		seen = gWorld.getPlayer().lightContains(body.getPosition().x, body.getPosition().y);
		currentAnimation = (Animation) body.getUserData();

		if (seen) {
			runTime += Gdx.graphics.getRawDeltaTime();
			batch.begin();
			if (currentAnimation == civPanicAnimation) {
				if (((Panic) ability).getStatus() == false) {
					body.setUserData(civWalkAnimation);
				}
				if (!body.getLinearVelocity().isZero() && checkMovable()) {
					if (walkSound.isPlaying() && isPlayer()) {
						walkSound.stop();
					}
					if (!runSound.isPlaying() && isPlayer()) {
						runSound.play();
					}
					batch.draw(currentAnimation.getKeyFrame(runTime * 5, true), body.getPosition().x - 9,
							body.getPosition().y - 9, 9, 9, 18, 18, 6f, 6f,
							(float) (body.getAngle() * 180 / Math.PI) - 90);

				} else {
					if (runSound.isPlaying() && isPlayer()) {
						runSound.stop();
					}
					batch.draw(civPanicRest, body.getPosition().x - 9, body.getPosition().y - 9, 9, 9, 18,
							18, 6f, 6f, (float) (body.getAngle() * 180 / Math.PI) - 90);
				}
			} else if (currentAnimation == civWalkAnimation) {
				if (!body.getLinearVelocity().isZero() && checkMovable()) {
					if (!walkSound.isPlaying() && isPlayer()) {
						walkSound.play();
					}
					if (runSound.isPlaying() && isPlayer()) {
						runSound.stop();
					}
					batch.draw(currentAnimation.getKeyFrame(runTime * 4, true), body.getPosition().x - 9,
							body.getPosition().y - 9, 9, 9, 18, 18, 6f, 6f,
							(float) (body.getAngle() * 180 / Math.PI) - 90);

				} else {
					if (walkSound.isPlaying() && isPlayer()) {
						walkSound.stop();
					}
					batch.draw(civRest, body.getPosition().x - 9, body.getPosition().y - 9, 9, 9, 18, 18, 6f,
							6f, (float) (body.getAngle() * 180 / Math.PI) - 90);
				}
			} else {
				animationRunTime += Gdx.graphics.getRawDeltaTime();
				if (currentAnimation.isAnimationFinished(animationRunTime)) {
					animationRunTime = 0;
					body.setUserData(civWalkAnimation);
				} else {
					body.setLinearVelocity(0, 0);
					body.setAngularVelocity(0);
					batch.draw(currentAnimation.getKeyFrame(animationRunTime, true),
							body.getPosition().x - 9, body.getPosition().y - 9, 9, 9, 18, 18, 6f, 6f,
							(float) (body.getAngle() * 180 / Math.PI) - 90);
				}
			}
			batch.end();
		} else {
			if (!currentAnimation.isAnimationFinished(animationRunTime)) {
				animationRunTime = 0;
				body.setUserData(civWalkAnimation);
			}
		}

	}

	public void die() {
		coneLight.remove();
		pointLight.remove();
		super.die();
	}

	public boolean useAbility() {// panic
		if (!isStun()) {
			if (super.useAbility()) {
				if (seen)
					body.setUserData(civPanicAnimation);
				return true;
			} else {
				if (isPlayer)
					gWorld.getTM().setDisplayMessage("You pant... completely out of breath");
			}
		}
		return false;
	}

	public void stun() {// stun
		super.stun();
		if (seen)
			body.setUserData(civStunAnimation);

	}

	public boolean useWeapon() {// bat
		if (!isStun()) {
			if (currentAnimation == civWalkAnimation || currentAnimation == civPanicAnimation) {
				if (super.useWeapon()) {// boolean
					if (weapon.getName().equals("Shotgun")) {
						if (seen)
							body.setUserData(civShotgunAnimation);
					} else {
						if (seen)
							body.setUserData(civBatAnimation);
					}
					return true;
				}
			}
		}
		return false;
	}

	public void useItem() {
		if (!isStun()) {
			super.useItem();
			if (seen)
				body.setUserData(civDisarmAnimation);
		}
	}
}
