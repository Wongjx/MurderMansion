package com.jkjk.GameObjects.Weapons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.GameWorld.GameWorld;

public class Knife extends Weapon {

	private BodyDef bdef;
	private FixtureDef fdef;
	private Vector2 playerPosition;
	private float playerAngle;

	public Knife(GameWorld gWorld) {
		super(gWorld);
		bdef = new BodyDef();
		fdef = new FixtureDef();
	}

	@Override
	public void use() {
		System.out.println("Used knife");
		playerPosition = gWorld.getPlayer().getBody().getPosition();
		playerAngle = gWorld.getPlayer().getBody().getAngle();
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(playerPosition.x, playerPosition.y);
		bdef.angle = playerAngle;
		body = gWorld.getWorld().createBody(bdef);

		Vector2[] vertices = { new Vector2(11, 0), new Vector2(20, 8.9f), new Vector2(28, 5.6f),
				new Vector2(32, 0), new Vector2(28, -5.6f), new Vector2(20, -8.9f) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("knife");

		hitBoxExposure.startExposure();
	}


	@Override
	public void postUse(GameWorld gWorld) {

	}

}
