package com.jkjk.MurderMansion;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.jkjk.MMHelpers.ActionResolver;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;
import com.jkjk.MMHelpers.NoOpActionResolver;
import com.jkjk.Screens.SplashScreen;

public class MurderMansion extends Game {
	public ActionResolver actionResolver;
	public MultiplayerSessionInfo mMultiplayerSession;

	public static final String TITLE = "Murder Mansion";
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 180;
	public static final int SCALE = 2;
	

	public MurderMansion(ActionResolver actionResolver, MultiplayerSessionInfo mMultiplayerSeisson){
		this.actionResolver = actionResolver != null ? actionResolver : new NoOpActionResolver();
		this.mMultiplayerSession = mMultiplayerSeisson != null ? mMultiplayerSeisson
				: new MultiplayerSessionInfo();
	}

	@Override
	public void create() {
		if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
			GdxNativesLoader.load();
		}
		AssetLoader.initiate();
		AssetLoader.loadLogo();
		AssetLoader.loadFont();
		AssetLoader.loadScoreScreen();
		setScreen(new SplashScreen(this, V_WIDTH * SCALE, V_HEIGHT * SCALE));
//		setScreen(new WaitScreen(this, V_WIDTH * SCALE, V_HEIGHT * SCALE));
//		setScreen(new MenuScreen(this,V_WIDTH * SCALE, V_HEIGHT * SCALE));
//		setScreen(new GameScreen(V_WIDTH * SCALE, V_HEIGHT * SCALE));
//		setScreen(new ScoreScreen(this,V_WIDTH * SCALE, V_HEIGHT * SCALE, true));
		//to test score screen
//		setScreen(new ScoreScreen(this,V_WIDTH * SCALE, V_HEIGHT * SCALE));
	}

	@Override
	public void dispose() {
		if (mMultiplayerSession != null) {
			mMultiplayerSession.clearMatchRuntime();
		}
		super.dispose();
		AssetLoader.dispose();
		AssetLoader.disposeSFX();
	}
}
