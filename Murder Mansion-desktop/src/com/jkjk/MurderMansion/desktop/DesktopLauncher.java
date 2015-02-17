package com.jkjk.MurderMansion.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jkjk.MurderMansion.JKJK;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Murder Mansion";
		cfg.useGL30 = true;
		cfg.width = 1920/3;
		cfg.height = 1080/3;
		
		new LwjglApplication(new JKJK(), cfg);
	}
}