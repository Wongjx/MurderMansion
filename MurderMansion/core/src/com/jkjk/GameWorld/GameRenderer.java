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
	private Box2DDebugRenderer b2dr;
	private HudRenderer hud;

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

		// Create camera
		cam = new OrthographicCamera();
		cam.setToOrtho(false, gameWidth, gameHeight);

		// Initialise assets
		initAssets(gameWidth, gameHeight);

		// Create player
		player = gWorld.getPlayer();
		playerBody = player.getBody();

		// Create Light for player
		rayHandler = new RayHandler(gWorld.getWorld());

		rayHandler.setAmbientLight(0.3f);

		coneLight = new ConeLight(rayHandler, 100, null, 600, 200, 200, 0, 40);
		coneLight.attachToBody(player.getBody(), -10, 0);

		tiledMap = new TmxMapLoader().load("map/mansion2.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		Box2DMapObjectParser parser = new Box2DMapObjectParser();
		parser.load(gWorld.getWorld(), tiledMap);

	}

	private void initAssets(float w, float h) {
		touchpad = AssetLoader.touchpad;

		tiledMap = AssetLoader.tiledMap;

		maxVelocity = w / 7;
	}

	public void render(float delta, float runTime) {
		System.out.println(Gdx.graphics.getFramesPerSecond());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clears screen everytime it renders

		playerMovement();

		cam.update(); // Update cam
		tiledMapRenderer.setView(cam);
		tiledMapRenderer.render();

		rayHandler.setCombinedMatrix(cam.combined);
		rayHandler.updateAndRender();

		b2dr.render(gWorld.getWorld(), cam.combined); // Renders box2d world

	}

	private void playerMovement() {
		touchpadX = touchpad.getKnobPercentX();
		touchpadY = touchpad.getKnobPercentY();
		if (!touchpad.isTouched()) {
			playerBody.setAngularVelocity(0);
		} else {
			angleDiff = (Math.atan2(touchpadY, touchpadX) - (playerBody.getAngle())) % (Math.PI * 2);
			if (angleDiff > 0) {
				if (angleDiff >= 3.14) {
					if (angleDiff > 6.2)
						playerBody.setAngularVelocity((float) -angleDiff / 7);
					else
						playerBody.setAngularVelocity(-5);
				} else if (angleDiff < 0.4)
					playerBody.setAngularVelocity((float) angleDiff * 3);
				else
					playerBody.setAngularVelocity(5);
			} else if (angleDiff < 0) {
				if (angleDiff <= -3.14) {
					if (angleDiff < -6.2)
						playerBody.setAngularVelocity((float) -angleDiff / 7);
					else
						playerBody.setAngularVelocity(5);
				} else if (angleDiff > -0.4)
					playerBody.setAngularVelocity((float) angleDiff * 3);
				else
					playerBody.setAngularVelocity(-5);
			} else
				playerBody.setAngularVelocity(0);
		}
		if (angleDiff < -6) {
			System.out.println(angleDiff);
			System.out.println(playerBody.getAngularVelocity());
		}

		playerBody.setLinearVelocity(touchpadX * maxVelocity, touchpadY * maxVelocity); // Set linearV of
																						// player

		cam.position.set(playerBody.getPosition(), 0); // Set cam position to be on player

	}

	public void rendererDispose() {
		gWorld.getWorld().dispose();
		rayHandler.dispose();
		b2dr.dispose();
	}
}
