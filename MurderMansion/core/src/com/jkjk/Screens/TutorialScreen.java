package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.LocalGameSession;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MMHelpers.PresentationFrame;
import com.jkjk.MurderMansion.MurderMansion;

public class TutorialScreen implements Screen {

	private static final float MURDERER_X = 118f;
	private static final float CIVILIAN_X = 483f;
	private static final float CHARACTER_Y = 134f;
	private static final float HIT_WIDTH = 170f;
	private static final float HIT_HEIGHT = 140f;
	private static final float BACK_X = 15f;
	private static final float BACK_Y = 127f;

	private final Stage stage;
	private final Image page1;
	private final Image backButton;
	private final Actor civHitArea;
	private final Actor murHitArea;

	public TutorialScreen(final MurderMansion game, final float gameWidth, final float gameHeight) {
		stage = new Stage(PresentationFrame.createViewport());
		page1 = new Image(AssetLoader.tutorialP1);
		backButton = new Image(AssetLoader.backButton);
		civHitArea = new Actor();
		murHitArea = new Actor();

		civHitArea.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startLocalTutorial(game, gameWidth, gameHeight, 1);
			}
		});

		murHitArea.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startLocalTutorial(game, gameWidth, gameHeight, 0);
			}
		});

		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game, gameWidth,
						gameHeight));
			}
		});

		page1.setFillParent(true);
		stage.addActor(page1);
		stage.addActor(civHitArea);
		stage.addActor(murHitArea);
		stage.addActor(backButton);
		layoutActors();
	}

	private void startLocalTutorial(MurderMansion game, float gameWidth, float gameHeight,
			int playerType) {
		AssetLoader.clickSound.play(AssetLoader.VOLUME);
		GameWorld gWorld = new GameWorld(true);
		GameRenderer renderer = new GameRenderer(gWorld, gameWidth, gameHeight);
		LocalGameSession session = new LocalGameSession(gWorld, playerType);
		AssetLoader.loadGameSfx();
		((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(game, gameWidth,
				gameHeight, gWorld, renderer, session, true));
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
		civHitArea.setBounds(CIVILIAN_X - 58f, CHARACTER_Y - 40f, HIT_WIDTH, HIT_HEIGHT);
		murHitArea.setBounds(MURDERER_X - 42f, CHARACTER_Y - 40f, HIT_WIDTH, HIT_HEIGHT);
		backButton.setSize(77f, 62f);
		backButton.setPosition(BACK_X, BACK_Y);
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
