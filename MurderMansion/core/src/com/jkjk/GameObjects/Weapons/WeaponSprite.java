package com.jkjk.GameObjects.Weapons;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class WeaponSprite {

	private Body body;
	private FixtureDef fdef;
	
	public WeaponSprite(Body body) {
		this.body = body;
		fdef = new FixtureDef();
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(7, 7);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("weapon");
	}

	public void spawn() {

	}

	public void remove() {

	}

	public void update() {

	}

}
