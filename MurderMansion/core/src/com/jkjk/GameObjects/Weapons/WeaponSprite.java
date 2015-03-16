package com.jkjk.GameObjects.Weapons;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class WeaponSprite {

	private Body body;
	private BodyDef bdef;
	private World world;
	private FixtureDef fdef;

	public WeaponSprite(World world) {
		this.world = world;
		fdef = new FixtureDef();
		bdef = new BodyDef();

		bdef.type = BodyType.StaticBody;
		body = world.createBody(bdef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(7, 7);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("weapon");
	}

	public void spawn(float x, float y, float angle) {
		body.setTransform(x, y, angle); // Spawn position
	}


	public void remove() {

	}

	public void update() {

	}

}
