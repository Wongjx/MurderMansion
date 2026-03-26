package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MMHelpers.PresentationFrame;
import com.jkjk.MurderMansion.MurderMansion;

public class MenuScreen implements Screen {

	private static final float BUTTON_WIDTH = 140f;
	private static final float BUTTON_HEIGHT = 42f;

	private final float gameWidth;
	private final float gameHeight;
	private final MurderMansion game;
	private final Stage stage;
	private final TextButtonStyle normal;

	private Image backgroundImage;
	private TextButton buttonPlay;
	private TextButton buttonOnline;
	private Image muteButton;
	private Image unmuteButton;

	public MenuScreen(MurderMansion game, float gameWidth, float gameHeight) {
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.game = game;
		this.normal = AssetLoader.normal;
		stage = new Stage(PresentationFrame.createViewport());
	}

	@Override
	public void show() {
		if (AssetLoader.gameMusic != null) {
			AssetLoader.gameMusic.stop();
			AssetLoader.disposeSFX();
		}
		AssetLoader.menuMusic.play();

		stage.clear();

		backgroundImage = new Image(AssetLoader.menuBackground);
		backgroundImage.setFillParent(true);
		stage.addActor(backgroundImage);

		buttonPlay = new TextButton("Play Local", normal);
		buttonPlay.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				((Game) Gdx.app.getApplicationListener()).setScreen(new TutorialScreen(game, gameWidth,
						gameHeight));
			}
		});
		stage.addActor(buttonPlay);

		buttonOnline = new TextButton("Play Online", normal);
		buttonOnline.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonOnline.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerMenuScreen(game, gameWidth,
						gameHeight));
			}
		});
		stage.addActor(buttonOnline);

		muteButton = new Image(AssetLoader.muteButton);
		unmuteButton = new Image(AssetLoader.unmuteButton);
		muteButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.muteSFX();
				muteButton.remove();
				stage.addActor(unmuteButton);
				layoutActors();
			}
		});
		unmuteButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.unmuteSFX();
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				unmuteButton.remove();
				stage.addActor(muteButton);
				layoutActors();
			}
		});

		if (AssetLoader.VOLUME == 1) {
			stage.addActor(muteButton);
		} else {
			stage.addActor(unmuteButton);
		}

		layoutActors();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
		layoutActors();
	}

	private void layoutActors() {
		if (buttonPlay != null) {
			buttonPlay.setPosition((PresentationFrame.WIDTH - BUTTON_WIDTH) / 2f, 158f);
		}
		if (buttonOnline != null) {
			buttonOnline.setPosition((PresentationFrame.WIDTH - BUTTON_WIDTH) / 2f, 110f);
		}
		float buttonX = PresentationFrame.WIDTH - 18f - AssetLoader.muteButton.getWidth();
		float buttonY = 18f;
		if (muteButton != null) {
			muteButton.setPosition(buttonX, buttonY);
		}
		if (unmuteButton != null) {
			unmuteButton.setPosition(buttonX, buttonY);
		}
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
