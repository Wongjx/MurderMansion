package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MMHelpers.PresentationFrame;
import com.jkjk.Multiplayer.MultiplayerUi;
import com.jkjk.MurderMansion.MurderMansion;

public class JoinCodeScreen implements Screen {
	private final MurderMansion game;
	private final float gameWidth;
	private final float gameHeight;
	private final Stage stage;
	private final Label statusLabel;
	private final TextField roomCodeField;

	public JoinCodeScreen(MurderMansion game, float gameWidth, float gameHeight) {
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.stage = new Stage(PresentationFrame.createViewport());

		Image background = new Image(AssetLoader.menuBackground);
		background.setFillParent(true);
		stage.addActor(background);
		stage.addActor(MultiplayerUi.createDimOverlay(0.34f));

		Label title = new Label("Join By Room Code", MultiplayerUi.createLabelStyle());
		title.setPosition(176f, 250f);
		stage.addActor(title);

		roomCodeField = new TextField("", MultiplayerUi.createTextFieldStyle());
		roomCodeField.setMessageText("Enter Code");
		roomCodeField.setSize(220f, 40f);
		roomCodeField.setPosition(210f, 180f);
		stage.addActor(roomCodeField);

		statusLabel = new Label("", MultiplayerUi.createLabelStyle());
		statusLabel.setPosition(120f, 150f);
		stage.addActor(statusLabel);

		addButton("Join", 220f, 110f, new Runnable() {
			@Override
			public void run() {
				((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerLobbyScreen(game,
						gameWidth, gameHeight, MultiplayerLobbyScreen.EntryMode.JOIN_CODE,
						roomCodeField.getText().trim().toUpperCase()));
			}
		});
		addButton("Back", 360f, 110f, new Runnable() {
			@Override
			public void run() {
				((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerMenuScreen(game,
						gameWidth, gameHeight));
			}
		});
	}

	private void addButton(String text, float x, float y, final Runnable action) {
		TextButton button = new TextButton(text, AssetLoader.normal);
		button.setSize(110f, 38f);
		button.setPosition(x, y);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				action.run();
			}
		});
		stage.addActor(button);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
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
