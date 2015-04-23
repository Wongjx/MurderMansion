package com.jkjk.GameObjects.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameWorld;

public class DisarmTrap extends Item {

	private BodyDef prebdef;
	private FixtureDef prefdef;
	private BodyDef postbdef;
	private FixtureDef postfdef;

	private Body preDisarmBody;
	private Body postDisarmBody;
	private Vector2 playerPosition;
	private float playerAngle;
	private Duration hitBoxExposure;
	private GameCharacter character;

	DisarmTrap(GameWorld gWorld, GameCharacter character) {
		super(gWorld, character);
		this.character = character;
		prebdef = new BodyDef();
		prefdef = new FixtureDef();
		hitBoxExposure = new Duration(50);

		// Create pre disarm trap
		prebdef.type = BodyType.DynamicBody;
		prebdef.position.set(0, 0);
		preDisarmBody = gWorld.getWorld().createBody(prebdef);
		preDisarmBody.setActive(false);

		Vector2[] vertices = { new Vector2(11, 0), new Vector2(20, 8.9f), new Vector2(28, 5.6f),
				new Vector2(32, 0), new Vector2(28, -5.6f), new Vector2(20, -8.9f) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		prefdef.shape = shape;
		prefdef.isSensor = true;
		prefdef.filter.maskBits = 1;

		preDisarmBody.createFixture(prefdef).setUserData("pre disarm trap");

		// Create post disarm trap
		postbdef = new BodyDef();
		postfdef = new FixtureDef();
		postbdef.type = BodyType.DynamicBody;
		postbdef.position.set(0, 0);
		postDisarmBody = gWorld.getWorld().createBody(postbdef);
		postDisarmBody.setActive(false);

		postfdef.shape = shape;
		postfdef.isSensor = true;
		postfdef.filter.maskBits = 1;

		postDisarmBody.createFixture(postfdef).setUserData("post disarm trap");
	}

	@Override
	public void startUse() {
		System.out.println("Used disarm trap");
		playerPosition = character.getBody().getPosition();
		playerAngle = character.getBody().getAngle();

		preDisarmBody.setActive(true);
		preDisarmBody.setTransform(playerPosition.x, playerPosition.y, playerAngle);

		hitBoxExposure.startCountdown();

		isInterrupted = true;

		super.startUse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jkjk.GameObjects.Items.Item#endUse()
	 */
	@Override
	public void endUse() {

		isCompleted = false;
		// gWorld.getPlayer().getBody().setUserData(AssetLoader.civDisarmAnimation);
		playerPosition = character.getBody().getPosition();
		playerAngle = character.getBody().getAngle();

		postDisarmBody.setActive(true);
		postDisarmBody.setTransform(playerPosition.x, playerPosition.y, playerAngle);

		hitBoxExposure.startCountdown();
		super.endUse();
	}

	@Override
	public void update() {
		super.update();
		hitBoxExposure.update();
		if (!hitBoxExposure.isCountingDown()) {
			if (preDisarmBody.isActive()) {
				preDisarmBody.setActive(false);
				preDisarmBody.setTransform(0, 0, 0);
				isCompleted = true;
			}
			if (postDisarmBody.isActive()) {
				postDisarmBody.setActive(false);
				postDisarmBody.setTransform(0, 0, 0);
				isCompleted = true;
			}
		}

	}

	public void foundTrap() {
		isInterrupted = false;
	}
}
