package com.jkjk.GameObjects.Characters;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;


public class Ghost extends GameCharacter {

	private PointLight pointLight;
	
	
	public Ghost(World world) {
		setType("Ghost");
		
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
		circle.setPosition(getBody().getPosition());;
		circle.setRadius(150);
		lightFdef.shape = circle;
		lightFdef.filter.maskBits = 1;
		body.createFixture(lightFdef).setUserData("lightBody");
	}

}
