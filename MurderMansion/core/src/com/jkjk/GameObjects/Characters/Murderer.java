package com.jkjk.GameObjects.Characters;

import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
		pointLight = new PointLight(rayHandler, 100, null, 100, 0, 0);
		pointLight.attachToBody(body);
		PointLight.setContactFilter((short) 2, (short) 2, (short) 1);
		body.setUserData(AssetLoader.civAnimation);// starts disguised
		disguised = true;
		animationRunTime = 0;

	}

	@Override
	public void render(OrthographicCamera cam, SpriteBatch batch) {
		if (gWorld.getPlayer().lightContains(body.getPosition().x, body.getPosition().y)) {
			runTime += Gdx.graphics.getRawDeltaTime();
			currentAnimation = (Animation) body.getUserData();
			batch.begin();
			if (currentAnimation == AssetLoader.murKnifeAnimation
					|| currentAnimation == AssetLoader.murPlantTrapAnimation
					|| currentAnimation == AssetLoader.murDeathAnimation
					|| currentAnimation == AssetLoader.murToCivAnimation
					|| currentAnimation == AssetLoader.civToMurAnimation) {
				if (currentAnimation.isAnimationFinished(animationRunTime)) { // reset
					animationRunTime = 0;
					if (isDisguised()) {
						body.setUserData(AssetLoader.civAnimation);
					} else {
						body.setUserData(AssetLoader.murAnimation);
					}
				} else {// disable touchpad while special animation occurs.
					body.setLinearVelocity(0, 0);
					body.setAngularVelocity(0);
					batch.draw(currentAnimation.getKeyFrame(animationRunTime, true),
							body.getPosition().x -9,
							body.getPosition().y - 9, 9, 9, 18, 18, 6f, 6f,
							(float) (body.getAngle() * 180 / Math.PI) - 90);
				}
			} else {
				if (!body.getLinearVelocity().isZero() && checkMovable()) {
					batch.draw(currentAnimation.getKeyFrame(runTime, true), body.getPosition().x -9,
							body.getPosition().y - 9, 9, 9, 18, 18, 6f, 6f,
							(float) (body.getAngle() * 180 / Math.PI) - 90);
				} else {
					if (isDisguised()) {
						batch.draw(AssetLoader.civ_rest, body.getPosition().x -9,
								body.getPosition().y - 9, 9, 9, 18, 18, 6f, 6f,
								(float) (body.getAngle() * 180 / Math.PI) - 90);
					} else {
						batch.draw(AssetLoader.mur_rest, body.getPosition().x -9,
								body.getPosition().y - 9, 9, 9, 18, 18, 6f, 6f,
								(float) (body.getAngle() * 180 / Math.PI) - 90);
					}
				}
			}

			batch.end();
		}
		
		super.render(cam, batch);

	}

	@Override
	public void useAbility() {
		if (!ability.isOnCoolDown()) {
			ability.use();
			ability.cooldown();
			abilityChange = true;
		}
	}
	
	public boolean isDisguised() {
		return disguised;
	}

	public void setDisguise(boolean disguised) {
		this.disguised = disguised;
	}

	public boolean lightContains(float x, float y) {
		return pointLight.contains(x, y);
	}
	
	public void useWeapon(){
		if (!disguised){
			super.useWeapon();
		} else {
			System.out.println("You cannot use your weapon while disguised.");
		}
	}
}
