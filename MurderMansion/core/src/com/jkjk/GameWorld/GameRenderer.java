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
	private ShapeRenderer shapeRenderer;

	private Sprite mapSprite;

	private float maxVelocity;
	private SpriteBatch batch;
	private float screenWidth;
	private float screenHeight;
	private float touchpadX;
	private float touchpadY;
	private float playerAngle;
	private double angleDiff;

	// Game Objects
	private Murderer murderer;
	private Body civilian;
	private Bat bat;
	private Trap trap;
	private DisarmTrap disarmTrap;
	private Knife knife;

	// Game Assets
	private Touchpad touchpad;
	private TouchpadStyle touchpadStyle;
	private Drawable touchBackground;
	private Drawable touchKnob;
	private Skin touchpadSkin;
	private Texture blockTexture;
	private Sprite blockSprite;
	private Sprite blockSprite2;
	private Sprite blockSprite3;

	// Buttons

	public GameRenderer(GameWorld gWorld, float screenWidth, float screenHeight) {
		this.gWorld = gWorld;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		b2dr = new Box2DDebugRenderer();
		batch = new SpriteBatch();

		// Create camera
		cam = new OrthographicCamera();
		cam.setToOrtho(false, screenWidth, screenHeight);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, screenWidth, screenHeight);

		// Initialise assets
		initAssets(screenWidth, screenHeight);

		// Create a Stage and add TouchPad
		stage = new Stage(new ExtendViewport(screenWidth, screenHeight, hudCam), batch);
		stage.addActor(touchpad);
		Gdx.input.setInputProcessor(stage);

		// Create player
		civilian = ((Civilian)gWorld.getPlayer()).getBody();
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
		cam.position.set(civilian.getPosition(), 0); // Set cam position to be on player

		playerMovement();
		
		stage.act(Gdx.graphics.getDeltaTime()); // Acts stage at deltatime
		stage.draw(); // Draw touchpad
		cam.update(); // Update cam
		b2dr.render(gWorld.getWorld(), cam.combined); // Renders box2d world
	}
	
	private void playerMovement(){
		touchpadX = touchpad.getKnobPercentX();
		touchpadY = touchpad.getKnobPercentY();
		if (touchpadX == 0) {
			civilian.setAngularVelocity(0);
		} else {
			angleDiff = (Math.atan2(touchpadY, touchpadX) - civilian.getAngle()) % (Math.PI * 2);
			if (angleDiff > 0) {
				if (angleDiff >= 3.14)
					civilian.setAngularVelocity(-5);
				else if (angleDiff < 0.07)
					civilian.setAngularVelocity(0);
				else
					civilian.setAngularVelocity(5);
			} else if (angleDiff < 0) {
				if (angleDiff <= -3.14)
					civilian.setAngularVelocity(5);
				else if (angleDiff > -0.07)
					civilian.setAngularVelocity(0);
				else
					civilian.setAngularVelocity(-5);
			} else
				civilian.setAngularVelocity(0);
		}

		civilian.setLinearVelocity(touchpad.getKnobPercentX() * maxVelocity, touchpad.getKnobPercentY()
				* maxVelocity); // Set linearV of player

	}
}
