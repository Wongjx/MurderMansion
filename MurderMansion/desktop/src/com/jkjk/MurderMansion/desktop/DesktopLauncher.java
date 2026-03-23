package com.jkjk.MurderMansion.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.jkjk.MurderMansion.MurderMansion;
import org.lwjgl.glfw.GLFW;

public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Murder Mansion");
		config.setWindowedMode(320 * 2, 180 * 2);
		config.useVsync(true);
		config.setResizable(true);

		desktopMultiplayer mMultiplayerSession = new desktopMultiplayer();
		new Lwjgl3Application(new MurderMansion(new ActionResolverDesktop(mMultiplayerSession),
				mMultiplayerSession), config);
		Lwjgl3Window window = ((Lwjgl3Graphics) com.badlogic.gdx.Gdx.graphics).getWindow();
		window.setSizeLimits(320, 180, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);
		GLFW.glfwSetWindowAspectRatio(window.getWindowHandle(), 16, 9);
	}
}
