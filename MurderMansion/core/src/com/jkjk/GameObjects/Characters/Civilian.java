package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.GameWorld.GameWorld;

public class Civilian extends GameCharacter {

	private FixtureDef fdef;
	private BodyDef bdef;
	private Body body;
	private World world;

	Civilian(int colour, World world) {
		this.world = world;
		fdef = new FixtureDef();
		bdef = new BodyDef();
		setName("Civilian");
		setColour(colour);

		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		Vector2[] vertices = { new Vector2(0, 0), new Vector2(-20, -10), new Vector2(-20, 10) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;

		body.createFixture(fdef).setUserData("civilian");
		setBody(body);
	}

	@Override
	public void spawn(float x, float y, float angle) {
		alive = true;
		body.setTransform(x, y, angle); // Spawn position
	}

	public void die() {
		alive = false;
	}
}
