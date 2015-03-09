package com.jkjk.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.HUD;

public class GameScreen implements Screen {
	private GameWorld gWorld;
	private GameRenderer renderer;
	private float runTime;

	private float gameWidth;
	private float gameHeight;
	
	public GameScreen(float gameWidth, float gameHeight) {

		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;

		gWorld = new GameWorld(gameWidth, gameHeight);
		renderer = new GameRenderer(gWorld, gameWidth, gameHeight);
		gWorld.setRenderer(renderer);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		runTime += delta;
		gWorld.update(delta);
		renderer.render(delta, runTime);

	}

	@Override
	public void resize(int width, int height) {

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
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
