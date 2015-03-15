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

	public Bat() {
		bdef = new BodyDef();
		fdef = new FixtureDef();

	}

	@Override
	public void use(GameWorld gWorld) {
		System.out.println("Used bat");
		playerPosition = gWorld.getPlayer().getBody().getPosition();
		bdef.type = BodyType.StaticBody;
		bdef.position.set(playerPosition.x - (float) (16/(Math.cos(playerPosition.angle()))), playerPosition.y - (float) (16/(Math.cos(playerPosition.angle()))));
		body = gWorld.getWorld().createBody(bdef);

		Vector2[] vertices = {new Vector2(0,0), new Vector2(-20,-10), new Vector2(-20,10)};
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		fdef.isSensor = true;
		
		body.createFixture(fdef);
	}

	@Override
	public void cooldown() {
		// TODO Auto-generated method stub

	}

}
