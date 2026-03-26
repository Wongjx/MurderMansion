package com.jkjk.MMHelpers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class PresentationFrame {
	public static final float WIDTH = 640f;
	public static final float HEIGHT = 360f;

	private PresentationFrame() {
	}

	public static FitViewport createViewport() {
		return new FitViewport(WIDTH, HEIGHT);
	}

	public static Viewport createHudViewport() {
		return createViewport();
	}

	public static Viewport createWorldViewport(OrthographicCamera camera) {
		return new FitViewport(WIDTH / 1.5f, HEIGHT / 1.5f, camera);
	}
}
