package com.jkjk.GameObjects.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

public class ItemSprite {

	private Body body;
	private BodyDef bdef;
	private GameWorld gWorld;
	private FixtureDef fdef;
	private float posX, posY;
	private Animation restingTrapAnimation;
	private float animationRunTime;
	private Animation disarmTrapSpriteAnimation;

	public ItemSprite(GameWorld gWorld) {
		this.gWorld = gWorld;
		fdef = new FixtureDef();
		bdef = new BodyDef();

		bdef.type = BodyType.StaticBody;
		body = gWorld.getWorld().createBody(bdef);

		CircleShape shape = new CircleShape();
		shape.setRadius(9);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.categoryBits = 5;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("item");
		
		restingTrapAnimation = AssetLoader.restingTrapAnimation;
		disarmTrapSpriteAnimation = AssetLoader.disarmTrapSpriteAnimation;
		animationRunTime = 0;

	}

	public void spawn(float x, float y, float angle) {
		body.setTransform(x, y, angle); // Spawn position
		posX = x;
		posY = y;
	}

	public float[] getLocation() {
		return new float[] { posX, posY };
	}

	public void render(SpriteBatch batch) {
		if (gWorld.getPlayer().lightContains(posX, posY)) {
			if (gWorld.getPlayer().getType().equals("Murderer")){
				animationRunTime += Gdx.graphics.getRawDeltaTime();
				batch.draw(restingTrapAnimation.getKeyFrame(animationRunTime), posX-12, posY-12, 25, 25);
			}else{
				animationRunTime += Gdx.graphics.getRawDeltaTime();
				batch.draw(disarmTrapSpriteAnimation.getKeyFrame(animationRunTime), posX-12, posY-12, 25, 25);
			}
		}
	}
	
	public Body getBody(){
		return body;
	}

}
