package com.jkjk.GameObjects.Weapons;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
	private Executor executor;

	public Bat() {
		bdef = new BodyDef();
		fdef = new FixtureDef();
		executor = Executors.newFixedThreadPool(2);
	}

	@Override
	public void use(GameWorld gWorld) {
		System.out.println("Used bat");
		playerPosition = gWorld.getPlayer().getBody().getPosition();
		playerAngle = gWorld.getPlayer().getBody().getAngle();
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(playerPosition.x, playerPosition.y);
		bdef.angle = playerAngle;
		body = gWorld.getWorld().createBody(bdef);

		Vector2[] vertices = { new Vector2(1, 0), new Vector2(27.7f, 16), new Vector2(30, 10.9f), new Vector2(31.5f, 5.6f),
				new Vector2(32, 0), new Vector2(31.5f,-5.6f), new Vector2(30, -10.9f), new Vector2(27.7f,-16) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		fdef.isSensor = true;
		//fdef.filter.maskBits = 1;
		
		body.createFixture(fdef).setUserData("bat");
		
		executor.execute(new WeaponDuration(this, gWorld.getWorld(), body));
	}
	
	public void postUse(GameWorld gWorld){
		
	}
	

	@Override
	public void cooldown() {
		executor.execute(new WeaponCooldown(this));
	}

}
