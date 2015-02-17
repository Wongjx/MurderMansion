package com.jkjk.MurderMansion;

import com.badlogic.gdx.Game;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.Screens.GameScreen;

public class JKJK extends Game {

	@Override
	public void create() {
		AssetLoader.load();
		setScreen(new GameScreen());
	}

	@Override
	public void dispose() {
		super.dispose();
		AssetLoader.dispose();
	}
}
