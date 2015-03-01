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

	private float force;
	private SpriteBatch batch;
	private float screenWidth;
	private float screenHeight;

	// Game Objects
	private Murderer murderer;
	private Civilian civilian;
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

	// Tween stuff

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

	}

	private void initAssets(float w, float h) {

		// Touchpad stuff
		touchpad = AssetLoader.touchpad;
		touchpadStyle = AssetLoader.touchpadStyle;
		touchBackground = AssetLoader.touchBackground;
		touchKnob = AssetLoader.touchKnob;
		touchpadSkin = AssetLoader.touchpadSkin;
		// setBounds(x,y,width,height)
		touchpad.setBounds(w / 14, h / 14, w / 5, w / 5);
		touchKnob.setMinHeight(touchpad.getHeight() / 4);
		touchKnob.setMinWidth(touchpad.getWidth() / 4);

		// Sprite stuff
		blockTexture = AssetLoader.blockTexture;
		blockSprite = AssetLoader.blockSprite;
		blockSprite2 = AssetLoader.blockSprite2;
		blockSprite3 = AssetLoader.blockSprite3;
		// Set position to centre of the screen
		blockSprite.setBounds(w / 2 - blockSprite.getWidth() / 2, h / 2 - blockSprite.getHeight() / 2,
				w / 20, w / 20);
		blockSprite2.setBounds(w / 4 - blockSprite.getWidth() / 2, h / 2 - blockSprite.getHeight() / 2,
				w / 20, w / 20);
		blockSprite3.setBounds(3 * w / 4 - blockSprite.getWidth() / 2, h / 2 - blockSprite.getHeight() / 2,
				w / 20, w / 20);
		force = w / 250;
	}

	public void render(float delta, float runTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clears screen everytime it renders
		cam.translate(force * touchpad.getKnobPercentX(), force * touchpad.getKnobPercentY());
		cam.update();
		

		gWorld.body.setTransform(gWorld.body.getPosition().x + force * touchpad.getKnobPercentX(),
				gWorld.body.getPosition().y + force * touchpad.getKnobPercentY(), 0);
		gWorld.body.setAwake(true);
		

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		
		b2dr.render(gWorld.getWorld(), cam.combined); // Renders box2d world
	}
}
