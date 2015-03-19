package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


public class Murderer extends GameCharacter {

	private boolean disguised;
	private World world;
	
	public Murderer(World world) {
		this.world = world;
		setName("Murderer");
		
		// create body of murderer
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		// triangular body fixture
		FixtureDef fdef = new FixtureDef();
		Vector2[] vertices = { new Vector2(0, 0), new Vector2(-20, -10), new Vector2(-20, 10) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("murderer");
		
		// light fixture
		FixtureDef lightFdef = new FixtureDef();
		CircleShape circle = new CircleShape();
		lightFdef.isSensor = true;
		circle.setPosition(getBody().getPosition());;
		circle.setRadius(100);
		lightFdef.shape = circle;
		lightFdef.filter.maskBits = 1;
		body.createFixture(lightFdef).setUserData("lightBody");
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
