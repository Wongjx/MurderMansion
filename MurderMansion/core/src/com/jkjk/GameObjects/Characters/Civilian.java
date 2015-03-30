package com.jkjk.GameObjects.Characters;

import box2dLight.ConeLight;
import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

public class Civilian extends GameCharacter {

	private PointLight pointLight;
	private ConeLight coneLight;
	private Touchpad touchpad;
	private Animation currentAnimation;
	private double hypothenuse;
	private GameWorld gWorld;
	private float animationRunTime;

	Civilian(int id, GameWorld gWorld, boolean isPlayer) {

		super("Civilian", id, gWorld, isPlayer);
		this.gWorld = gWorld;
		touchpad = AssetLoader.touchpad;

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
		coneLight = new ConeLight(rayHandler, 100, null, 140, 0, 0, 0, 40);
		coneLight.attachToBody(body, 0, 0);
		ConeLight.setContactFilter((short) 2, (short) 2, (short) 1);
		pointLight = new PointLight(rayHandler, 100, null, 30, 0, 0);
		pointLight.attachToBody(body);

		// cone-ish Torch light fixture
		/*
		 * FixtureDef coneFdef = new FixtureDef(); Vector2[] ConeLightVert = { new Vector2(0, 0), new
		 * Vector2(113, 99), new Vector2(122, 87), new Vector2(146, 34), new Vector2(150, 0), new Vector2(146,
		 * -34), new Vector2(122, -87), new Vector2(113, -99) }; PolygonShape coneShape = new PolygonShape();
		 * coneFdef.isSensor = true; coneShape.set(ConeLightVert); coneFdef.shape = coneShape;
		 * coneFdef.filter.maskBits = 1;// cannot bump into other light bodies.
		 * body.createFixture(coneFdef).setUserData("lightBody");
		 */
		animationRunTime = 0;
		body.setUserData(AssetLoader.civAnimation);
	}

	@Override
	public void render(OrthographicCamera cam) {
		
		super.render(cam);
		
		if (gWorld.getPlayer().lightContains(body.getPosition().x, body.getPosition().y)) {
			runTime += Gdx.graphics.getRawDeltaTime();
			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			currentAnimation = (Animation) body.getUserData();
			if (currentAnimation == AssetLoader.civBatAnimation
					|| currentAnimation == AssetLoader.civDisarmAnimation
					|| currentAnimation == AssetLoader.civKnifeDeathAnimation
					|| currentAnimation == AssetLoader.civTrapDeathAnimation) {
				animationRunTime += Gdx.graphics.getRawDeltaTime();
				if (currentAnimation.isAnimationFinished(animationRunTime)) {
					animationRunTime = 0;
					body.setUserData(AssetLoader.civAnimation);
				} else {
					body.setLinearVelocity(0, 0);
					body.setAngularVelocity(0);
					batch.draw(currentAnimation.getKeyFrame(animationRunTime, true), body.getPosition().x - 10,
							body.getPosition().y - 10, 10, 10, 20, 20, 1, 1,
							(float) (body.getAngle() * 180 / Math.PI) - 90);
				}
				
			} else {
				if (!body.getLinearVelocity().isZero() && checkMovable()) {
					batch.draw(currentAnimation.getKeyFrame(runTime, true), body.getPosition().x - 10,
							body.getPosition().y - 10, 10, 10, 20, 20, 1, 1,
							(float) (body.getAngle() * 180 / Math.PI) - 90);
				} else {
					batch.draw(AssetLoader.civ_rest, body.getPosition().x - 10, body.getPosition().y - 10, 10, 10, 20,
							20, 1, 1, (float) (body.getAngle() * 180 / Math.PI) - 90);
				}
			}

			batch.end();
		}
		

	}

	public boolean lightContains(float x, float y) {
		return coneLight.contains(x, y) || pointLight.contains(x, y);
	}
}
