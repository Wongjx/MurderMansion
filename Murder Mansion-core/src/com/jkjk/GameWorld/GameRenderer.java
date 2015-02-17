package com.jkjk.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.GL10;
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
import com.jkjk.GameObjects.Bat;
import com.jkjk.GameObjects.Civilian;
import com.jkjk.GameObjects.DisarmTrap;
import com.jkjk.GameObjects.Knife;
import com.jkjk.GameObjects.Murderer;
import com.jkjk.GameObjects.Trap;
import com.jkjk.MMHelpers.AssetLoader;

public class GameRenderer {
	private GameWorld myWorld;
	private OrthographicCamera cam;
	private Stage stage;
	private ShapeRenderer shapeRenderer;

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
		
		// Create camera
		float aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		initAssets();

		// setBounds(x,y,width,height)
		touchpad.setBounds(70, 70, 350, 350);
		
		batch = new SpriteBatch();
		
		// Create a Stage and add TouchPad
		stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cam), batch);
		stage.addActor(touchpad);
		Gdx.input.setInputProcessor(stage);
		
		// Set position to centre of the screen
		blockSprite.setPosition(Gdx.graphics.getWidth() / 2 - blockSprite.getWidth() / 2,
				Gdx.graphics.getHeight() / 2 - blockSprite.getHeight() / 2);
		blockSprite2.setPosition(500, 500);
		blockSprite3.setPosition(1000, 500);
		blockSpeed = 5;
	}
	
	private void initAssets() {
		touchpad = AssetLoader.touchpad;
		touchpadStyle = AssetLoader.touchpadStyle;
		touchBackground = AssetLoader.touchBackground;
		touchKnob = AssetLoader.touchKnob;
		touchpadSkin = AssetLoader.touchpadSkin;
		blockTexture = AssetLoader.blockTexture;
		blockSprite = AssetLoader.blockSprite;
		blockSprite2 = AssetLoader.blockSprite2;
		blockSprite3 = AssetLoader.blockSprite3;
	}
	
	public void render(float delta, float runTime) {
		Gdx.gl.glClearColor(0.294f, 0.294f, 0.294f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		cam.translate(touchpad.getKnobPercentX() * blockSpeed, touchpad.getKnobPercentY() * blockSpeed);
		cam.update();

		// Move blockSprite with TouchPad
		blockSprite.setX(blockSprite.getX() + touchpad.getKnobPercentX() * blockSpeed);
		blockSprite.setY(blockSprite.getY() + touchpad.getKnobPercentY() * blockSpeed);
		touchpad.setX(touchpad.getX() + touchpad.getKnobPercentX() * blockSpeed);
		touchpad.setY(touchpad.getY() + touchpad.getKnobPercentY() * blockSpeed);

		// Draw
		batch.begin();
		blockSprite.draw(batch);
		blockSprite2.draw(batch);
		blockSprite3.draw(batch);
		batch.end();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		

	}
}