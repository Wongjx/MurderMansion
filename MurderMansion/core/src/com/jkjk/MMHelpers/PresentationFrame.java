package com.jkjk.MMHelpers;

import com.badlogic.gdx.utils.viewport.FitViewport;

public final class PresentationFrame {
	public static final float WIDTH = 640f;
	public static final float HEIGHT = 360f;

	private PresentationFrame() {
	}

	public static FitViewport createViewport() {
		return new FitViewport(WIDTH, HEIGHT);
	}
}
