package com.jkjk.Screens;

import java.util.concurrent.CountDownLatch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.MMClient;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MurderMansion.MurderMansion;

public class WaitScreen implements Screen {
	
	private Stage stage;
	private SpriteBatch batcher;
	private Sprite sprite;
	private MurderMansion game;

	private float gameWidth;
	private float gameHeight;

	public WaitScreen(MurderMansion game, float gameWidth, float gameHeight) {
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		stage = new Stage(new ExtendViewport(gameWidth, gameHeight));
	}

	@Override
	public void show() {
		sprite = new Sprite(AssetLoader.logo);
		sprite.setColor(1, 1, 1, 1);

		float desiredWidth = gameWidth * .3f;
		float scale = desiredWidth / sprite.getWidth();

		sprite.setSize(sprite.getWidth() * scale, sprite.getHeight() * scale);
		sprite.setPosition((gameWidth / 2) - (sprite.getWidth() / 2), (gameHeight / 2)
				- (sprite.getHeight() / 2));
		Image logo = new Image(new SpriteDrawable(sprite));
		logo.setPosition((Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2),
				(Gdx.graphics.getHeight() / 2 - sprite.getHeight() / 2));
		stage.addActor(logo);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
//		System.out.println("Room state: "+game.mMultiplayerSession.mState);
//		System.out.println("Server adddress: "+game.mMultiplayerSession.serverAddress+" Server Port: "+game.mMultiplayerSession.serverPort);
		if ((game.mMultiplayerSession.mState == game.mMultiplayerSession.ROOM_PLAY)
				&& (game.mMultiplayerSession.serverAddress != null)
				&& (game.mMultiplayerSession.serverPort != 0)) {
			System.out.println("Condition fufilled!");
			// Create MMClient and connect to server
			GameWorld gWorld = new GameWorld();
			System.out.println("New world made");
			GameRenderer renderer = new GameRenderer(gWorld, gameWidth, gameHeight);
			System.out.println("New Renderer made");

			try {
				game.mMultiplayerSession.setClient(new MMClient(gWorld, renderer,
						game.mMultiplayerSession.serverAddress, game.mMultiplayerSession.serverPort,game.mMultiplayerSession.mId,game.mMultiplayerSession.mName));
				System.out.println("Set new client.");

			} catch (Exception e) {
				System.out.println("Error @ HERE!");
				e.printStackTrace();
			}
			System.out.println("Setting screen to new loading screen.");
			((Game) Gdx.app.getApplicationListener()).setScreen(new LoadingScreen(game, gameWidth, gameHeight,
					gWorld, renderer));

		} else if (game.mMultiplayerSession.mState == game.mMultiplayerSession.ROOM_MENU) {
			game.mMultiplayerSession.mState = game.mMultiplayerSession.ROOM_NULL;
			((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game, gameWidth, gameHeight));

		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stage.dispose();
	}

}
