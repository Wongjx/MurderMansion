package com.jkjk.GameObjects.Weapons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

public class Bat extends Weapon {

	private BodyDef bdef;
	private FixtureDef fdef;
	private Vector2 playerPosition;
	private float playerAngle;
	private GameCharacter character;

	Bat(GameWorld gWorld, GameCharacter character) {
		super(gWorld, character);
		this.character = character;
		bdef = new BodyDef();
		fdef = new FixtureDef();
		name = "Bat";
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(0, 0);
		body = gWorld.getWorld().createBody(bdef);
		body.setActive(false);

		Vector2[] vertices = { new Vector2(18, 0), new Vector2(34.6f, 20), new Vector2(37.6f, 13.7f),
				new Vector2(39.4f, 6.95f), new Vector2(40, 0), new Vector2(39.4f, -6.95f),
				new Vector2(37.6f, -13.7f), new Vector2(34.6f, -20) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("bat");

	}

	@Override
	public void use() {
		super.use();
		System.out.println("Used bat");
		playerPosition = character.getBody().getPosition();
		playerAngle = character.getBody().getAngle();

		body.setActive(true);
		body.setTransform(playerPosition.x, playerPosition.y, playerAngle);

		hitBoxExposure.startCountdown();
	}

}
