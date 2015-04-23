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
import com.jkjk.MMHelpers.AssetLoader;

public class Ghost extends GameCharacter {

	private PointLight pointLight;
	private GameWorld gWorld;
	private Animation currentAnimation;
	private Vector2 spawnLocation;

	private Animation ghostHauntAnimation;
	private float animationRunTime;

	Ghost(int id, GameWorld gWorld, boolean isPlayer) {
		super("Ghost", id, gWorld, isPlayer);

		this.gWorld = gWorld;
		// create body of murderer
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		body = gWorld.getWorld().createBody(bdef);

		// circular body fixture
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

		ghostHauntAnimation = AssetLoader.ghostHauntAnimation;
		animationRunTime = 0;
	}

	@Override
	public void render(OrthographicCamera cam, SpriteBatch batch) {
		runTime += Gdx.graphics.getRawDeltaTime();
		currentAnimation = (Animation) body.getUserData();
		batch.begin();
		if (currentAnimation == ghostHauntAnimation) {
			animationRunTime += Gdx.graphics.getRawDeltaTime();
			batch.draw(currentAnimation.getKeyFrame(runTime, true), body.getPosition().x - 9,
					body.getPosition().y - 9, 9, 9, 18, 18, 2.4f, 2.4f,
					(float) (body.getAngle() * 180 / Math.PI) - 90);
			if (currentAnimation.isAnimationFinished(animationRunTime)) {
				animationRunTime = 0;
				body.setUserData(AssetLoader.ghostFloatAnimation);
			}
		} else {
			batch.draw(AssetLoader.ghost_float, body.getPosition().x - 9, body.getPosition().y - 9, 9, 9, 18,
					18, 1, 1, (float) (body.getAngle() * 180 / Math.PI) - 90, 0, 0, 44, 44, false, false);
			// (float) (body.getAngle()*180/Math.PI)-90);
		}
		batch.draw(AssetLoader.civ_dead_lines, this.get_deathPositionX() - 18,
				this.get_deathPositionY() - 18, 36, 36);
		batch.end();

		super.render(cam, batch);

	}

	@Override
	public boolean useAbility() {
		if (!ability.isOnCoolDown()) {
			super.useAbility();
			body.setUserData(ghostHauntAnimation);
			return true;
		}
		return false;

	}

	@Override
	public boolean useWeapon() {
		spawnLocation = new Vector2(body.getPosition().x + (float) (25f * Math.cos(body.getAngle())),
				body.getPosition().y + (float) (25f * Math.sin(body.getAngle())));
		WeaponSprite ws = new WeaponSprite(gWorld);
		gWorld.getWeaponList().put(spawnLocation, ws);
		ws.spawn(spawnLocation.x, spawnLocation.y, 0);
		weapon = null;
		weaponChange = true;
		return true;
	}

	@Override
	public void useItem() {
		spawnLocation = new Vector2(body.getPosition().x + (float) (25f * Math.cos(body.getAngle())),
				body.getPosition().y + (float) (25f * Math.sin(body.getAngle())));
		ItemSprite is = new ItemSprite(gWorld);
		gWorld.getItemList().put(spawnLocation, is);
		is.spawn(spawnLocation.x, spawnLocation.y, 0);
		item = null;
		itemChange = true;
	}

}
