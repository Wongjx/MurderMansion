package com.jkjk.GameWorld;

import com.jkjk.GameObjects.Civilian;
import com.jkjk.GameObjects.Murderer;

public class GameWorld {
	private Civilian civilian;
	private Murderer murderer;
	private GameRenderer renderer;

	public GameWorld() {
	}

	public void update(float delta) {

	}

	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}
}
