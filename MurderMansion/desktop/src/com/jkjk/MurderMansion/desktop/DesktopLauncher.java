package com.jkjk.MurderMansion.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jkjk.MurderMansion.MurderMansion;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Murder Mansion";
		config.width = 320 * 2;
		config.height = 180 * 2;
		desktopMultiplayer mMultiplayerSeisson = new desktopMultiplayer();

		new LwjglApplication(new MurderMansion(new ActionResolverDesktop(mMultiplayerSeisson),mMultiplayerSeisson), config);
	}
}
