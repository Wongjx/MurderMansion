/**
 * 
 */
package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MurderMansion.MurderMansion;

/**
 * @author LeeJunXiang
 * 
 */
public class TutorialScreen implements Screen {

	private Stage stage;
	private Image page1;
	private Image civTut;
	private Image murTut;
	private Image hudTut;
	private Image screenTut;
	private Image mapTut;
	private Image civButton;
	private Image murButton;
	private Image civButtonDown;
	private Image murButtonDown;
	private Image backButton;
	private Image nextButton;

	public TutorialScreen(final MurderMansion game, final float gameWidth, final float gameHeight) {
		stage = new Stage(new ExtendViewport(gameWidth, gameHeight));
		page1 = new Image(AssetLoader.tutorialP1);
		civTut = new Image(AssetLoader.civTut);
		murTut = new Image(AssetLoader.murTut);
		hudTut = new Image(AssetLoader.hudTutorial);
		screenTut = new Image(AssetLoader.screenTutorial);
		mapTut = new Image(AssetLoader.mapTutorial);

		civButton = new Image(AssetLoader.civButton);
		civButton.setPosition(464, 154);
		murButton = new Image(AssetLoader.murButton);
		murButton.setPosition(124, 154);
		civButtonDown = new Image(AssetLoader.civButtonDown);
		civButtonDown.setPosition(464, 154);
		murButtonDown = new Image(AssetLoader.murButtonDown);
		murButtonDown.setPosition(124, 154);
		backButton = new Image(AssetLoader.backButton);
		backButton.setPosition(20, 150);
		nextButton = new Image(AssetLoader.nextButton);
		nextButton.setPosition(550, 150);

		civButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.clear();
				stage.addActor(civTut);
				stage.addActor(murButtonDown);
				stage.addActor(backButton);
				stage.addActor(nextButton);
			}

		});

		murButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.clear();
				stage.addActor(murTut);
				stage.addActor(civButtonDown);
				stage.addActor(backButton);
				stage.addActor(nextButton);
			}

		});

		civButtonDown.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.clear();
				stage.addActor(civTut);
				stage.addActor(murButtonDown);
				stage.addActor(backButton);
				stage.addActor(nextButton);
			}

		});

		murButtonDown.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.clear();
				stage.addActor(murTut);
				stage.addActor(civButtonDown);
				stage.addActor(backButton);
				stage.addActor(nextButton);
			}

		});
		// TODO:
		nextButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				if (stage.getActors().first().equals(hudTut)) {
					stage.clear();
					stage.addActor(screenTut);
					stage.addActor(backButton);
					stage.addActor(nextButton);
				} else if (stage.getActors().first().equals(screenTut)) {
					stage.clear();
					stage.addActor(mapTut);
					stage.addActor(backButton);
					stage.addActor(nextButton);
				} else if (stage.getActors().first().equals(mapTut)) {
					stage.clear();
					((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game, gameWidth,
							gameHeight));
				} else {
					stage.clear();
					stage.addActor(hudTut);
					stage.addActor(backButton);
					stage.addActor(nextButton);
				}
			}

		});
		// TODO:
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				if (stage.getActors().first().equals(hudTut)) {
					stage.clear();
					stage.addActor(page1);
					stage.addActor(civButton);
					stage.addActor(murButton);
					stage.addActor(backButton);
					stage.addActor(nextButton);
				} else if (stage.getActors().first().equals(screenTut)) {
					stage.clear();
					stage.addActor(hudTut);
					stage.addActor(backButton);
					stage.addActor(nextButton);
				} else if (stage.getActors().first().equals(mapTut)) {
					stage.clear();
					stage.addActor(screenTut);
					stage.addActor(backButton);
					stage.addActor(nextButton);
				} else {
					stage.clear();
					((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game, gameWidth,
							gameHeight));
				}
			}

		});

		stage.addActor(page1);
		stage.addActor(civButton);
		stage.addActor(murButton);
		stage.addActor(backButton);
		stage.addActor(nextButton);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		stage.draw();
		stage.act(delta);
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
