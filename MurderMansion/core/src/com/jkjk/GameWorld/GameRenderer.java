package com.jkjk.GameWorld;

import java.util.List;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import box2dLight.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jkjk.GameObjects.Characters.GameCharacter;
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
	private TiledMap tiledMap;
	private TiledMapRenderer tiledMapRenderer;

	// Buttons

	// Lights
	private RayHandler rayHandler;
	private ConeLight coneLight;
	
	
	public GameRenderer(GameWorld gWorld, float gameWidth, float gameHeight) {
		this.gWorld = gWorld;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		b2dr = new Box2DDebugRenderer();
		batch = new SpriteBatch();
		hud = new HUD(gWorld);

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
		stage.addActor(hud.getEmptyItemSlot());
		stage.addActor(hud.getEmptyWeaponSlot());
		Gdx.input.setInputProcessor(stage);

		// Create player
		player = gWorld.getPlayer();
		playerBody = player.getBody();

		// Create Light for player
		rayHandler = new RayHandler(gWorld.getWorld());

		rayHandler.setAmbientLight(0.5f);
		
		coneLight = new ConeLight(rayHandler, 5000, null, 600, 200, 200, 0, 40);
		coneLight.attachToBody(player.getBody(), -10, 0);

		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

	}

	private void initAssets(float w, float h) {

		// Touchpad stuff
		touchpad = AssetLoader.touchpad;
		touchpad.setName("touchpad");
		touchpad.setBounds(w / 14, h / 14, w / 5, w / 5);
		touchKnob = AssetLoader.touchKnob;
		touchKnob.setMinHeight(touchpad.getHeight() / 4);
		touchKnob.setMinWidth(touchpad.getWidth() / 4);
		
		tiledMap = AssetLoader.tiledMap;

		maxVelocity = w / 7;
	}

	public void render(float delta, float runTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clears screen everytime it renders

		playerMovement();
		if (player.getItemChange())
			itemCheck();
		if (player.getWeaponChange())
			weaponCheck();

		cam.update(); // Update cam
		tiledMapRenderer.setView(cam);
		tiledMapRenderer.render();

		rayHandler.setCombinedMatrix(cam.combined);
		rayHandler.updateAndRender();

		rayHandler.setCombinedMatrix(cam.combined);
		rayHandler.updateAndRender();

		b2dr.render(gWorld.getWorld(), cam.combined); // Renders box2d world
		stage.draw(); // Draw touchpad
		stage.act(Gdx.graphics.getDeltaTime()); // Acts stage at deltatime

	}

	private void itemCheck() {
		player.setItemChange(false);
		if (player.getItem() != null) {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Empty Item Slot"))
					actors.remove();
			}
			if (player.getName().equals("Civilian"))
				stage.addActor(hud.getDisarmTrap());
			else if (player.getName().equals("Murderer"))
				stage.addActor(hud.getTrap());
		} else {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Item Button"))
					actors.remove();
			}
			stage.addActor(hud.getEmptyItemSlot());
		}
	}

	private void weaponCheck() {
		player.setWeaponChange(false);
		if (player.getWeapon() != null) {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Empty Weapon Slot"))
					actors.remove();
			}
			if (player.getName().equals("Civilian"))
				stage.addActor(hud.getBat());
			else if (player.getName().equals("Murderer"))
				stage.addActor(hud.getKnife());
		} else {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Weapon Button"))
					actors.remove();
			}
			stage.addActor(hud.getEmptyWeaponSlot());
		}
	}

	private void playerMovement() {
		touchpadX = touchpad.getKnobPercentX();
		touchpadY = touchpad.getKnobPercentY();
		if (!touchpad.isTouched()) {
			playerBody.setAngularVelocity(0);
		} else {
			angleDiff = (Math.atan2(touchpadY, touchpadX) - playerBody.getAngle()) % (Math.PI * 2);
			if (angleDiff > 0.05) {
				if (angleDiff >= 3.14)
					playerBody.setAngularVelocity(-5);
				else if (angleDiff < 0.4)
					playerBody.setAngularVelocity(1);
				else
					playerBody.setAngularVelocity(5);
			} else if (angleDiff < -0.05) {
				if (angleDiff <= -3.14)
					playerBody.setAngularVelocity(5);
				else if (angleDiff > -0.4)
					playerBody.setAngularVelocity(-1);
				else
					playerBody.setAngularVelocity(-5);
			} else
				playerBody.setAngularVelocity(0);
		}

		playerBody.setLinearVelocity(touchpadX * maxVelocity, touchpadY * maxVelocity); // Set linearV of
																						// player

		cam.position.set(playerBody.getPosition(), 0); // Set cam position to be on player

	}

	public void rendererDispose() {
		System.out.println("Disposing!");
		gWorld.getWorld().dispose();
		rayHandler.dispose();
		stage.dispose();
		b2dr.dispose();
	}
}
