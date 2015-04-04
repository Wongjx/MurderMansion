package com.jkjk.GameObjects.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.jkjk.GameObjects.Duration;
import com.jkjk.GameWorld.GameWorld;

public class DisarmTrap extends Item {

	private BodyDef bdef;
	private FixtureDef fdef;
	private Vector2 playerPosition;
	private float playerAngle;
	private Duration hitBoxExposure;

	DisarmTrap(GameWorld gWorld) {
		super(gWorld);
		bdef = new BodyDef();
		fdef = new FixtureDef();
		hitBoxExposure = new Duration(10);
	}

	@Override
	public void startUse() {
		System.out.println("Used disarm trap");

		//gWorld.getPlayer().getBody().setUserData(AssetLoader.civDisarmAnimation);
		playerPosition = gWorld.getPlayer().getBody().getPosition();
		playerAngle = gWorld.getPlayer().getBody().getAngle();
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(playerPosition.x, playerPosition.y);
		bdef.angle = playerAngle;
		body = gWorld.getWorld().createBody(bdef);

		Vector2[] vertices = { new Vector2(11, 0), new Vector2(20, 8.9f), new Vector2(28, 5.6f),
				new Vector2(32, 0), new Vector2(28, -5.6f), new Vector2(20, -8.9f) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;

		body.createFixture(fdef).setUserData("pre disarm trap");
		hitBoxExposure.startCountdown();
		
		isInterrupted = true;
		
		super.startUse();
	}

	/* (non-Javadoc)
	 * @see com.jkjk.GameObjects.Items.Item#endUse()
	 */
	@Override
	public void endUse() {

		isCompleted = false;
		//gWorld.getPlayer().getBody().setUserData(AssetLoader.civDisarmAnimation);
		playerPosition = gWorld.getPlayer().getBody().getPosition();
		playerAngle = gWorld.getPlayer().getBody().getAngle();
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(playerPosition.x, playerPosition.y);
		bdef.angle = playerAngle;
		body = gWorld.getWorld().createBody(bdef);

		Vector2[] vertices = { new Vector2(11, 0), new Vector2(20, 8.9f), new Vector2(28, 5.6f),
				new Vector2(32, 0), new Vector2(28, -5.6f), new Vector2(20, -8.9f) };
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;

		body.createFixture(fdef).setUserData("post disarm trap");
		hitBoxExposure.startCountdown();
		
	}

	@Override
	public void update() {
		super.update();
		hitBoxExposure.update();
		if (!hitBoxExposure.isCountingDown()) {
			if (body != null) {
				gWorld.getWorld().destroyBody(body);
				body = null;
				isCompleted = true;
			}
		}

	}
	
	@Override
	public void foundTrap(){
		isInterrupted = false;
	}
}
