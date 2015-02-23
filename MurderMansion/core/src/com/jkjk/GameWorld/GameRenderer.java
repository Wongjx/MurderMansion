package com.jkjk.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
	private GameWorld myWorld;
	private OrthographicCamera cam;
	private Stage stage;
	private ShapeRenderer shapeRenderer;

	static final int WORLD_WIDTH = 100;
	static final int WORLD_HEIGHT = 100;
	private Sprite mapSprite;

	private float blockSpeed;
	private SpriteBatch batch;

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

	public GameRenderer(GameWorld world) {
		myWorld = world;
		batch = new SpriteBatch();

		// Get Screen width and height
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Create camera
		cam = new OrthographicCamera(30, 30 * (h / w));
		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.update();

		// Initialise assets
		initAssets(w, h);

		// Create a Stage and add TouchPad
		stage = new Stage(new ExtendViewport(w, h, cam), batch);
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
		blockSprite.setBounds(w / 2 - blockSprite.getWidth() / 2, h / 2
				- blockSprite.getHeight() / 2, w / 20, w / 20);
		blockSprite2.setBounds(w / 4 - blockSprite.getWidth() / 2, h / 2
				- blockSprite.getHeight() / 2, w / 20, w / 20);
		blockSprite3.setBounds(3 * w / 4 - blockSprite.getWidth() / 2, h / 2
				- blockSprite.getHeight() / 2, w / 20, w / 20);
		blockSpeed = w / 200;
	}

	public void render(float delta, float runTime) {
		Gdx.gl.glClearColor(0.294f, 0.294f, 0.294f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cam.translate(touchpad.getKnobPercentX() * blockSpeed,
				touchpad.getKnobPercentY() * blockSpeed);
		cam.update();

		// Move blockSprite with TouchPad
		blockSprite.setX(blockSprite.getX() + touchpad.getKnobPercentX()
				* blockSpeed);
		blockSprite.setY(blockSprite.getY() + touchpad.getKnobPercentY()
				* blockSpeed);
		touchpad.setX(touchpad.getX() + touchpad.getKnobPercentX() * blockSpeed);
		touchpad.setY(touchpad.getY() + touchpad.getKnobPercentY() * blockSpeed);

		// Draw
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		blockSprite.draw(batch);
		blockSprite2.draw(batch);
		blockSprite3.draw(batch);
		batch.end();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

	}
}
