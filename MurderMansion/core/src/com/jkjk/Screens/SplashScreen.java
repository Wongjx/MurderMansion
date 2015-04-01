package com.jkjk.Screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MurderMansion.MurderMansion;
import com.jkjk.TweenAccessors.SpriteAccessor;

public class SplashScreen implements Screen {

	private TweenManager manager;
	private SpriteBatch batcher;
	private Sprite sprite;
	private MurderMansion game;
	
	private float gameWidth;
	private float gameHeight;

	public SplashScreen(MurderMansion game, float gameWidth, float gameHeight) {
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
	}

	@Override
	public void show() {
		sprite = new Sprite(AssetLoader.logo);
		sprite.setColor(1, 1, 1, 0);

		sprite.setPosition((Gdx.graphics.getWidth()/2 - sprite.getWidth()/2), 
				(Gdx.graphics.getHeight()/2 - sprite.getHeight()/2));
		setupTween();
		batcher = new SpriteBatch();
	}

	private void setupTween() {
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		manager = new TweenManager();

		TweenCallback cb = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				game.setScreen(new MenuScreen(game,gameWidth,gameHeight));
			}
		};

		Tween.to(sprite, SpriteAccessor.ALPHA, 2f).target(1)
				.ease(TweenEquations.easeInOutQuad).repeatYoyo(1, 0.5f)
				.setCallback(cb).setCallbackTriggers(TweenCallback.COMPLETE)
				.start(manager);
	}

	@Override
	public void render(float delta) {
		manager.update(delta);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batcher.begin();
		sprite.draw(batcher);
		batcher.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

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

	}

}
