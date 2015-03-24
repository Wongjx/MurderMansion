package com.jkjk.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.MMHelpers.AssetLoader;

/**
 * @author JunXiang GameRenderer's primary purpose is to render the graphics that are seen on your screen.
 *         This includes displaying the HUD, map, and sprites that exist in the world.
 * 
 */
public class GameRenderer {
	private GameWorld gWorld;
	private OrthographicCamera cam;
	private Box2DDebugRenderer b2dr;

	// Game Objects
	private GameCharacter player;

	// Game Assets
	private TiledMap tiledMap;
	private TiledMapRenderer tiledMapRenderer;

	public GameRenderer(GameWorld gWorld, float gameWidth, float gameHeight) {
		this.gWorld = gWorld;
		b2dr = new Box2DDebugRenderer();

		// Create camera
		cam = new OrthographicCamera();
		cam.setToOrtho(false, (float) (gameWidth / (4.0 / 3)), (float) (gameHeight / (4.0 / 3)));

		// Create player
		player = gWorld.getPlayer();

		tiledMap = AssetLoader.tiledMap;
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

	}

	public void render(float delta, float runTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clears screen everytime it renders

		tiledMapRenderer.setView(cam);
		tiledMapRenderer.render();

		if (gWorld.getPlayer().isAlive()) {
			gWorld.getPlayer().render(cam);
		}
		cam.update(); // Update cam

		b2dr.render(gWorld.getWorld(), cam.combined); // Renders box2d world

	}

	public void rendererDispose() {
		gWorld.getWorld().dispose();
		gWorld.getPlayer().dispose();
		b2dr.dispose();
	}

}
