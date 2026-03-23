package com.jkjk.Screens;

import java.io.IOException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameSession;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.HudRenderer;
import com.jkjk.Host.MMServer;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MurderMansion.MurderMansion;

public class GameScreen implements Screen {
	private GameWorld gWorld;
	private GameRenderer renderer;
	private HudRenderer hudRenderer;
	private float runTime;
	private float gameWidth;
	private float gameHeight;
	private MurderMansion game;

	private GameSession session;

	public GameScreen(MurderMansion game, float gameWidth, float gameHeight, GameWorld world,
			GameRenderer renderer, GameSession session, boolean tutorial) {
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.gWorld = world;
		this.renderer = renderer;
		this.session = session;
		hudRenderer = new HudRenderer(gWorld, session, gameWidth, gameHeight, game, tutorial);
		gWorld.setPlayerInputController(hudRenderer.getPlayerInputController());
	}

	@Override
	public void show() {
		AssetLoader.menuMusic.stop();
		AssetLoader.gameMusic.play();

		session.updatePlayerIsReady();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void render(float delta) {
		hudRenderer.updateInput(renderer.getWorldViewport());
		if (gWorld.isCivWin() || gWorld.isMurWin()) {
			gWorld.getGameOverTimer().update();
			if (!gWorld.getGameOverTimer().isCountingDown()) {
				System.out.println("GAMEWORLD UPDATE: GAMEOVER COMPLETE");
				((Game) Gdx.app.getApplicationListener()).setScreen(new ScoreScreen(game, gameWidth,
						gameHeight, session, gWorld));
			}
		} else if (gWorld.isDisconnected()) {
			gWorld.getGameOverTimer().update();
			if (!gWorld.getGameOverTimer().isCountingDown()) {
				System.out.println("GAMEWORLD UPDATE: GAMEOVER COMPLETE");
				((Game) Gdx.app.getApplicationListener()).setScreen(new ScoreScreen(game, gameWidth,
						gameHeight, session, gWorld));
			}
		}
		runTime += delta;
		gWorld.update(delta, session);

		renderer.render(delta, runTime, session);
		hudRenderer.render(delta, session.getIsGameStart());

		// if phone is designated server
		if (game.mMultiplayerSession != null && game.mMultiplayerSession.isServer
				&& game.mMultiplayerSession.getServer() != null) {
			try {
				game.mMultiplayerSession.getServer().update();
			} catch (NullPointerException e) {
				e.printStackTrace();
				System.out.println("Disconnected?");
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		renderer.resize(width, height);
		hudRenderer.resize(width, height);
	}

	@Override
	public void pause() {
		try {
			if (session != null) {
				session.endSession();
			}
			if (game.mMultiplayerSession != null && game.mMultiplayerSession.isServer
					&& game.mMultiplayerSession.getServer() != null) {
				game.mMultiplayerSession.getServer().endSession();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		try {
			if (session != null) {
				session.endSession();
			}
			if (game.mMultiplayerSession != null && game.mMultiplayerSession.isServer
					&& game.mMultiplayerSession.getServer() != null) {
				game.mMultiplayerSession.getServer().endSession();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		dispose();

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
		while (!isInterrupted()) {
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
