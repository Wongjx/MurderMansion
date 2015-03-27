package com.jkjk.GameObjects.Characters;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.MMHelpers.AssetLoader;

public class Ghost extends GameCharacter {

	private PointLight pointLight;
	private Texture civ_dead_lines;
	private Animation charAnim;
	
	public Ghost(int id, World world) {
		super("Ghost", id, world);

		// create body of murderer
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		// triangular body fixture
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		fdef.shape = shape;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("ghost");

		// create light
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.12f);
		pointLight = new PointLight(rayHandler, 100, null, 150, 0, 0);
		pointLight.attachToBody(body);
		PointLight.setContactFilter((short) 2, (short) 2, (short) 0);

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
		
		civ_dead_lines = AssetLoader.civ_dead_lines;
	}
	
	@Override
	public void render(OrthographicCamera cam){
		
		batch.setProjectionMatrix(cam.combined);
		
		batch.begin();
		batch.draw(civ_dead_lines, this.get_deathPositionX()-33/2, this.get_deathPositionY()-32/2, 33, 32);
		batch.end();
		
		super.render(cam);
		charAnim = AssetLoader.civAnimation;
		body.setUserData(charAnim);
		batch = new SpriteBatch();
		runTime = 0;
	}
	
}
