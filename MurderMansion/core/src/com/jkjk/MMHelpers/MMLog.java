package com.jkjk.MMHelpers;

import java.lang.reflect.Method;

import com.badlogic.gdx.Gdx;
import com.jkjk.Telemetry.TelemetryService;

public final class MMLog {
	private MMLog() {
	}

	public static void log(String tag, String message) {
		String line = "[" + tag + "] " + message;
		System.out.println(line);
		System.err.println(line);
		TelemetryService telemetryService = TelemetryService.get();
		if (telemetryService != null) {
			telemetryService.appendLogLine(line);
		}
		if (Gdx.app != null) {
			Gdx.app.log(tag, message);
		}
		tryNativeIosLog(line);
	}

	public static void log(String tag, String message, Throwable throwable) {
		log(tag, message);
		if (throwable != null) {
			throwable.printStackTrace();
		}
	}

	private static void tryNativeIosLog(String line) {
		try {
			Class<?> foundation = Class.forName("org.robovm.apple.foundation.Foundation");
			Method logMethod = foundation.getMethod("log", String.class);
			logMethod.invoke(null, line);
		} catch (Throwable ignored) {
		}
	}
}
