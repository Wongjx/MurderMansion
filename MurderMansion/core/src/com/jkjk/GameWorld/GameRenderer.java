package com.jkjk.GameWorld;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.MMHelpers.AssetLoader;

public class GameRenderer {
	private GameWorld gWorld;
	private OrthographicCamera cam;
	private Box2DDebugRenderer b2dr;
	private float maxVelocity;
	private float touchpadX;
	private float touchpadY;
	private double angleDiff;

	// Game Objects
	private Body playerBody;
	private GameCharacter player;
	// Game Assets
	private Touchpad touchpad;
	private TiledMap tiledMap;
	private TiledMapRenderer tiledMapRenderer;

	// Buttons

	// Lights
	private RayHandler rayHandler;
	private ConeLight coneLight;
	private PointLight pointLight;

	public GameRenderer(GameWorld gWorld, float gameWidth, float gameHeight) {
		this.gWorld = gWorld;
		b2dr = new Box2DDebugRenderer();

		// Create camera
		cam = new OrthographicCamera();
		cam.setToOrtho(false, (float) (gameWidth / (4.0 / 3)), (float) (gameHeight / (4.0 / 3)));

		// Initialise assets
		initAssets(gameWidth, gameHeight);

		// Create player
		player = gWorld.getPlayer();
		playerBody = player.getBody();

		// Create Light for player
		rayHandler = new RayHandler(gWorld.getWorld());
		rayHandler.setAmbientLight(0.12f);
		if (player.getName().equals("Civilian")) {
			coneLight = new ConeLight(rayHandler, 100, null, 200, 0, 0, 0, 40);
			coneLight.attachToBody(player.getBody(), -10, 0);
			ConeLight.setContactFilter((short) 2, (short) 2, (short) 1);
		} else if (player.getName().equals("Murderer")) {
			pointLight = new PointLight(rayHandler, 100, null, 150, 0, 0);
			pointLight.attachToBody(player.getBody());
			PointLight.setContactFilter((short) 2, (short) 2, (short) 1);
		}

		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

	}

	private void initAssets(float w, float h) {
		touchpad = AssetLoader.touchpad;

		tiledMap = AssetLoader.tiledMap;

		maxVelocity = w / 10;
	}

	public void render(float delta, float runTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clears screen everytime it renders

		playerMovement();

		// Vector2 v2 = player.getBody().getPosition();
		// player.getLightBody().setTransform(v2.x,v2.y,player.getBody().getAngle());

		cam.update(); // Update cam
		tiledMapRenderer.setView(cam);
		tiledMapRenderer.render();

		rayHandler.setCombinedMatrix(cam.combined);
		rayHandler.updateAndRender();

		//b2dr.render(gWorld.getWorld(), cam.combined); // Renders box2d world

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
