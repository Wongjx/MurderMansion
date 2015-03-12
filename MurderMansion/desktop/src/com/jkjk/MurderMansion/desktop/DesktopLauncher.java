package com.jkjk.MurderMansion.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jkjk.MurderMansion.murdermansion;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Murder Mansion";
		config.width = 320 * 2;
		config.height = 180 * 2;
		new LwjglApplication(new murdermansion(new ActionResolverDesktop()), config);
	}
}
