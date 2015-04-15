package com.jkjk.GameObjects.Characters;

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
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

public class Murderer extends GameCharacter {

	private PointLight pointLight;
	private Animation currentAnimation;
	private GameWorld gWorld;
	private float animationRunTime;
	private boolean disguised; // true for civilian, false for murderer

	// Animations for disguised
	private Animation civWalkAnimation;
	private Animation civStunAnimation;
	private Animation murToCivAnimation;
	private Animation civToMurAnimation;
	private Animation civPlantTrapAnimation;
	private TextureRegion civRest;

	private Music walkSound;

	Murderer(int id, GameWorld gWorld, boolean isPlayer) {
		super("Murderer", id, gWorld, isPlayer);

		this.gWorld = gWorld;

		// create body of murderer
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		body = gWorld.getWorld().createBody(bdef);

		// triangular body fixture
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		fdef.shape = shape;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("murderer");

		// create light
		pointLight = new PointLight(rayHandler, 100, null, 120, 0, 0);
		pointLight.attachToBody(body);
		Light.setContactFilter((short) 2, (short) 2, (short) 1);
		disguised = true;

		// INITIATE ANIMATIONS

		animationRunTime = 0;
		switch (id % AssetLoader.NUM_CIVILIAN_TEXTURES) {
		case 0:
			civWalkAnimation = AssetLoader.civAnimation0;
			civStunAnimation = AssetLoader.civStunAnimation0;
			murToCivAnimation = AssetLoader.murToCivAnimation0;
			civToMurAnimation = AssetLoader.civToMurAnimation0;
			civPlantTrapAnimation = AssetLoader.civDisarmAnimation0;
			civRest = AssetLoader.civ_rest0;
			break;
		case 1:
			civWalkAnimation = AssetLoader.civAnimation1;
			civStunAnimation = AssetLoader.civStunAnimation1;
			murToCivAnimation = AssetLoader.murToCivAnimation1;
			civToMurAnimation = AssetLoader.civToMurAnimation1;
			civPlantTrapAnimation = AssetLoader.civDisarmAnimation1;
			civRest = AssetLoader.civ_rest1;
			break;
		case 2:
			civWalkAnimation = AssetLoader.civAnimation2;
			civStunAnimation = AssetLoader.civStunAnimation2;
			murToCivAnimation = AssetLoader.murToCivAnimation2;
			civToMurAnimation = AssetLoader.civToMurAnimation2;
			civPlantTrapAnimation = AssetLoader.civDisarmAnimation2;
			civRest = AssetLoader.civ_rest2;
			break;
		case 3:
			civWalkAnimation = AssetLoader.civAnimation3;
			civStunAnimation = AssetLoader.civStunAnimation3;
			murToCivAnimation = AssetLoader.murToCivAnimation3;
			civToMurAnimation = AssetLoader.civToMurAnimation3;
			civPlantTrapAnimation = AssetLoader.civDisarmAnimation3;
			civRest = AssetLoader.civ_rest3;
			break;
		default:
			System.out.println("MURDERER CLASS ANIMATION ERROR");
		}
		body.setUserData(civWalkAnimation);

		walkSound = AssetLoader.walkSound;
	}

	@Override
	public void render(OrthographicCamera cam, SpriteBatch batch) {
		
		super.render(cam, batch);
		
		if (gWorld.getPlayer().lightContains(body.getPosition().x, body.getPosition().y)) {
			runTime += Gdx.graphics.getRawDeltaTime();
			currentAnimation = (Animation) body.getUserData();
			batch.begin();

			if (currentAnimation == AssetLoader.murAnimation || currentAnimation == civWalkAnimation) {
				if (!body.getLinearVelocity().isZero() && checkMovable()) {
					if (!walkSound.isPlaying() && isPlayer()) {
						walkSound.play();
					}
					batch.draw(currentAnimation.getKeyFrame(runTime * 4, true), body.getPosition().x - 9,
							body.getPosition().y - 9, 9, 9, 18, 18, 6f, 6f,
							(float) (body.getAngle() * 180 / Math.PI) - 90);
				} else {
					if (walkSound.isPlaying() && isPlayer()) {
						walkSound.stop();
					}
					if (isDisguised()) {
						batch.draw(civRest, body.getPosition().x - 9, body.getPosition().y - 9, 9, 9, 18, 18,
								6f, 6f, (float) (body.getAngle() * 180 / Math.PI) - 90);
					} else {
						batch.draw(AssetLoader.mur_rest,
								body.getPosition().x - 9,// to be changed to mur_rest when ready.
								body.getPosition().y - 9, 9, 9, 18, 18, 6f, 6f,
								(float) (body.getAngle() * 180 / Math.PI) - 90);
					}
				}
			} else {
				animationRunTime += Gdx.graphics.getRawDeltaTime();
				if (currentAnimation.isAnimationFinished(animationRunTime)) { // reset
					animationRunTime = 0;
					if (!isDisguised()) {
						body.setUserData(AssetLoader.murAnimation);
					} else {
						body.setUserData(civWalkAnimation);
					}
				} else {// disable touchpad while special animation occurs.
					body.setLinearVelocity(0, 0);
					body.setAngularVelocity(0);
					batch.draw(currentAnimation.getKeyFrame(animationRunTime, true),
							body.getPosition().x - 9, body.getPosition().y - 9, 9, 9, 18, 18, 6f, 6f,
							(float) (body.getAngle() * 180 / Math.PI) - 90);
				}
			}

			batch.end();
		}

	}

	public void die() {
		pointLight.remove();
		super.die();
	}

	@Override
	public boolean useAbility() {
		if (!ability.isOnCoolDown()) {
			ability.use();
			ability.cooldown();
			abilityChange = true;
			if (disguised) {
				body.setUserData(civToMurAnimation);
			} else {
				body.setUserData(murToCivAnimation);
			}
			return true;
		}
		return false;
	}

	public boolean isDisguised() {
		return disguised;
	}

	public void setDisguise(boolean disguised) {
		this.disguised = disguised;
	}

	public boolean useWeapon() {
		if (currentAnimation == AssetLoader.murAnimation) {
			if (super.useWeapon()) {
				body.setUserData(AssetLoader.murKnifeAnimation);
				return true;
			} else {
				return false;
			}
		} else {
			System.out.println("You cannot use your weapon while disguised.");
			return false;
		}
	}

	public void useItem() {
		if (currentAnimation == AssetLoader.murAnimation || currentAnimation == civWalkAnimation) {
			super.useItem();
			if (disguised) {
				body.setUserData(civPlantTrapAnimation);
			} else {
				body.setUserData(AssetLoader.murPlantTrapAnimation);
			}
		}
	}
	
	public void stun() {
		super.stun();
		if (!isDisguised()) {
			body.setUserData(AssetLoader.murStunAnimation);
		} else {
			body.setUserData(civStunAnimation);
		}
	}
}
