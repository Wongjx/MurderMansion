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
import com.jkjk.GameWorld.MMClient;
import com.jkjk.MMHelpers.AssetLoader;

public class Trap extends Item {
	public MMClient client;

	private BodyDef bdef;
	private Body body;
	private FixtureDef fdef;
	private Vector2 playerPosition;
	private float playerAngle;
	private Animation plantedTrapAnimation;
	private float animationRunTime;

	public Trap(GameWorld gWorld, MMClient client) {
		super(gWorld);
		this.client = client;
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

		spawn(playerPosition.x, playerPosition.y, playerAngle);

		isCompleted = true;
	}

	public void spawn(float x, float y, float angle) {
		Gdx.app.postRunnable(new spawnRun(this,x,y,angle));
		
//		bdef.type = BodyType.StaticBody;
//		bdef.position.set(x, y);
//		body = gWorld.getWorld().createBody(bdef);
//
//		CircleShape shape = new CircleShape();
//		shape.setRadius(10);
//		if (angle != 0)
//			shape.setPosition(new Vector2((float) (25f * Math.cos(angle)), (float) (25f * Math.sin(angle))));
//		fdef.shape = shape;
//		fdef.isSensor = true;
//		fdef.filter.maskBits = 1;
//
//		body.createFixture(fdef).setUserData("trap");
//		client.produceTrapLocation(body.getPosition().x,body.getPosition().y);
//		gWorld.getTrapList().put(body.getPosition(), this);
	}

	public void render(SpriteBatch batch) {
		if (gWorld.getPlayer().lightContains(body.getPosition().x, body.getPosition().y)) {
			animationRunTime += Gdx.graphics.getRawDeltaTime();
			batch.draw(plantedTrapAnimation.getKeyFrame(animationRunTime), body.getPosition().x,
					body.getPosition().y, 32, 32);
		}
	}

	public BodyDef getBdef() {
		return bdef;
	}

	public void setBdef(BodyDef bdef) {
		this.bdef = bdef;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public FixtureDef getFdef() {
		return fdef;
	}

	public void setFdef(FixtureDef fdef) {
		this.fdef = fdef;
	}
}


class spawnRun implements Runnable{
	private Trap trap;
	private BodyDef bdef;
	private Body body;
	private FixtureDef fdef;
	private float x;
	private float y;
	private float angle;
	
	spawnRun(Trap trap, float x, float y, float angle){
		this.trap=trap;
		this.bdef=trap.getBdef();
		this.fdef=trap.getFdef();
		this.body=trap.getBody();
		this.x=x;
		this.y=y;
		this.angle=angle;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		bdef.type = BodyType.StaticBody;
		bdef.position.set(x, y);
		body = trap.gWorld.getWorld().createBody(bdef);

		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		if (angle != 0)
			shape.setPosition(new Vector2((float) (25f * Math.cos(angle)), (float) (25f * Math.sin(angle))));
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;

		body.createFixture(fdef).setUserData("trap");
		trap.client.produceTrapLocation(body.getPosition().x,body.getPosition().y);
		trap.gWorld.getTrapList().put(body.getPosition(), trap);
	}
	
}
