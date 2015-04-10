package com.jkjk.GameObjects.Characters;

import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jkjk.GameObjects.Items.ItemSprite;
import com.jkjk.GameObjects.Weapons.WeaponSprite;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.MMClient;
import com.jkjk.MMHelpers.AssetLoader;

public class Ghost extends GameCharacter {

	private PointLight pointLight;
	private GameWorld gWorld;
	private Animation currentAnimation;
	private Vector2 spawnLocation;

	Ghost(int id, GameWorld gWorld, boolean isPlayer) {
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
		// fdef.isSensor = true;
		fdef.filter.categoryBits = 5;
		fdef.filter.maskBits = 4;
		body.createFixture(fdef).setUserData("ghost");

		// create light
		pointLight = new PointLight(rayHandler, 100, null, 150, 0, 0);
		pointLight.attachToBody(body);
		pointLight.setXray(true);

		body.setUserData(AssetLoader.ghostFloatAnimation);
		setVelocity(getVelocity() / 4 * 3);
	}

	@Override
	public void render(OrthographicCamera cam, SpriteBatch batch) {
		runTime += Gdx.graphics.getRawDeltaTime();
		currentAnimation = (Animation) body.getUserData();
		batch.begin();
		batch.draw(AssetLoader.civ_dead_lines, this.get_deathPositionX() - 18,
				this.get_deathPositionY() - 18, 36, 36);
		batch.draw(AssetLoader.ghost_float, body.getPosition().x - 16, body.getPosition().y - 17, 32, 32);
		// (float) (body.getAngle()*180/Math.PI)-90);
		batch.end();

		super.render(cam, batch);

	}

	public boolean lightContains(float x, float y) {
		return pointLight.contains(x, y);
	}

	@Override
	public void useWeapon() {
		spawnLocation = new Vector2(body.getPosition().x + (float) (25f * Math.cos(body.getAngle())),
				body.getPosition().y + (float) (25f * Math.sin(body.getAngle())));
		WeaponSprite ws = new WeaponSprite(gWorld);
		gWorld.getWeaponList().put(spawnLocation, ws);
		ws.spawn(spawnLocation.x, spawnLocation.y, 0);
		gWorld.getWeaponsToAdd().add(spawnLocation);
		weapon = null;
		weaponChange = true;
	}

	@Override
	public void useItem() {
		spawnLocation = new Vector2(body.getPosition().x + (float) (25f * Math.cos(body.getAngle())),
				body.getPosition().y + (float) (25f * Math.sin(body.getAngle())));
		ItemSprite is = new ItemSprite(gWorld);
		gWorld.getItemList().put(spawnLocation, is);
		is.spawn(spawnLocation.x, spawnLocation.y, 0);
		gWorld.getItemsToAdd().add(spawnLocation);
		item = null;
		itemChange = true;
	}

}
