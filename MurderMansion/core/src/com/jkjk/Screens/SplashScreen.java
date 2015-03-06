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
import com.jkjk.MurderMansion.murdermansion;
import com.jkjk.TweenAccessors.SpriteAccessor;

public class SplashScreen implements Screen {

	private TweenManager manager;
	private SpriteBatch batcher;
	private Sprite sprite;
	private murdermansion game;
	
	private float screenWidth;
	private float screenHeight;

	public SplashScreen(murdermansion game, float screenWidth, float screenHeight) {
		this.game = game;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	@Override
	public void show() {
		sprite = new Sprite(AssetLoader.logo);
		sprite.setColor(1, 1, 1, 0);

		float desiredWidth = screenWidth * .3f;
		float scale = desiredWidth / sprite.getWidth();

		sprite.setSize(sprite.getWidth() * scale, sprite.getHeight() * scale);
		sprite.setPosition((screenWidth / 2) - (sprite.getWidth() / 2), (screenHeight / 2)
				- (sprite.getHeight() / 2));
		setupTween();
		batcher = new SpriteBatch();
	}

	private void setupTween() {
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		manager = new TweenManager();

		TweenCallback cb = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				game.setScreen(new MenuScreen(game,screenWidth,screenHeight));
			}
		};

		Tween.to(sprite, SpriteAccessor.ALPHA, .8f).target(1)
				.ease(TweenEquations.easeInOutQuad).repeatYoyo(1, .4f)
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
