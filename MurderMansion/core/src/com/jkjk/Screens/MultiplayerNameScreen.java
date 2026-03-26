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
import com.jkjk.Multiplayer.MultiplayerPreferences;
import com.jkjk.Multiplayer.MultiplayerUi;
import com.jkjk.MurderMansion.MurderMansion;

public class MultiplayerNameScreen implements Screen {
	private final MurderMansion game;
	private final float gameWidth;
	private final float gameHeight;
	private final Stage stage;
	private final Screen returnScreen;

	private TextField nameField;

	public MultiplayerNameScreen(MurderMansion game, float gameWidth, float gameHeight, Screen returnScreen) {
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.returnScreen = returnScreen;
		this.stage = new Stage(PresentationFrame.createViewport());
	}

	@Override
	public void show() {
		stage.clear();
		Image background = new Image(AssetLoader.menuBackground);
		background.setFillParent(true);
		stage.addActor(background);
		stage.addActor(MultiplayerUi.createDimOverlay(0.34f));

		Label title = new Label("Set Display Name", MultiplayerUi.createLabelStyle());
		title.setPosition(212f, 250f);
		stage.addActor(title);

		nameField = new TextField(MultiplayerPreferences.getDisplayName(), MultiplayerUi.createTextFieldStyle());
		nameField.setMessageText("Guest Name");
		nameField.setMaxLength(MultiplayerPreferences.MAX_DISPLAY_NAME_LENGTH);
		nameField.setSize(240f, 40f);
		nameField.setPosition(200f, 180f);
		stage.addActor(nameField);

		addButton("Save", 220f, 112f, new Runnable() {
			@Override
			public void run() {
				MultiplayerPreferences.setDisplayName(nameField.getText());
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				((Game) Gdx.app.getApplicationListener()).setScreen(returnScreen);
			}
		});
		addButton("Cancel", 360f, 112f, new Runnable() {
			@Override
			public void run() {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				((Game) Gdx.app.getApplicationListener()).setScreen(returnScreen);
			}
		});

		Gdx.input.setInputProcessor(stage);
	}

	private void addButton(String text, float x, float y, final Runnable action) {
		TextButton button = new TextButton(text, AssetLoader.normal);
		button.setSize(110f, 38f);
		button.setPosition(x, y);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				action.run();
			}
		});
		stage.addActor(button);
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
