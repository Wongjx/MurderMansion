package com.jkjk.MurderMansion;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.jkjk.MMHelpers.MMLog;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;
import com.jkjk.MMHelpers.NoOpActionResolver;

public class IOSLauncher extends IOSApplication.Delegate {
	@Override
	protected IOSApplication createApplication() {
		MMLog.log("MM-START", "IOS createApplication()");
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				MMLog.log("MM-CRASH", "Uncaught exception on thread: " + thread.getName(), throwable);
			}
		});
		IOSApplicationConfiguration config = new IOSApplicationConfiguration();
		config.orientationLandscape = true;
		config.orientationPortrait = false;
		MultiplayerSessionInfo sessionInfo = new MultiplayerSessionInfo();
		sessionInfo.relaySocketFactory = new IOSRelaySocketFactory();
		return new IOSApplication(new MurderMansion(new NoOpActionResolver(), sessionInfo), config);
	}

	public static void main(String[] argv) {
		NSAutoreleasePool pool = new NSAutoreleasePool();
		UIApplication.main(argv, null, IOSLauncher.class);
		pool.close();
	}
}
