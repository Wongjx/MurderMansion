package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameSession;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.HudRenderer;
import com.jkjk.Host.MMServer;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MMHelpers.MMLog;
import com.jkjk.MurderMansion.MurderMansion;
import com.jkjk.Telemetry.TelemetryService;

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
		TelemetryService telemetryService = TelemetryService.get();
		if (telemetryService != null) {
			telemetryService.setScreenName("GameScreen");
			telemetryService.recordEvent("game_screen_shown", "GameScreen", null);
		}
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
				MMLog.log("MM-SCORE", "Game over complete from win state. civWin=" + gWorld.isCivWin()
						+ " murWin=" + gWorld.isMurWin());
				try {
					((Game) Gdx.app.getApplicationListener()).setScreen(new ScoreScreen(game, gameWidth,
							gameHeight, session, gWorld));
					return;
				} catch (Throwable t) {
					MMLog.log("MM-CRASH", "Failed transitioning to ScoreScreen from win state.", t);
					throw t;
				}
			}
		} else if (gWorld.isDisconnected()) {
			gWorld.getGameOverTimer().update();
			if (!gWorld.getGameOverTimer().isCountingDown()) {
				MMLog.log("MM-SCORE", "Game over complete from disconnect state.");
				try {
					((Game) Gdx.app.getApplicationListener()).setScreen(new ScoreScreen(game, gameWidth,
							gameHeight, session, gWorld));
					return;
				} catch (Throwable t) {
					MMLog.log("MM-CRASH", "Failed transitioning to ScoreScreen from disconnect state.", t);
					throw t;
				}
			}
		}
		runTime += delta;
		gWorld.update(delta, session);

		renderer.render(delta, runTime, session);
		hudRenderer.renderActionPreview(renderer.getWorldViewport().getCamera());
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
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
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
