package com.jkjk.GameObjects.Weapons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jkjk.GameWorld.GameWorld;

public class Bat extends Weapon {

	private BodyDef bdef;
	private Body body;
	private FixtureDef fdef;
	private Vector2 playerPosition;
	private float playerAngle;

	public Bat() {
		bdef = new BodyDef();
		fdef = new FixtureDef();

	}

	@Override
	public void use(GameWorld gWorld) {
		System.out.println("Used bat");
		playerPosition = gWorld.getPlayer().getBody().getPosition();
		playerAngle = gWorld.getPlayer().getBody().getAngle();
		bdef.type = BodyType.StaticBody;
		bdef.position.set(playerPosition.x, playerPosition.y);
		bdef.angle = playerAngle;
		body = gWorld.getWorld().createBody(bdef);

		Vector2[] vertices = { new Vector2(0, 0), new Vector2(41, 24), new Vector2(44.5f, 16), new Vector2(47, 8),
				new Vector2(48, 0), new Vector2(47,-8), new Vector2(44.5f, -16), new Vector2(41,-24) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		fdef.isSensor = true;
		
		body.createFixture(fdef).setUserData("Bat");
	}

	@Override
	public void cooldown() {
		// TODO Auto-generated method stub

	}

}
