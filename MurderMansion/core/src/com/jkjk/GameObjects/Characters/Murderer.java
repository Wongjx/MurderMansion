package com.jkjk.GameObjects.Characters;

import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

public class Murderer extends GameCharacter {

	private PointLight pointLight;
	private Animation currentAnimation;
	private Touchpad touchpad;
	private TextureRegion civ_rest;
	private TextureRegion mur_rest;
	private Animation charAnim;
	private GameWorld gWorld;

	public Murderer(int id, GameWorld gWorld, boolean isPlayer) {
		super("Murderer", id, gWorld, isPlayer);

		this.gWorld = gWorld;
		touchpad = AssetLoader.touchpad;

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

		// light fixture
		/*
		 * FixtureDef lightFdef = new FixtureDef(); CircleShape circle = new CircleShape(); lightFdef.isSensor
		 * = true; circle.setPosition(getBody().getPosition()); circle.setRadius(100); lightFdef.shape =
		 * circle; lightFdef.filter.maskBits = 1; body.createFixture(lightFdef).setUserData("lightBody");
		 */

		charAnim = AssetLoader.civAnimation;
		body.setUserData(charAnim);

		disguised = true;

		civ_rest = AssetLoader.civ_rest;
		// mur_rest = AssetLoader.mur_rest;
	}

	@Override
	public void render(OrthographicCamera cam) {
		if (gWorld.getPlayer().lightContains(body.getPosition().x, body.getPosition().y)) {
			// charAnim = (Animation) body.getUserData();
			runTime += Gdx.graphics.getRawDeltaTime();
			currentAnimation = (Animation) body.getUserData();
			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			if (currentAnimation == AssetLoader.murKnifeAnimation
					|| currentAnimation == AssetLoader.murPlantTrapAnimation
					|| currentAnimation == AssetLoader.murDeathAnimation
					|| currentAnimation == AssetLoader.murToCivAnimation
					|| currentAnimation == AssetLoader.civToMurAnimation) {
				if (currentAnimation.isAnimationFinished(runTime)) { // reset
					if (isDisguised()) {
						currentAnimation = AssetLoader.civAnimation;
						body.setUserData(AssetLoader.civAnimation);
					} else {
						currentAnimation = AssetLoader.murAnimation;
						body.setUserData(AssetLoader.murAnimation);
					}
				} else {// disable touchpad while special animation occurs.
					body.setLinearVelocity(0, 0);
					body.setAngularVelocity(0);
				}
			} else {
				if (!body.getLinearVelocity().isZero() && checkMovable()) {
					batch.draw(currentAnimation.getKeyFrame(runTime, true), body.getPosition().x - 10,
							body.getPosition().y - 10, 10, 10, 20, 20, 1, 1,
							(float) (body.getAngle() * 180 / Math.PI) - 90);
				} else {
					if (isDisguised()) {
						batch.draw(civ_rest, body.getPosition().x - 10, body.getPosition().y - 10, 10, 10,
								20, 20, 1, 1, (float) (body.getAngle() * 180 / Math.PI) - 90);
					} else {
						batch.draw(mur_rest, body.getPosition().x - 10, body.getPosition().y - 10, 10, 10,
								20, 20, 1, 1, (float) (body.getAngle() * 180 / Math.PI) - 90);
					}
				}
			}

			batch.end();
		}
		super.render(cam);

	}

	@Override
	public void useAbility() {
		if (!ability.isOnCoolDown()) {
			ability.use();
			ability.cooldown();
			abilityChange = true;
		}
	}

	public boolean lightContains(float x, float y) {
		return pointLight.contains(x, y);
	}
}
