package com.jkjk.GameObjects.Items;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class ItemSprite {

	private Body body;
	private FixtureDef fdef;
	
	public ItemSprite(Body body) {
		this.body = body;
		fdef = new FixtureDef();
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(7, 7);
		fdef.shape = shape;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("item");
	}

	public void spawn() {

	}

	public void remove() {

	}

	public void update() {

	}

}
