package com.jkjk.GameObjects.Items;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

public class Trap extends Item {
	
	private BodyDef bdef;
	private Body body;
	private FixtureDef fdef;
	private Vector2 playerPosition;
	private float playerAngle;

	public Trap(GameWorld gWorld) {
		super(gWorld);
		bdef = new BodyDef();
		fdef = new FixtureDef();
	}

	@Override
	public void use() {
		System.out.println("Used trap");
		gWorld.getPlayer().getBody().setUserData(AssetLoader.murPlantTrapAnimation);
		playerPosition = gWorld.getPlayer().getBody().getPosition();
		playerAngle = gWorld.getPlayer().getBody().getAngle();
		bdef.type = BodyType.StaticBody;
		bdef.position.set(playerPosition.x, playerPosition.y);
		body = gWorld.getWorld().createBody(bdef);

		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		shape.setPosition(new Vector2((float) (25f*Math.cos(playerAngle)),(float) (25f*Math.sin(playerAngle))));
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		
		body.createFixture(fdef).setUserData("trap");
		
		isCompleted = true;
	}
	
	
}
