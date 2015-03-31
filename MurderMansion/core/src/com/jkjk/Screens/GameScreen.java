package com.jkjk.Screens;

import com.badlogic.gdx.Screen;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.HudRenderer;
import com.jkjk.Host.MMServer;
import com.jkjk.MurderMansion.murdermansion;

public class GameScreen implements Screen {
	private GameWorld gWorld;
	private GameRenderer renderer;
	private HudRenderer hudRenderer;
	private float runTime;

	private MMServer server;

	public GameScreen(murdermansion game, float gameWidth, float gameHeight) {
		if (game.mMultiplayerSeisson.mState == game.mMultiplayerSeisson.ROOM_PLAY) {
			// gWorld = new mGameWorld(gameWidth, gameHeight,game);
		} else {
			gWorld = new GameWorld(gameWidth, gameHeight);
		}

		renderer = new GameRenderer(gWorld, gameWidth, gameHeight);
		hudRenderer = new HudRenderer(gWorld, gameWidth, gameHeight);

		server = new MMServer(4);
		MMServerThread serverThread = new MMServerThread(server);
		serverThread.start();
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
		hudRenderer.render(delta);

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
		hudRenderer.hudDispose();
	}
}

class MMServerThread extends Thread {

	private MMServer server;

	public MMServerThread(MMServer server) {
		this.server = server;
	}

	public void run() {
		while (true){
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			server.update();
		}
	}
}
