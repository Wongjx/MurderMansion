package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


public class Murderer extends GameCharacter {

	private boolean disguised;

	private FixtureDef fdef;
	private BodyDef bdef;
	private Body body;
	private World world;
	
	private FixtureDef lightFdef;
	private BodyDef lightBdef;
	
	public Murderer(World world) {
		this.world = world;
		fdef = new FixtureDef();
		bdef = new BodyDef();
		setName("Murderer");

		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		Vector2[] vertices = { new Vector2(0, 0), new Vector2(-20, -10), new Vector2(-20, 10) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;

		body.createFixture(fdef).setUserData("murderer");
		setBody(body);
		
		CircleShape circle = new CircleShape();
		lightFdef = new FixtureDef();
		lightFdef.isSensor = true;
		circle.setPosition(getBody().getPosition());;
		circle.setRadius(100);
		lightFdef.shape = circle;
		lightFdef.filter.maskBits = 1;
		
		lightBdef = new BodyDef();
		lightBdef.type = BodyType.StaticBody;
		
		Body lightBody = world.createBody(lightBdef);
		lightBody.createFixture(lightFdef).setUserData("lightBody");
		setLightBody(lightBody);
	}

	@Override
	public void spawn(float x, float y, float angle) {
		alive = true;
		body.setTransform(x, y, angle); // Spawn position
	}


	public void die() {
		alive = false;
	}
	
	public boolean isDisguised(){
		return disguised;
	}

}
