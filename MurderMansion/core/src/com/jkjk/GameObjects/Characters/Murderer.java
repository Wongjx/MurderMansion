package com.jkjk.GameObjects.Characters;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.jkjk.MMHelpers.AssetLoader;

public class Murderer extends GameCharacter {

	private PointLight pointLight;
	private SpriteBatch batch;
	private float runTime;
	private Animation charAnim;
	
	public Murderer(int id, World world) {
		super("Murderer", id);

		// create body of murderer
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		// triangular body fixture
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("murderer");

		// create light
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.12f);
		pointLight = new PointLight(rayHandler, 100, null, 150, 0, 0);
		pointLight.attachToBody(body);
		PointLight.setContactFilter((short) 2, (short) 2, (short) 1);

		// light fixture
		FixtureDef lightFdef = new FixtureDef();
		CircleShape circle = new CircleShape();
		lightFdef.isSensor = true;
		circle.setPosition(getBody().getPosition());
		;
		circle.setRadius(150);
		lightFdef.shape = circle;
		lightFdef.filter.maskBits = 1;
		body.createFixture(lightFdef).setUserData("lightBody");
		charAnim = AssetLoader.civAnimation;
		body.setUserData(charAnim);
		batch = new SpriteBatch();
		runTime = 0;
	}
	@Override
	public void render(OrthographicCamera cam){
		super.render(cam);
		
		//charAnim = (Animation) body.getUserData();
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		runTime +=Gdx.graphics.getRawDeltaTime();
		batch.draw(charAnim.getKeyFrame(runTime,true), body.getPosition().x-10, body.getPosition().y-10, 10, 10, 20, 20, 1, 1,(float) (body.getAngle()*180/Math.PI)-90);
		batch.end();
		disguised = true;

	}
	
	@Override
	public void useAbility() {
		if (!ability.isOnCoolDown()) {
			abilityChange = true;
		}
	}
}
