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

public class Ghost extends GameCharacter {

	private PointLight pointLight;
	private GameWorld gWorld;
	private Animation currentAnimation;
	
//	public Ghost(int id, World world) {
//		super("Ghost", id, world,true);
//	}
//	

	public Ghost(int id, GameWorld gWorld, boolean isPlayer) {
		super("Ghost", id, gWorld, isPlayer);

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
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("ghost");

		// create light
		pointLight = new PointLight(rayHandler, 100, null, 100, 0, 0);
		pointLight.attachToBody(body);
		PointLight.setContactFilter((short) 2, (short) 2, (short) 0);
		
		body.setUserData(AssetLoader.ghostFloatAnimation);
	}

	@Override
	public void render(OrthographicCamera cam, SpriteBatch batch) {
		runTime += Gdx.graphics.getRawDeltaTime();
		currentAnimation = (Animation) body.getUserData();
		batch.begin();
		batch.draw(AssetLoader.civ_dead_lines, this.get_deathPositionX() - 33 / 2, this.get_deathPositionY() - 32 / 2, 33, 32);
		batch.draw(AssetLoader.ghost_float, body.getPosition().x-16, 
				body.getPosition().y-17, 32,32); 
				//(float) (body.getAngle()*180/Math.PI)-90);
		batch.end();

		super.render(cam, batch);

	}

	public boolean lightContains(float x, float y) {
		return pointLight.contains(x, y);
	}

}
