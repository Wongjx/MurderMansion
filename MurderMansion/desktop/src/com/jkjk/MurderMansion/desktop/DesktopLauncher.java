package com.jkjk.MurderMansion.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jkjk.MurderMansion.murdermansion;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 1080/3;
		config.width = 1794/3;
		new LwjglApplication(new murdermansion(), config);
	}
}
