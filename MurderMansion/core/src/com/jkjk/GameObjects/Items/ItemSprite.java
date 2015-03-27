package com.jkjk.GameObjects.Items;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.jkjk.MMHelpers.AssetLoader;

public class ItemSprite {

	private Body body;
	private BodyDef bdef;
	private World world;
	private FixtureDef fdef;
	
	public ItemSprite(World world) {
		this.world = world;
		fdef = new FixtureDef();
		bdef = new BodyDef();
		

		bdef.type = BodyType.StaticBody;
		body = world.createBody(bdef);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(9);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("item");
		body.setUserData(AssetLoader.itemTexture);
	
	}

	public void spawn(float x, float y, float angle) {
		body.setTransform(x, y, angle); // Spawn position
	}

	public void render() {

	}

}
