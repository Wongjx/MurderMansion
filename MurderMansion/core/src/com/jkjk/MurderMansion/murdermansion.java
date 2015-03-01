package com.jkjk.MurderMansion;

import com.badlogic.gdx.Game;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.Screens.GameScreen;
import com.jkjk.Screens.MenuScreen;
import com.jkjk.Screens.SplashScreen;

public class MurderMansion extends Game {

	public static final String TITLE = "Murder Mansion";
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 200;
	public static final int SCALE = 2;

	@Override
	public void create() {
		AssetLoader.load();
//		setScreen(new SplashScreen(this, V_WIDTH * SCALE, V_HEIGHT * SCALE));
//		setScreen(new MenuScreen(V_WIDTH * SCALE, V_HEIGHT * SCALE));
		setScreen(new GameScreen(V_WIDTH * SCALE, V_HEIGHT * SCALE));
	}

	@Override
	public void dispose() {
		super.dispose();
		AssetLoader.dispose();
	}
}