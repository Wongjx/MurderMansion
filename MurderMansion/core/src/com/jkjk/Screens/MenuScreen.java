package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.MMClient;
import com.jkjk.Host.MMServer;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MurderMansion.MurderMansion;

public class MenuScreen implements Screen {

	private float gameWidth;
	private float gameHeight;
	private float BUTTON_WIDTH;
	private float BUTTON_HEIGHT;
	private float scale;

	private SpriteBatch batch;
	private Texture background;
	private Sprite sprite;

	private TextButtonStyle normal;

	private Stage stage = new Stage();
	private TextButton buttonPlay;
	private TextButton buttonJoin;
	private TextButton buttonLogout;
	private TextButton buttonLogin;
	private TextButton buttonQuick;
	private TextButton buttonInvite;

	MurderMansion game;

	public MenuScreen(MurderMansion game, float gameWidth, float gameHeight) {
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.game = game;

		BUTTON_WIDTH = 120;
		BUTTON_HEIGHT = 40;

		normal = AssetLoader.normal;

		buttonPlay = new TextButton("Enter", normal);
		buttonJoin = new TextButton("Connect", normal);
		buttonLogout = new TextButton("Logout", normal);
		buttonLogin = new TextButton("Login", normal);
		buttonQuick = new TextButton("Quick Game", normal);
		buttonInvite = new TextButton("Join Game", normal);
		scale = Gdx.graphics.getWidth() / gameWidth;
	}

	@Override
	public void show() {
		// The elements are displayed in the order you add them.
		// The first appear on top, the last at the bottom.
		AssetLoader.gameMusic.stop();
		AssetLoader.menuMusic.play();
		
		batch = new SpriteBatch();
		background = AssetLoader.menuBackground;
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		sprite = new Sprite(background);
		sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		buttonPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameWorld world = GameWorld.getInstance();
				GameRenderer renderer = GameRenderer.getInstance(world, gameWidth, gameHeight);

				try {
					game.mMultiplayerSeisson.isServer = true;
					game.mMultiplayerSeisson.setServer(MMServer.getInstance(1, game.mMultiplayerSeisson));
					game.mMultiplayerSeisson.setClient(MMClient.getInstance(world, renderer,
							game.mMultiplayerSeisson.serverAddress, game.mMultiplayerSeisson.serverPort));
				} catch (Exception e) {
					e.printStackTrace();
				}

				((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(game, gameWidth,
						gameHeight, world, renderer));
			}
		});
		buttonLogin.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.actionResolver.loginGPGS();

			}
		});

		buttonLogout.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.actionResolver.logoutGPGS();
			}
		});

		buttonQuick.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Host multiplayer game
				game.actionResolver.startQuickGame();
				game.mMultiplayerSeisson.mState = game.mMultiplayerSeisson.ROOM_WAIT;
				((Game) Gdx.app.getApplicationListener()).setScreen(new WaitScreen(game, gameWidth,
						gameHeight));
			}
		});

		buttonInvite.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.actionResolver.sendInvitations();
				game.mMultiplayerSeisson.mState = game.mMultiplayerSeisson.ROOM_WAIT;
				((Game) Gdx.app.getApplicationListener()).setScreen(new WaitScreen(game, gameWidth,
						gameHeight));
			}
		});

		buttonJoin.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.actionResolver.seeInvitations();
				game.mMultiplayerSeisson.mState = game.mMultiplayerSeisson.ROOM_WAIT;
				((Game) Gdx.app.getApplicationListener()).setScreen(new WaitScreen(game, gameWidth,
						gameHeight));
			}
		});

		buttonPlay.setSize(this.BUTTON_WIDTH * scale, this.BUTTON_HEIGHT * scale);
		buttonPlay.setPosition(345 * scale, 220 * scale);
		stage.addActor(buttonPlay);

		buttonQuick.setSize(this.BUTTON_WIDTH * scale, this.BUTTON_HEIGHT * scale);
		buttonQuick.setPosition(475 * scale, 220 * scale);
		stage.addActor(buttonQuick);

		buttonLogin.setSize(this.BUTTON_WIDTH * scale, this.BUTTON_HEIGHT * scale);
		buttonLogin.setPosition(345 * scale, 160 * scale);
		stage.addActor(buttonLogin);

		buttonLogout.setSize(this.BUTTON_WIDTH * scale, this.BUTTON_HEIGHT * scale);
		buttonLogout.setPosition(475 * scale, 160 * scale);
		stage.addActor(buttonLogout);

		buttonInvite.setSize(this.BUTTON_WIDTH * scale, this.BUTTON_HEIGHT * scale);
		buttonInvite.setPosition(345 * scale, 100 * scale);
		stage.addActor(buttonInvite);

		buttonJoin.setSize(this.BUTTON_WIDTH * scale, this.BUTTON_HEIGHT * scale);
		buttonJoin.setPosition(475 * scale, 100 * scale);
		stage.addActor(buttonJoin);

		System.out.println("height: " + BUTTON_HEIGHT);
		System.out.println("width: " + BUTTON_WIDTH);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		sprite.draw(batch);
		batch.end();

		stage.act();
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
