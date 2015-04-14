package com.jkjk.GameObjects.Weapons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

public class Shotgun extends Weapon {

	private BodyDef bdef;
	private FixtureDef fdef;
	private Vector2 playerPosition;
	private float playerAngle;
	private GameCharacter character;

	Shotgun(GameWorld gWorld, GameCharacter character) {
		super(gWorld, character);
		this.character = character;
		bdef = new BodyDef();
		fdef = new FixtureDef();
		name = "Shotgun";
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(0, 0);
		body = gWorld.getWorld().createBody(bdef);
		body.setActive(false);

		Vector2[] vertices = { new Vector2(15, 0), new Vector2(43.3f, 25), new Vector2(47, 17.1f),
				new Vector2(49.2f, 8.7f), new Vector2(50, 0), new Vector2(49.2f, -8.7f),
				new Vector2(47, -17.1f), new Vector2(43.3f, -25) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("shotgun");
	}

	@Override
	public void use() {
		super.use();
		System.out.println("Used shotgun");
		playerPosition = character.getBody().getPosition();
		playerAngle = character.getBody().getAngle();

		body.setActive(true);
		body.setTransform(playerPosition.x, playerPosition.y, playerAngle);

		hitBoxExposure.startCountdown();
	}

}
