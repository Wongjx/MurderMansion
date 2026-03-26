package com.jkjk.Multiplayer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jkjk.MMHelpers.AssetLoader;

public final class MultiplayerUi {
	private static Texture cursorTexture;
	private static Texture solidTexture;

	private MultiplayerUi() {
	}

	public static TextFieldStyle createTextFieldStyle() {
		TextFieldStyle style = new TextFieldStyle();
		style.font = AssetLoader.crimesFont36Black;
		style.fontColor = Color.WHITE;
		style.messageFont = AssetLoader.crimesFont36Black;
		style.messageFontColor = new Color(0.8f, 0.8f, 0.8f, 1f);
		style.background = AssetLoader.scoreSkin.getDrawable("namebox");
		style.cursor = getCursorDrawable();
		style.selection = AssetLoader.menuSkin.getDrawable("buttonDown");
		return style;
	}

	public static LabelStyle createLabelStyle() {
		LabelStyle style = new LabelStyle();
		style.font = AssetLoader.crimesFont36;
		style.fontColor = Color.WHITE;
		return style;
	}

	public static Image createDimOverlay(float alpha) {
		Image overlay = new Image(getSolidDrawable());
		overlay.setFillParent(true);
		overlay.setColor(0f, 0f, 0f, alpha);
		return overlay;
	}

	public static Image createPanel(float x, float y, float width, float height, float alpha) {
		Image panel = new Image(getSolidDrawable());
		panel.setBounds(x, y, width, height);
		panel.setColor(0f, 0f, 0f, alpha);
		return panel;
	}

	private static TextureRegionDrawable getCursorDrawable() {
		if (cursorTexture == null) {
			Pixmap pixmap = new Pixmap(2, 40, Pixmap.Format.RGBA8888);
			pixmap.setColor(Color.WHITE);
			pixmap.fill();
			cursorTexture = new Texture(pixmap);
			pixmap.dispose();
		}
		return new TextureRegionDrawable(cursorTexture);
	}

	private static TextureRegionDrawable getSolidDrawable() {
		if (solidTexture == null) {
			Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
			pixmap.setColor(Color.WHITE);
			pixmap.fill();
			solidTexture = new Texture(pixmap);
			pixmap.dispose();
		}
		return new TextureRegionDrawable(solidTexture);
	}
}
