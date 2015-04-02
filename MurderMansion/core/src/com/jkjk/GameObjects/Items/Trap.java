package com.jkjk.GameObjects.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

public class Trap extends Item {

	private BodyDef bdef;
	private Body body;
	private FixtureDef fdef;
	private Vector2 playerPosition;
	private float playerAngle;
	private Animation plantedTrapAnimation;
	private float animationRunTime;

	public Trap(GameWorld gWorld) {
		super(gWorld);
		bdef = new BodyDef();
		fdef = new FixtureDef();
		
		plantedTrapAnimation = AssetLoader.plantedTrapAnimation;
		animationRunTime = 0;
	}

	@Override
	public void startUse() {
		System.out.println("Used trap");

		super.startUse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jkjk.GameObjects.Items.Item#endUse()
	 */
	@Override
	public void endUse() {

		// gWorld.getPlayer().getBody().setUserData(AssetLoader.murPlantTrapAnimation);
		playerPosition = gWorld.getPlayer().getBody().getPosition();
		playerAngle = gWorld.getPlayer().getBody().getAngle();
		bdef.type = BodyType.StaticBody;
		bdef.position.set(playerPosition.x, playerPosition.y);
		body = gWorld.getWorld().createBody(bdef);

		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		shape.setPosition(new Vector2((float) (25f * Math.cos(playerAngle)), (float) (25f * Math
				.sin(playerAngle))));
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;

		body.createFixture(fdef).setUserData("trap");
		gWorld.getTrapList().put(body.getPosition(), this);
		isCompleted = true;
	}

	public void render(SpriteBatch batch) {
		if (gWorld.getPlayer().lightContains(body.getPosition().x, body.getPosition().y)) {
			batch.begin();
			animationRunTime += Gdx.graphics.getRawDeltaTime();
			batch.draw(plantedTrapAnimation.getKeyFrame(animationRunTime), body.getPosition().x, body.getPosition().y, 32, 32);
			batch.end();
		}
	}

}
