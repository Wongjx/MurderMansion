package com.jkjk.Screens;

import java.util.concurrent.CountDownLatch;

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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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

	private SpriteBatch batch;
	private Texture background;
	private Sprite sprite;

	private TextButtonStyle normal;

	private Stage stage;
	private TextButton buttonQuick;
	private TextButton buttonLogin;
	private TextButton buttonLogout;
	private TextButton buttonPlay;
	private TextButton buttonJoin;
	private TextButton buttonTutorial;
	private TextButton buttonInvite;

	private MurderMansion game;

	public MenuScreen(MurderMansion game, float gameWidth, float gameHeight) {
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.game = game;

		BUTTON_WIDTH = 120;
		BUTTON_HEIGHT = 40;

		normal = AssetLoader.normal;

		stage = new Stage(new ExtendViewport(gameWidth, gameHeight));
		buttonPlay = new TextButton("Enter", normal);
		buttonJoin = new TextButton("Join Game", normal);
		buttonTutorial = new TextButton("Tutorial", normal);
		buttonLogin = new TextButton("Login", normal);
		buttonLogout = new TextButton("Logout", normal);
		buttonQuick = new TextButton("Quick Game", normal);
		buttonInvite = new TextButton("Invite", normal);
	}

	@Override
	public void show() {

		// The elements are displayed in the order you add them.
		// The first appear on top, the last at the bottom.
		if (AssetLoader.gameMusic != null) {
			AssetLoader.gameMusic.stop();
			AssetLoader.disposeSFX();
		}
		AssetLoader.menuMusic.play();

		batch = new SpriteBatch();
		background = AssetLoader.menuBackground;
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		sprite = new Sprite(background);
		sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		buttonPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				GameWorld world = new GameWorld();
				GameRenderer renderer = new GameRenderer(world, gameWidth, gameHeight);

				try {
					game.mMultiplayerSession.isServer = true;
					game.mMultiplayerSession.setServer(new MMServer(1, game.mMultiplayerSession));
					game.mMultiplayerSession.setClient(new MMClient(world, renderer,
							game.mMultiplayerSession.serverAddress, game.mMultiplayerSession.serverPort,game.mMultiplayerSession.mId,game.mMultiplayerSession.mName));
				} catch (Exception e) {
					e.printStackTrace();
				}
				((Game) Gdx.app.getApplicationListener()).setScreen(new LoadingScreen(game, gameWidth,
						gameHeight, world, renderer));
			}
		});
		buttonLogin.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				game.actionResolver.loginGPGS();

			}
		});

		buttonLogout.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				game.actionResolver.logoutGPGS();

			}
		});

		buttonTutorial.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				// TODO Set to tutorial screen
				((Game) Gdx.app.getApplicationListener()).setScreen(new TutorialScreen(game, gameWidth,
						gameHeight));
			}
		});

		buttonQuick.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				// Host multiplayer game
				if(game.actionResolver.getSignedInGPGS()){
					game.actionResolver.startQuickGame();
					game.mMultiplayerSession.mState = game.mMultiplayerSession.ROOM_WAIT;
					((Game) Gdx.app.getApplicationListener()).setScreen(new WaitScreen(game, gameWidth,
							gameHeight));
				} else{
					game.actionResolver.loginGPGS();
				}
			}
		});

		buttonInvite.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				if(game.actionResolver.getSignedInGPGS()){
					game.actionResolver.sendInvitations();
					game.mMultiplayerSession.mState = game.mMultiplayerSession.ROOM_WAIT;
					((Game) Gdx.app.getApplicationListener()).setScreen(new WaitScreen(game, gameWidth,
							gameHeight));
				}else{
					game.actionResolver.loginGPGS();
				}
			}
		});

		buttonJoin.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				if(game.actionResolver.getSignedInGPGS()){
					game.actionResolver.seeInvitations();
					game.mMultiplayerSession.mState = game.mMultiplayerSession.ROOM_WAIT;
					((Game) Gdx.app.getApplicationListener()).setScreen(new WaitScreen(game, gameWidth,
							gameHeight));
				}else{
					game.actionResolver.loginGPGS();
				}
			}

		});

		buttonPlay.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		buttonPlay.setPosition(345, 100);
		stage.addActor(buttonPlay);

		buttonQuick.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		buttonQuick.setPosition(475, 220);
		stage.addActor(buttonQuick);

		buttonLogin.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		buttonLogin.setPosition(345, 220);
		stage.addActor(buttonLogin);

		buttonLogout.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		buttonLogout.setPosition(345, 20);
		stage.addActor(buttonLogout);

		buttonTutorial.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		buttonTutorial.setPosition(475, 100);
		stage.addActor(buttonTutorial);

		buttonInvite.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		buttonInvite.setPosition(345, 160);
		stage.addActor(buttonInvite);

		buttonJoin.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		buttonJoin.setPosition(475, 160);
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
