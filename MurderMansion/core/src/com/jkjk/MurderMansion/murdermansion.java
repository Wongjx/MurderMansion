package com.jkjk.MurderMansion;

import com.badlogic.gdx.Game;
import com.jkjk.MMHelpers.ActionResolver;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.Screens.GameScreen;
import com.jkjk.Screens.MenuScreen;

public class MurderMansion extends Game {
	public ActionResolver actionResolver;

	public static final String TITLE = "Murder Mansion";
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 200;
	public static final int SCALE = 2;
	

	public MurderMansion(ActionResolver actionResolver){
		this.actionResolver=actionResolver;
	}

	@Override
	public void create() {
		AssetLoader.load();
//		setScreen(new SplashScreen(this, V_WIDTH * SCALE, V_HEIGHT * SCALE));
		setScreen(new MenuScreen(this,V_WIDTH * SCALE, V_HEIGHT * SCALE));
//		setScreen(new GameScreen(V_WIDTH * SCALE, V_HEIGHT * SCALE));
	}

	@Override
	public void dispose() {
		super.dispose();
		AssetLoader.dispose();
	}
}