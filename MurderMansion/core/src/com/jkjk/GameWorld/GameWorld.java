package com.jkjk.GameWorld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.jkjk.GameObjects.Characters.Civilian;
import com.jkjk.GameObjects.Characters.Murderer;

public class GameWorld {
	private Civilian civilian;
	private Murderer murderer;
	
	private GameRenderer renderer;
	private World world;
	
	private float screenWidth;
	private float screenHeight;

	public GameWorld(float screenWidth, float screenHeight) {
		// create WORLD
		world = new World(new Vector2(0, 0), true);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		// create BODY for WORLD
		BodyDef bdef = new BodyDef();
		bdef.position.set(screenWidth/2,screenHeight/2);
		// static body - don't move, unaffected by forces *eg. ground
		// kinematic body - don't get affected by forces, but can move *eg. moving platform
		// dynamic body - always get affected by forces *eg. player
		bdef.type = BodyType.StaticBody;
		Body body = world.createBody(bdef);
		
		// create FIXTURE for BODY for WORLD
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(50, 5);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		body.createFixture(fdef);
		
		// create box
		bdef.position.set(screenWidth/2,screenHeight/2+40);
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);
		
		shape.setAsBox(5, 5);
		fdef.shape = shape;
		body.createFixture(fdef);

	}
	
	public World getWorld(){
		return world;
	}

	public void update(float delta) {
		world.step(delta, 6, 2); // Step size|Steps for each body to check collision|Accuracy of body position
									// after collision
	}

	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}
}
