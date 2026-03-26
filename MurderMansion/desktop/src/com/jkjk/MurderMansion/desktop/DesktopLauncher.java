package com.jkjk.MurderMansion.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;
import com.jkjk.MurderMansion.MurderMansion;

public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Murder Mansion");
		config.setWindowedMode(1280, 720);
		config.setWindowSizeLimits(320, 180, Integer.MAX_VALUE, Integer.MAX_VALUE);
		config.useVsync(true);
		config.setResizable(true);
		config.setMaximized(true);

		desktopMultiplayer mMultiplayerSession = new desktopMultiplayer();
		mMultiplayerSession.relaySocketFactory = new DesktopRelaySocketFactory();
		new Lwjgl3Application(new MurderMansion(new ActionResolverDesktop(mMultiplayerSession),
				mMultiplayerSession), config);
	}
}
