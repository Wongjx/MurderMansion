package com.jkjk.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.jkjk.GameObjects.Obstacles;
import com.jkjk.GameObjects.Items.ItemSprite;
import com.jkjk.GameObjects.Items.Trap;
import com.jkjk.GameObjects.Weapons.WeaponPartSprite;
import com.jkjk.GameObjects.Weapons.WeaponSprite;
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

	// Game Assets
	private TiledMap tiledMap; // Loaded map
	private TiledMapRenderer tiledMapRenderer; // Renders the map

	private SpriteBatch batch;

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
		cam.setToOrtho(false, (float) (gameWidth / 1.5), (float) (gameHeight / 1.5));

		tiledMap = AssetLoader.tiledMap;
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		batch = new SpriteBatch();

	}

	/**
	 * Renders all images and actions on the player's screen.
	 * 
	 * @param delta
	 *            The time between each render.
	 * @param runTime
	 *            The total runtime since start.
	 */
	public void render(float delta, float runTime, MMClient client) {
		System.out.println("Clear screen");
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clears screen everytime it renders

		System.out.println("Set tiled map renderer");
		tiledMapRenderer.setView(cam);
		System.out.println("Render tiled map");
		tiledMapRenderer.render();
		
		System.out.println("Set projection matrix");
		batch.setProjectionMatrix(cam.combined);
		
		System.out.println("Render client");
		client.render(cam, batch);
		System.out.println("Begin batch");
		batch.begin();
		
		System.out.println("Render obstacles");
		for (Obstacles ob : gWorld.getObstacleList().values()) {
			ob.render(batch);
		}
		System.out.println("Render items");
		for (ItemSprite iS : gWorld.getItemList().values()) {
			iS.render(batch);
		}
		System.out.println("Render weapons");
		for (WeaponSprite wS : gWorld.getWeaponList().values()) {
			wS.render(batch);
		}
		System.out.println("Render weapon part");
		for (WeaponPartSprite wPS : gWorld.getWeaponPartList().values()) {
			wPS.render(batch);
		}
		System.out.println("Render traps");
		for (Trap trap : gWorld.getTrapList().values()) {
			trap.render(batch);
		}
		System.out.println("End batch");
		batch.end();
		
		System.out.println("Render player");
		if (gWorld.getPlayer().isAlive()) {
			gWorld.getPlayer().render(cam, batch);
		}
		System.out.println("Update cam");
		cam.update(); // Update cam

		// b2dr.render(gWorld.getWorld(), cam.combined); // Renders box2d world

	}

	/**
	 * Releases the resources held by objects or images loaded.
	 */
	public void rendererDispose() {
		b2dr.dispose();
	}

}
