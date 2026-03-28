package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MMHelpers.PresentationFrame;
import com.jkjk.Multiplayer.MultiplayerPreferences;
import com.jkjk.Multiplayer.MultiplayerUi;
import com.jkjk.MurderMansion.MurderMansion;

public class MultiplayerMenuScreen implements Screen {
	private static final float ACTION_BUTTON_WIDTH = 176f;
	private static final float ACTION_BUTTON_HEIGHT = 38f;

	private final MurderMansion game;
	private final float gameWidth;
	private final float gameHeight;
	private final Stage stage;

	private TextButton hostPublicButton;
	private TextButton hostPrivateButton;
	private TextButton joinCodeButton;
	private TextButton quickStartButton;
	private TextButton editNameButton;
	private TextButton backButton;
	private Label nameLabel;
	private Label infoLabel;

	public MultiplayerMenuScreen(MurderMansion game, float gameWidth, float gameHeight) {
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.stage = new Stage(PresentationFrame.createViewport());
	}

	@Override
	public void show() {
		if (AssetLoader.gameMusic != null) {
			AssetLoader.gameMusic.stop();
		}
		AssetLoader.menuMusic.play();
		stage.clear();
		Image background = new Image(AssetLoader.menuBackground);
		background.setFillParent(true);
		stage.addActor(background);
		stage.addActor(MultiplayerUi.createDimOverlay(0.34f));

		nameLabel = new Label("Name: " + MultiplayerPreferences.getDisplayName(), MultiplayerUi.createLabelStyle());
		nameLabel.setPosition(88f, 270f);
		stage.addActor(nameLabel);

		if (game.mMultiplayerSession != null && game.mMultiplayerSession.roomNotice != null
				&& !game.mMultiplayerSession.roomNotice.isEmpty()) {
			infoLabel = new Label(game.mMultiplayerSession.roomNotice, MultiplayerUi.createLabelStyle());
			infoLabel.setPosition(88f, 236f);
			stage.addActor(infoLabel);
			game.mMultiplayerSession.roomNotice = null;
		}

		hostPublicButton = button("Host Public Match", 232f, 148f, new Runnable() {
			@Override
			public void run() {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerLobbyScreen(game,
						gameWidth, gameHeight, MultiplayerLobbyScreen.EntryMode.HOST_PUBLIC, null));
			}
		});
		hostPrivateButton = button("Host Private Match", 232f, 100f, new Runnable() {
			@Override
			public void run() {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerLobbyScreen(game,
						gameWidth, gameHeight, MultiplayerLobbyScreen.EntryMode.HOST_PRIVATE, null));
			}
		});
		joinCodeButton = button("Join By Code", 232f, 52f, new Runnable() {
			@Override
			public void run() {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				((Game) Gdx.app.getApplicationListener()).setScreen(new JoinCodeScreen(game, gameWidth,
						gameHeight));
			}
		});
		quickStartButton = button("Quick Start", 232f, 196f, new Runnable() {
			@Override
			public void run() {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerLobbyScreen(game,
						gameWidth, gameHeight, MultiplayerLobbyScreen.EntryMode.QUICK_START, null));
			}
		});
		editNameButton = button("Edit Name", 434f, 270f, new Runnable() {
			@Override
			public void run() {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerNameScreen(game,
						gameWidth, gameHeight, new MultiplayerMenuScreen(game, gameWidth, gameHeight)));
			}
		});
		backButton = button("Back", 22f, 22f, new Runnable() {
			@Override
			public void run() {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game, gameWidth,
						gameHeight));
			}
		});

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(stage);
	}

	private TextButton button(String text, float x, float y, final Runnable action) {
		TextButton button = new TextButton(text, AssetLoader.normal);
		button.setSize(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT);
		button.setPosition(x, y);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				action.run();
			}
		});
		stage.addActor(button);
		return button;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.getViewport().apply();
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
