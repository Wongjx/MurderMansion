/**
 * 
 */
package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameSession;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MurderMansion.MurderMansion;

/**
 * @author LeeJunXiang
 * 
 */
public class LoadingScreen implements Screen {
	private GameWorld gWorld;
	private GameRenderer renderer;
	private float gameWidth;
	private float gameHeight;
	private MurderMansion game;

	private GameSession session;

	private Image loadingImageCiv;
	private Image loadingImageMur;
	private Stage stage;

	private boolean screenQueued;

	/**
	 * Loads Game SFX and shows mini tutorial
	 */
	public LoadingScreen(MurderMansion game, float gameWidth, float gameHeight, GameWorld gWorld,
			GameRenderer renderer, GameSession session) {
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.gWorld = gWorld;
		this.renderer = renderer;
		this.session = session;
		stage = new Stage(new ExtendViewport(gameWidth, gameHeight));
		screenQueued = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
		loadingImageCiv = new Image(AssetLoader.civLoad);
		loadingImageMur = new Image(AssetLoader.murLoad);

		if ("Murderer".equals(gWorld.getPlayer().getType())) {
			stage.addActor(loadingImageMur);
		} else {
			stage.addActor(loadingImageCiv);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		stage.draw();
		stage.act();

		if (!screenQueued) {
			screenQueued = true;
			AssetLoader.loadGameSfx();
			System.out.println("Setting screen to new game screen.");
			((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(game, gameWidth, gameHeight,
					gWorld, renderer, session, false));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stage.dispose();
	}
}
