package com.jkjk.GameObjects.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.MMClient;
import com.jkjk.MMHelpers.AssetLoader;


public class Trap extends Item  {
	public MMClient client;

	private BodyDef bdef;
	private Body body;
	private FixtureDef fdef;
	private Vector2 playerPosition;
	private float playerAngle;
	private Animation plantedTrapAnimation;
	private float animationRunTime;
	private GameCharacter character;
	
	private Sound plantTrapSound;

	public Trap(GameWorld gWorld, MMClient client, GameCharacter character) {
		super(gWorld, character);
		this.character = character;
		this.client = client;
		bdef = new BodyDef();
		fdef = new FixtureDef();

		plantedTrapAnimation = AssetLoader.plantedTrapAnimation;
		animationRunTime = 0;
		
		plantTrapSound = AssetLoader.plantTrapSound;
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
		playerPosition = character.getBody().getPosition();
		playerAngle = character.getBody().getAngle();

		spawn(playerPosition.x, playerPosition.y, playerAngle);
		plantTrapSound.play();

		client.produceTrapLocation(body.getPosition().x,body.getPosition().y);

		isCompleted = true;
	}

	public void spawn(float x, float y, float angle) {
//		Gdx.app.postRunnable(new spawnRun(this,x,y,angle));
		
		bdef.type = BodyType.StaticBody;
		if (angle == 0) {
			bdef.position.set(x, y);
		} else {
			bdef.position.set(x + (float) (25f * Math.cos(angle)), y + (float) (25f * Math.sin(angle)));
		}

		body = gWorld.getWorld().createBody(bdef);

		CircleShape shape = new CircleShape();
		shape.setRadius(10);

		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;

		body.createFixture(fdef).setUserData("trap");
		gWorld.getTrapList().put(body.getPosition(), this);
	}

	public void render(SpriteBatch batch) {
		if (gWorld.getPlayer().lightContains(body.getPosition().x, body.getPosition().y)) {
			animationRunTime += Gdx.graphics.getRawDeltaTime();
			batch.draw(plantedTrapAnimation.getKeyFrame(animationRunTime), body.getPosition().x-16,
					body.getPosition().y-16, 32, 32);
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
