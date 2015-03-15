package com.jkjk.Screens;

import com.badlogic.gdx.Screen;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.mGameWorld;
import com.jkjk.MMHelpers.MultiplayerSeissonInfo;
import com.jkjk.MurderMansion.murdermansion;

public class GameScreen implements Screen {
	private murdermansion game;
	
	private GameWorld gWorld;
	private GameRenderer renderer;
	private float runTime;

	private float gameWidth;
	private float gameHeight;
	
	public GameScreen(murdermansion game,float gameWidth, float gameHeight) {
		this.game=game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		
		
		if(game.mMultiplayerSeisson.mState==game.mMultiplayerSeisson.ROOM_PLAY){
			gWorld = new mGameWorld(gameWidth, gameHeight,game);
		}else{
			gWorld = new GameWorld(gameWidth, gameHeight);
		}
		
		renderer = new GameRenderer(gWorld, gameWidth, gameHeight);
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
		renderer.rendererDispose();
	}
}
