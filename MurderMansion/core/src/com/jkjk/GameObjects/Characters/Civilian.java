package com.jkjk.GameObjects.Characters;

import box2dLight.ConeLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Civilian extends GameCharacter {

	private World world;
	private ConeLight coneLight;

	Civilian(int colour, World world) {
		this.world = world;
		setType("Civilian");
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
		
		// Create Light for player
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.12f);
		coneLight = new ConeLight(rayHandler, 100, null, 200, 0, 0, 0, 40);
		coneLight.attachToBody(body, -10, 0);
		ConeLight.setContactFilter((short) 2, (short) 2, (short) 1);

		// cone-ish Torch light fixture
		FixtureDef coneFdef = new FixtureDef();
		Vector2[] ConeLightVert = { new Vector2(-15, 0), new Vector2(113, 99), new Vector2(122, 87),
				new Vector2(146, 34), new Vector2(150, 0), new Vector2(146, -34), new Vector2(122, -87),
				new Vector2(113, -99) };
		PolygonShape coneShape = new PolygonShape();
		coneFdef.isSensor = true;
		coneShape.set(ConeLightVert);
		coneFdef.shape = coneShape;
		coneFdef.filter.maskBits = 1;// cannot bump into other light bodies.
		body.createFixture(coneFdef).setUserData("lightBody");

	}
}
