package com.jkjk.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jkjk.GameObjects.Characters.Civilian;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.Murderer;
import com.jkjk.GameObjects.Items.DisarmTrap;
import com.jkjk.GameObjects.Items.Trap;
import com.jkjk.GameObjects.Weapons.Bat;
import com.jkjk.GameObjects.Weapons.Knife;
import com.jkjk.MMHelpers.AssetLoader;

public class GameRenderer {
	private GameWorld gWorld;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;
	private Box2DDebugRenderer b2dr;
	private Stage stage;
	private HUD hud;

	private SpriteBatch batch;
	private Sprite mapSprite;

	private float maxVelocity;
	private float gameWidth;
	private float gameHeight;
	private float touchpadX;
	private float touchpadY;
	private float playerAngle;
	private double angleDiff;

	// Game Objects
	private Body playerBody;
	private GameCharacter player;
	private Bat bat;
	private Trap trap;
	private DisarmTrap disarmTrap;
	private Knife knife;

	// Game Assets
	private Touchpad touchpad;
	private Drawable touchKnob;

	// Buttons

	public GameRenderer(GameWorld gWorld, float gameWidth, float gameHeight) {
		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		this.gWorld = gWorld;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		b2dr = new Box2DDebugRenderer();
		batch = new SpriteBatch();
		hud = new HUD(gWorld, screenWidth / gameWidth, screenHeight / gameHeight);

		// Create camera
		cam = new OrthographicCamera();
		cam.setToOrtho(false, gameWidth, gameHeight);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, gameWidth, gameHeight);

		// Initialise assets
		initAssets(gameWidth, gameHeight);

		// Create a Stage and add TouchPad
		stage = new Stage(new ExtendViewport(gameWidth, gameHeight, hudCam), batch);
		stage.addActor(touchpad);
		Gdx.input.setInputProcessor(stage);

		// Create player
		player = gWorld.getPlayer();
		playerBody = player.getBody();
	}

	private void initAssets(float w, float h) {

		// Touchpad stuff
		touchpad = AssetLoader.touchpad;
		touchpad.setBounds(w / 14, h / 14, w / 5, w / 5);
		touchKnob = AssetLoader.touchKnob;
		touchKnob.setMinHeight(touchpad.getHeight() / 4);
		touchKnob.setMinWidth(touchpad.getWidth() / 4);

		maxVelocity = w / 7;
	}

	public void render(float delta, float runTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clears screen everytime it renders
		cam.position.set(playerBody.getPosition(), 0); // Set cam position to be on player

		playerMovement();

		stage.act(Gdx.graphics.getDeltaTime()); // Acts stage at deltatime
		stage.draw(); // Draw touchpad
		cam.update(); // Update cam
		b2dr.render(gWorld.getWorld(), cam.combined); // Renders box2d world

		batch.begin();
		batch.setProjectionMatrix(hudCam.combined);
		itemCheck(batch);
		batch.end();
	}

	private void itemCheck(SpriteBatch sb) {
		if (player.getName().equals("Civilian")) {
			if (player.getItem() != null)
				hud.drawDisarmTrap(sb);
			else
				hud.drawEmptyItemSlot(sb);
			if (player.getWeapon() != null)
				hud.drawBat(sb);
			else
				hud.drawEmptyWeaponSlot(sb);
		} else if (player.getName().equals("Murderer")) {
			if (player.getItem() != null)
				hud.drawTrap(sb);
			else
				hud.drawEmptyItemSlot(sb);
			if (player.getWeapon() != null)
				hud.drawKnife(sb);
			else
				hud.drawEmptyWeaponSlot(sb);
		}
	}

	private void playerMovement() {
		touchpadX = touchpad.getKnobPercentX();
		touchpadY = touchpad.getKnobPercentY();
		if (touchpadX == 0) {
			playerBody.setAngularVelocity(0);
		} else {
			angleDiff = (Math.atan2(touchpadY, touchpadX) - playerBody.getAngle()) % (Math.PI * 2);
			if (angleDiff > 0) {
				if (angleDiff >= 3.14)
					playerBody.setAngularVelocity(-5);
				else if (angleDiff < 0.07)
					playerBody.setAngularVelocity(0);
				else
					playerBody.setAngularVelocity(5);
			} else if (angleDiff < 0) {
				if (angleDiff <= -3.14)
					playerBody.setAngularVelocity(5);
				else if (angleDiff > -0.07)
					playerBody.setAngularVelocity(0);
				else
					playerBody.setAngularVelocity(-5);
			} else
				playerBody.setAngularVelocity(0);
		}

		playerBody.setLinearVelocity(touchpad.getKnobPercentX() * maxVelocity, touchpad.getKnobPercentY()
				* maxVelocity); // Set linearV of player

	}
}
