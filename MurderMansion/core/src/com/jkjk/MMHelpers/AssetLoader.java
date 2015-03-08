package com.jkjk.MMHelpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jkjk.MurderMansion.murdermansion;

public class AssetLoader {
	public static Touchpad touchpad;
	public static TouchpadStyle touchpadStyle;
	public static Drawable touchBackground;
	public static Drawable touchKnob;

	public static Texture hudTexture;
	public static TextureRegionDrawable emptySlot;
	public static TextureRegionDrawable bat;
	public static TextureRegionDrawable disarmTrap;

	public static Texture logoTexture;
	public static TextureRegion logo;
	public static TextButtonStyle normal;
	public static BitmapFont basker32black;
	public static BitmapFont basker45black;
	public static Drawable buttonUp;
	public static Drawable buttonDown;

	public static LabelStyle title;

	public static Skin touchpadSkin;
	public static Skin menuSkin;

	public static void load() {

		int screenWidth = (murdermansion.V_WIDTH * murdermansion.SCALE);

		logoTexture = new Texture(Gdx.files.internal("data/logo.png"));
		logoTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		logo = new TextureRegion(logoTexture);

		// Create new skin for menu screen
		menuSkin = new Skin();
		// Set menu font
		menuSkin.add("basker32", new BitmapFont(Gdx.files.internal("Fonts/Basker32.fnt")));
		menuSkin.add("basker45", new BitmapFont(Gdx.files.internal("Fonts/Baskek45.fnt")));
		// Set menu buttons
		menuSkin.add("buttonUp", new Texture("data/butt1.png"));
		menuSkin.add("buttonDown", new Texture("data/butt2.png"));
		// Create Text button Style
		normal = new TextButtonStyle();
		normal.font = menuSkin.getFont("basker32");
		normal.font.scale((Gdx.graphics.getWidth() - screenWidth) / screenWidth);
		normal.up = menuSkin.getDrawable("buttonUp");
		normal.down = menuSkin.getDrawable("buttonDown");
		normal.pressedOffsetY = -4;
		// Set label style
		title = new LabelStyle();
		title.font = menuSkin.getFont("basker45");
		title.font.scale((Gdx.graphics.getWidth() - screenWidth) / screenWidth);

		// Create a touchpad
		touchpadSkin = new Skin();
		touchpadSkin.add("touchBackground", new Texture("data/touchBackground.png"));
		touchpadSkin.add("touchKnob", new Texture("data/touchKnob.png"));
		touchBackground = touchpadSkin.getDrawable("touchBackground");
		touchKnob = touchpadSkin.getDrawable("touchKnob");
		touchpadStyle = new TouchpadStyle();
		touchpadStyle.background = touchBackground;
		touchpadStyle.knob = touchKnob;
		touchpad = new Touchpad(5, touchpadStyle);

		hudTexture = new Texture(Gdx.files.internal("data/MM-PS-HUD.png"));
		emptySlot = new TextureRegionDrawable(new TextureRegion(hudTexture, 0, 0, 46, 46));
		disarmTrap = new TextureRegionDrawable(new TextureRegion(hudTexture, 50, 0, 46, 46));
		bat = new TextureRegionDrawable(new TextureRegion(hudTexture, 100, 0, 46, 46));
	}

	public static void dispose() {
		// We must dispose of the texture when we are finished.
		menuSkin.dispose();
		logoTexture.dispose();
		touchpadSkin.dispose();
		hudTexture.dispose();
	}
}
