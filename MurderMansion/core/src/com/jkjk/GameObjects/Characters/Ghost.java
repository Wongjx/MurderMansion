package com.jkjk.GameObjects.Characters;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
<<<<<<< HEAD
import com.badlogic.gdx.graphics.Texture;
=======
import com.badlogic.gdx.graphics.g2d.Animation;
>>>>>>> 79ed37b1bd35cdcbd6fdb3e44a7ce6b495fb440c
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
<<<<<<< HEAD
import com.jkjk.GameWorld.GameWorld;
=======
>>>>>>> 79ed37b1bd35cdcbd6fdb3e44a7ce6b495fb440c
import com.jkjk.MMHelpers.AssetLoader;

public class Ghost extends GameCharacter {

	private PointLight pointLight;
	private SpriteBatch batch;
<<<<<<< HEAD
	private Texture civ_dead_lines;

=======
	private float runTime;
	private Animation charAnim;
	
>>>>>>> 79ed37b1bd35cdcbd6fdb3e44a7ce6b495fb440c
	public Ghost(int id, World world) {
		super("Ghost", id);

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
<<<<<<< HEAD
		
		batch = new SpriteBatch();
		civ_dead_lines = AssetLoader.civ_dead_lines;
	}
	
	@Override
	public void render(OrthographicCamera cam){
		
		batch.setProjectionMatrix(cam.combined);
		
		batch.begin();
		batch.draw(civ_dead_lines, this.get_deathPositionX()-33/2, this.get_deathPositionY()-32/2, 33, 32);
		batch.end();
		
		super.render(cam);
=======
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

>>>>>>> 79ed37b1bd35cdcbd6fdb3e44a7ce6b495fb440c
	}

	
}
