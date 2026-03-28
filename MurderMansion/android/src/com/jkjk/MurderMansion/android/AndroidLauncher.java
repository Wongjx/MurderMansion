package com.jkjk.MurderMansion.android;

import android.os.Bundle;
import android.os.Build;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;
import com.jkjk.MMHelpers.NoOpActionResolver;
import com.jkjk.MurderMansion.MurderMansion;
import com.jkjk.Telemetry.TelemetryService;

public class AndroidLauncher extends AndroidApplication {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				TelemetryService.reportUnhandledException(thread, throwable);
				throwable.printStackTrace();
			}
		});

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useGyroscope = false;

		MultiplayerSessionInfo sessionInfo = new MultiplayerSessionInfo();
		sessionInfo.relaySocketFactory = new AndroidRelaySocketFactory();
		sessionInfo.telemetryPlatform = "android";
		sessionInfo.telemetryAppVersion = "1.0.2";
		sessionInfo.telemetryBuildNumber = "4";
		sessionInfo.telemetryDeviceModel = Build.MANUFACTURER + " " + Build.MODEL;
		sessionInfo.telemetryOsVersion = "Android " + Build.VERSION.RELEASE;
		initialize(new MurderMansion(new NoOpActionResolver(), sessionInfo), config);
	}
}
