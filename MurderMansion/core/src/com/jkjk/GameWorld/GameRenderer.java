package com.jkjk.GameWorld;

import box2dLight.RayHandler;

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
 * GameRenderer's primary purpose is to render the graphics that are seen on your screen. This includes
 * displaying the HUD, map, and sprites that exist in the world.
 * 
 * @author LeeJunXiang
 * 
 */
public class GameRenderer {
	private GameWorld gWorld; // Box2D world. This will hold all objects (players, items, walls)
	private OrthographicCamera cam; // Game camera. Views what is happening in the game.
	private Box2DDebugRenderer b2dr; // Renders Box2D objects. (For debugging)

	// Game Objects
	private GameCharacter player; // Player's character

	// Game Assets
	private TiledMap tiledMap; // Loaded map
	private TiledMapRenderer tiledMapRenderer; // Renders the map

	/**
	 * Constructs the link from the Box2D world created in GameWorld to GameRenderer. Allows rendering of the
	 * player's actions, other players and map on the camera.
	 * 
	 * @param gWorld
	 *            Link to the GameWorld, accessing box2d objected created in the world.
	 * @param gameWidth
	 *            Accesses the virtual game width.
	 * @param gameHeight
	 *            Accesses the virtual game height.
	 */
	public GameRenderer(GameWorld gWorld, float gameWidth, float gameHeight) {
		this.gWorld = gWorld;
		b2dr = new Box2DDebugRenderer();

		// Create camera
		cam = new OrthographicCamera();
		cam.setToOrtho(false, (float) (gameWidth / (4.0 / 3)), (float) (gameHeight / (4.0 / 3)));

		tiledMap = AssetLoader.tiledMap;
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

	}

	/**
	 * Renders all images and actions on the player's screen.
	 * 
	 * @param delta
	 *            The time between each render.
	 * @param runTime
	 *            The total runtime since start.
	 */
	public void render(float delta, float runTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clears screen everytime it renders

		tiledMapRenderer.setView(cam);
		tiledMapRenderer.render();

		if (gWorld.getPlayer().isAlive()) {
			gWorld.getPlayer().render(cam);
		}
		cam.update(); // Update cam

		//b2dr.render(gWorld.getWorld(), cam.combined); // Renders box2d world

	}

	/**
	 * Releases the resources held by objects or images loaded.
	 */
	public void rendererDispose() {
		gWorld.getWorld().dispose();
		player.dispose();
		b2dr.dispose();
	}

}
