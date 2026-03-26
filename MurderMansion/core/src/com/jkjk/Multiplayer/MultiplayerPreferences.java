package com.jkjk.Multiplayer;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class MultiplayerPreferences {
	private static final String PREFS_NAME = "murder-mansion-multiplayer";
	private static final String DISPLAY_NAME_KEY = "display_name";
	private static final String DISCOVERY_URL_KEY = "discovery_url";
	public static final int MAX_DISPLAY_NAME_LENGTH = 12;

	private static final String[] ADJECTIVES = { "Silent", "Shadow", "Crimson", "Misty", "Ghastly",
			"Rogue", "Clever", "Hidden", "Moonlit", "Wicked" };
	private static final String[] NOUNS = { "Crow", "Fox", "Ghost", "Knife", "Lantern", "Raven",
			"Trap", "Watcher", "Mask", "Mansion" };

	private MultiplayerPreferences() {
	}

	public static String getDisplayName() {
		Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);
		String value = sanitizeDisplayName(preferences.getString(DISPLAY_NAME_KEY, ""));
		if (value == null || value.isEmpty()) {
			value = sanitizeDisplayName(generateGuestName());
			preferences.putString(DISPLAY_NAME_KEY, value);
			preferences.flush();
		}
		return value;
	}

	public static void setDisplayName(String displayName) {
		Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);
		preferences.putString(DISPLAY_NAME_KEY, sanitizeDisplayName(displayName));
		preferences.flush();
	}

	public static String getDiscoveryUrl() {
		Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);
		return preferences.getString(DISCOVERY_URL_KEY, DiscoveryApiClient.DEFAULT_BASE_URL);
	}

	public static void setDiscoveryUrl(String discoveryUrl) {
		Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);
		preferences.putString(DISCOVERY_URL_KEY, discoveryUrl == null ? "" : discoveryUrl.trim());
		preferences.flush();
	}

	private static String generateGuestName() {
		Random random = new Random();
		return ADJECTIVES[random.nextInt(ADJECTIVES.length)] + NOUNS[random.nextInt(NOUNS.length)]
				+ (10 + random.nextInt(90));
	}

	private static String sanitizeDisplayName(String displayName) {
		if (displayName == null) {
			return "";
		}
		String normalized = displayName.trim();
		if (normalized.length() > MAX_DISPLAY_NAME_LENGTH) {
			return normalized.substring(0, MAX_DISPLAY_NAME_LENGTH);
		}
		return normalized;
	}
}
