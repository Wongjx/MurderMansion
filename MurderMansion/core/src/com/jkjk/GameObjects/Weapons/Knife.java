package com.jkjk.GameObjects.Weapons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

public class Knife extends Weapon {

	private BodyDef bdef;
	private FixtureDef fdef;
	private Vector2 playerPosition;
	private float playerAngle;
	private GameCharacter character;

	Knife(GameWorld gWorld, GameCharacter character) {
		super(gWorld, character);
		this.character = character;
		bdef = new BodyDef();
		fdef = new FixtureDef();
		name = "Knife";
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(0, 0);
		body = gWorld.getWorld().createBody(bdef);
		body.setActive(false);

		Vector2[] vertices = { new Vector2(15, 0), new Vector2(20, 8.9f), new Vector2(28, 5.6f),
				new Vector2(32, 0), new Vector2(28, -5.6f), new Vector2(20, -8.9f) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("knife");
	}

	@Override
	public void use() {
		super.use();
		System.out.println("Used knife");
		playerPosition = character.getBody().getPosition();
		playerAngle = character.getBody().getAngle();

		body.setActive(true);
		body.setTransform(playerPosition.x, playerPosition.y, playerAngle);

		hitBoxExposure.startCountdown();
	}

}
