package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.HudRenderer;
import com.jkjk.GameWorld.MMClient;
import com.jkjk.Host.MMServer;
import com.jkjk.MMHelpers.MultiplayerSeissonInfo;
import com.jkjk.MurderMansion.MurderMansion;

public class GameScreen implements Screen {
	private MultiplayerSeissonInfo info;
	private GameWorld gWorld;
	private GameRenderer renderer;
	private HudRenderer hudRenderer;
	private float runTime;
	private float gameWidth;
	private float gameHeight;
	private MurderMansion game;

	private MMServer server;
	private MMClient client;

	public GameScreen(MurderMansion game, float gameWidth, float gameHeight, GameWorld world, GameRenderer renderer) {
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.client=game.mMultiplayerSeisson.getClient();
		this.info=game.mMultiplayerSeisson;
		this.gWorld=client.getgWorld();
		this.renderer=client.getRenderer();
//		this.gWorld=world;		
//		this.renderer=renderer;

//		client = new MMClient(server, gWorld, renderer);
		hudRenderer = HudRenderer.getInstance(gWorld, gameWidth, gameHeight);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		runTime += delta;
		gWorld.update(delta, client);
		renderer.render(delta, runTime, client);
		hudRenderer.render(delta);
		//if phone is designated server
		if(info.isServer){
			info.getServer().update();
		}
		
		if (gWorld.isCivWin() || gWorld.isMurWin()){
			gWorld.getGameOverTimer().update();
			if (!gWorld.getGameOverTimer().isCountingDown()){
//				System.out.println("GAMEWORLD UPDATE: GAMEOVER COMPLETE");
                ((Game)Gdx.app.getApplicationListener()).setScreen(new ScoreScreen(game, gameWidth, gameHeight, gWorld.isMurWin()));
			}
		}
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
		while (true) {
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
