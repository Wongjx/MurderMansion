package com.jkjk.MurderMansion;

import com.badlogic.gdx.Game;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.Screens.GameScreen;
import com.jkjk.Screens.MenuScreen;

public class murdermansion extends Game {

	@Override
	public void create() {
		AssetLoader.load();
		setScreen(new MenuScreen());
	}

	@Override
	public void dispose() {
		super.dispose();
		AssetLoader.dispose();
	}
}