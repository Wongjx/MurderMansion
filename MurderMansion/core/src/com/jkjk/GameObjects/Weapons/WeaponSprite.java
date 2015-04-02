package com.jkjk.GameObjects.Weapons;

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

public class WeaponSprite {

	private Body body;
	private BodyDef bdef;
	private GameWorld gWorld;
	private FixtureDef fdef;
	private float posX, posY;
	private Animation batSpriteAnimation;
	private Animation knifeSpriteAnimation;
	private float animationRunTime;

	public WeaponSprite(GameWorld gWorld) {
		this.gWorld = gWorld;
		fdef = new FixtureDef();
		bdef = new BodyDef();

		bdef.type = BodyType.StaticBody;
		body = gWorld.getWorld().createBody(bdef);

		CircleShape shape = new CircleShape();
		shape.setRadius(9);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("weapon");
		
		batSpriteAnimation = AssetLoader.batSpriteAnimation;
		knifeSpriteAnimation = AssetLoader.knifeSpriteAnimation;
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
			batch.begin();
			if (gWorld.getPlayer().getType().equals("Murderer")){
				animationRunTime += Gdx.graphics.getRawDeltaTime();
				batch.draw(knifeSpriteAnimation.getKeyFrame(animationRunTime), posX-12, posY-12, 24, 24);
			}else{
				animationRunTime += Gdx.graphics.getRawDeltaTime();
				batch.draw(batSpriteAnimation.getKeyFrame(animationRunTime), posX-12, posY-12, 24, 24);
			}
			batch.end();
		}
	}

}
