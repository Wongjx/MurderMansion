/**
 * 
 */
package com.jkjk.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;

/**
 * @author LeeJunXiang
 * 
 */
public class Obstacles {

	private GameWorld gWorld;
	private Body body;
	private int type;

	/**
	 * @param gWorld
	 *            GameWorld!
	 * @param type
	 *            0 for main door, 1 for normal obstacle
	 */
	public Obstacles(GameWorld gWorld, Vector2 location, int type) {
		this.gWorld = gWorld;
		this.type = type;

		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.StaticBody;
		bdef.position.set(location);
		body = gWorld.getWorld().createBody(bdef);

		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		if (type == 0) {
			shape.setAsBox(3, 38);
			fdef.shape = shape;
			body.createFixture(fdef);
			body.setUserData(AssetLoader.main_door);
		} else {
			shape.setAsBox(15, 15);
			fdef.shape = shape;
			body.createFixture(fdef);
			body.setUserData(AssetLoader.obstacle);
		}
	}

	public Body getBody() {
		return body;
	}

	public void render(SpriteBatch batch) {
		// System.out.println((Texture) body.getUserData());
		batch.begin();
		if (type == 0)
			batch.draw((Texture) body.getUserData(), body.getPosition().x - 16, body.getPosition().y - 48);
		else
			batch.draw((Texture) body.getUserData(), body.getPosition().x - 16, body.getPosition().y - 16);
		batch.end();
	}

}
