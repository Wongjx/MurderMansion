package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Civilian extends GameCharacter {

	
	private World world;

	Civilian(int colour, World world) {
		this.world = world;
		setName("Civilian");
		setColour(colour);
		
		// create body of civilian
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);
		
		// triangular body fixture
		FixtureDef fdef = new FixtureDef();
		Vector2[] vertices = { new Vector2(0, 0), new Vector2(-20, -10), new Vector2(-20, 10) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("civilian");		
		
		// cone-ish Torch light fixture
		FixtureDef coneFdef = new FixtureDef();
		Vector2[] ConeLightVert = {new Vector2(-15,0), new Vector2(117,99), new Vector2(150,87), new Vector2(194,34), new Vector2(200,0), new Vector2(150,-87), new Vector2(194,-34), new Vector2(117,-99)};
		PolygonShape coneShape = new PolygonShape();
		coneFdef.isSensor = true;
		coneShape.set(ConeLightVert);
		coneFdef.shape = coneShape;
		coneFdef.filter.maskBits = 1;//cannot bump into other light bodies.
		body.createFixture(coneFdef).setUserData("lightBody");
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

