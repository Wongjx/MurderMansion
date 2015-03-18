package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Civilian extends GameCharacter {

	private FixtureDef fdef;
	private BodyDef bdef;
	private World world;
	private FixtureDef coneFdef;
	private BodyDef coneBdef;

	Civilian(int colour, World world) {
		this.world = world;
		fdef = new FixtureDef();
		bdef = new BodyDef();
		setName("Civilian");
		setColour(colour);

		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		Vector2[] vertices = { new Vector2(0, 0), new Vector2(-20, -10), new Vector2(-20, 10) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;

		body.createFixture(fdef).setUserData("civilian");

		Vector2[] ConeLightVert = { new Vector2(0, 0), new Vector2(100, 100), new Vector2(100, -100) };// triangle
																										// first
																										// for
																										// testing
		PolygonShape coneShape = new PolygonShape();
		coneFdef = new FixtureDef();
		coneFdef.isSensor = true;
		coneShape.set(ConeLightVert);
		coneFdef.shape = coneShape;
		coneFdef.filter.maskBits = 1;

		coneBdef = new BodyDef();
		coneBdef.type = BodyType.StaticBody;
		coneBdef.position.set(getBody().getPosition());
		coneBdef.angle = getBody().getAngle();

		Body coneBody = world.createBody(coneBdef);
		coneBody.createFixture(coneFdef).setUserData("lightBody");
		setLightBody(coneBody);
	}

	@Override
	public void spawn(float x, float y, float angle) {
		alive = true;
		body.setTransform(x, y, angle); // Spawn position
	}

	public void die() {
		alive = false;
	}
}

// public void update(){
// Vector2 v2 = getBody().getPosition();
// getLightBody().setTransform(v2.x,v2.y,getBody().getAngle());
// }