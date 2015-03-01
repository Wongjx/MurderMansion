package com.jkjk.Screens;

import com.badlogic.gdx.Screen;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameWorld;

public class GameScreen implements Screen {
	private GameWorld gWorld;
	private GameRenderer renderer;
	private float runTime;

	private float screenWidth;
	private float screenHeight;

	// This is the constructor, not the class declaration
	public GameScreen(float screenWidth, float screenHeight) {

		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		gWorld = new GameWorld(screenWidth, screenHeight);
		renderer = new GameRenderer(gWorld, screenWidth, screenHeight);
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
		this.screenHeight = height;
		this.screenWidth = width;

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
