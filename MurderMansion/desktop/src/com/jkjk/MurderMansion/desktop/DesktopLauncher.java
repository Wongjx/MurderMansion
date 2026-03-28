package com.jkjk.MurderMansion.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;
import com.jkjk.MurderMansion.MurderMansion;
import com.jkjk.Telemetry.TelemetryService;

public class DesktopLauncher {
	public static void main(String[] arg) {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				TelemetryService.reportUnhandledException(thread, throwable);
				throwable.printStackTrace();
			}
		});
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Murder Mansion");
		config.setWindowedMode(1280, 720);
		config.setWindowSizeLimits(320, 180, Integer.MAX_VALUE, Integer.MAX_VALUE);
		config.useVsync(true);
		config.setResizable(true);
		config.setMaximized(true);

		desktopMultiplayer mMultiplayerSession = new desktopMultiplayer();
		mMultiplayerSession.relaySocketFactory = new DesktopRelaySocketFactory();
		mMultiplayerSession.telemetryPlatform = "desktop";
		mMultiplayerSession.telemetryAppVersion = "1.0.2";
		mMultiplayerSession.telemetryBuildNumber = "4";
		mMultiplayerSession.telemetryDeviceModel = System.getProperty("os.arch", "desktop");
		mMultiplayerSession.telemetryOsVersion = System.getProperty("os.name", "desktop") + " "
				+ System.getProperty("os.version", "unknown");
		new Lwjgl3Application(new MurderMansion(new ActionResolverDesktop(mMultiplayerSession),
				mMultiplayerSession), config);
	}
}
