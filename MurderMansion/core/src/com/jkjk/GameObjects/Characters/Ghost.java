package com.jkjk.GameObjects.Characters;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
<<<<<<< HEAD
=======
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.GameWorld.GameWorld;
>>>>>>> e4f585b1c9b22bf7b005b2698f0e0befc02f2a78
import com.jkjk.MMHelpers.AssetLoader;

public class Ghost extends GameCharacter {

	private PointLight pointLight;
	private Texture civ_dead_lines;
	private Animation charAnim;
<<<<<<< HEAD
	private SpriteBatch batch;
	
	public Ghost(int id, World world) {
		super("Ghost", id, world);
=======
	private GameWorld gWorld;
>>>>>>> e4f585b1c9b22bf7b005b2698f0e0befc02f2a78

	public Ghost(int id, GameWorld gWorld, boolean isPlayer) {
		super("Ghost", id, gWorld, isPlayer);

		this.gWorld = gWorld;
		// create body of murderer
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		body = gWorld.getWorld().createBody(bdef);

		// triangular body fixture
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.maskBits = 1;
		body.createFixture(fdef).setUserData("ghost");

		// create light
		pointLight = new PointLight(rayHandler, 100, null, 100, 0, 0);
		pointLight.attachToBody(body);
		PointLight.setContactFilter((short) 2, (short) 2, (short) 0);

		// light fixture
		/*
		 * FixtureDef lightFdef = new FixtureDef(); CircleShape circle = new CircleShape(); lightFdef.isSensor
		 * = true; circle.setPosition(getBody().getPosition()); circle.setRadius(100); lightFdef.shape =
		 * circle; lightFdef.filter.maskBits = 1; body.createFixture(lightFdef).setUserData("lightBody");
		 */

		civ_dead_lines = AssetLoader.civ_dead_lines;

		charAnim = AssetLoader.civAnimation;
		body.setUserData(charAnim);
	}

	@Override
	public void render(OrthographicCamera cam) {

		batch.setProjectionMatrix(cam.combined);

		batch.begin();
		batch.draw(civ_dead_lines, this.get_deathPositionX() - 33 / 2, this.get_deathPositionY() - 32 / 2,
				33, 32);
		batch.end();

		super.render(cam);
<<<<<<< HEAD

=======
>>>>>>> e4f585b1c9b22bf7b005b2698f0e0befc02f2a78
	}

	public boolean lightContains(float x, float y) {
		return pointLight.contains(x, y);
	}

}
