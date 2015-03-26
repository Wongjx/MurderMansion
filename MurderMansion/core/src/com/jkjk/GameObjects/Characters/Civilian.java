package com.jkjk.GameObjects.Characters;

import box2dLight.ConeLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.jkjk.MMHelpers.AssetLoader;

public class Civilian extends GameCharacter {

	private ConeLight coneLight;
	private SpriteBatch batch;
	private float runTime;
	private Touchpad touchpad;
	private Animation civAnimation;
	private TextureRegion civ_rest;
	private float ambientLightValue;

	Civilian(int id, World world) {

		super("Civilian", id);
		
		touchpad = AssetLoader.touchpad;

		// create body of civilian
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		// Circular body fixture
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("civilian");

		// Create Light for player
		ambientLightValue = 0;
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(ambientLightValue);
		coneLight = new ConeLight(rayHandler, 100, null, 200, 0, 0, 0, 40);
		coneLight.attachToBody(body, 0, 0);
		ConeLight.setContactFilter((short) 2, (short) 2, (short) 1);

		// cone-ish Torch light fixture
		FixtureDef coneFdef = new FixtureDef();
		Vector2[] ConeLightVert = { new Vector2(0, 0), new Vector2(113, 99), new Vector2(122, 87),
				new Vector2(146, 34), new Vector2(150, 0), new Vector2(146, -34), new Vector2(122, -87),
				new Vector2(113, -99) };
		PolygonShape coneShape = new PolygonShape();
		coneFdef.isSensor = true;
		coneShape.set(ConeLightVert);
		coneFdef.shape = coneShape;
		coneFdef.filter.maskBits = 1;// cannot bump into other light bodies.
		body.createFixture(coneFdef).setUserData("lightBody");

		civAnimation = AssetLoader.civAnimation;
		civ_rest = AssetLoader.civ_rest;
		body.setUserData(civAnimation);
		batch = new SpriteBatch();
		runTime = 0;
	}

	@Override
<<<<<<< HEAD
	public void render(OrthographicCamera cam){
		
		
		//charAnim = (Animation) body.getUserData();
=======
	public void render(OrthographicCamera cam) {

		// charAnim = (Animation) body.getUserData();

		if (runTime % 5.0 < 0.02){
			ambientLightValue += 0.003;
			System.out.println(ambientLightValue);
			rayHandler.setAmbientLight(ambientLightValue);
		}

		super.render(cam);
>>>>>>> 89ba71310709f43381f0348025c49a31335b2b4a
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		runTime += Gdx.graphics.getRawDeltaTime();
		
		if (touchpad.isTouched()){
			batch.draw(civAnimation.getKeyFrame(runTime,true), body.getPosition().x-10, body.getPosition().y-10, 10, 10, 20, 20, 1, 1,(float) (body.getAngle()*180/Math.PI)-90);
		}
		else{
			batch.draw(civ_rest, body.getPosition().x-10, body.getPosition().y-10, 10, 10, 20, 20, 1, 1,(float) (body.getAngle()*180/Math.PI)-90);
		}
		
		batch.end();
	}
}
