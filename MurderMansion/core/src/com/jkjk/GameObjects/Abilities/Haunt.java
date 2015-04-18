package com.jkjk.GameObjects.Abilities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

public class Haunt extends Ability {

	private GameCharacter character;
	private Body body;
	private BodyDef bdef;
	private FixtureDef fdef;
	private Vector2 playerPosition;
	private float playerAngle;
	private Duration hitBoxExposure;

	Haunt(GameWorld gWorld, GameCharacter character) {
		super(character);
		this.character = character;
		bdef = new BodyDef();
		fdef = new FixtureDef();
		cooldown = new Duration(20000);
		hitBoxExposure = new Duration(50);

		bdef.type = BodyType.DynamicBody;
		bdef.position.set(0, 0);
		body = gWorld.getWorld().createBody(bdef);
		body.setActive(false);

		CircleShape shape = new CircleShape();
		shape.setRadius(25);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("haunt");
	}

	@Override
	public void use() {
		System.out.println("Used haunt");
		playerPosition = character.getBody().getPosition();
		playerAngle = character.getBody().getAngle();

		body.setActive(true);
		body.setTransform(playerPosition.x, playerPosition.y, playerAngle);

		hitBoxExposure.startCountdown();
		AssetLoader.hauntSFX();
	}

	@Override
	public void cooldown() {
		cooldown.startCountdown();
	}

	@Override
	public void update() {
		super.update();
		hitBoxExposure.update();
		if (!hitBoxExposure.isCountingDown()) {
			if (body.isActive()) {
				body.setActive(false);
				body.setTransform(0, 0, 0);
			}
		}
	}
}
