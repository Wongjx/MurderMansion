package com.jkjk.MMHelpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class AssetLoader {
	public static Touchpad touchpad;
	public static TouchpadStyle touchpadStyle;
	public static Drawable touchBackground;
	public static Drawable touchKnob;
	
	public static TextButtonStyle normal;
	public static BitmapFont basker32black;
	public static BitmapFont basker45black;
	public static Drawable buttonUp;
	public static Drawable buttonDown;
	
	public static LabelStyle title;
	
	public static Skin touchpadSkin;
	public static Skin menuSkin;
	
	public static Texture blockTexture;
	public static Sprite blockSprite;
	public static Sprite blockSprite2;
	public static Sprite blockSprite3;
	
	public static void load() {
		//Create new skin for menu screen
		menuSkin=new Skin();
		//Set menu font
		menuSkin.add("basker32", new BitmapFont(Gdx.files.internal("Fonts/Basker32.fnt")));
		menuSkin.add("basker45", new BitmapFont(Gdx.files.internal("Fonts/Baskek45.fnt")));
		//Set menu buttons
		menuSkin.add("buttonUp", new Texture("data/butt1.png"));
		menuSkin.add("buttonDown", new Texture("data/butt2.png"));
		//Create Text button Style
		normal = new TextButtonStyle();
		normal.font=menuSkin.getFont("basker32");
		normal.up=menuSkin.getDrawable("buttonUp");
		normal.down=menuSkin.getDrawable("buttonDown");
		normal.pressedOffsetY=-4;
		//Set label style
		title=new LabelStyle();
		title.font=menuSkin.getFont("basker45");
		
		// Create a touchpad skin
		touchpadSkin = new Skin();
		// Set background image
		touchpadSkin.add("touchBackground", new Texture("data/touchBackground.png"));
		// Set knob image
		touchpadSkin.add("touchKnob", new Texture("data/touchKnob.png"));
		// Create TouchPad Style
		touchpadStyle = new TouchpadStyle();
		// Create Drawable's from TouchPad skin
		touchBackground = touchpadSkin.getDrawable("touchBackground");
		touchKnob = touchpadSkin.getDrawable("touchKnob");
		// Apply the Drawables to the TouchPad Style
		touchpadStyle.background = touchBackground;
		touchpadStyle.knob = touchKnob;
		// Create new TouchPad with the created style
		touchpad = new Touchpad(10, touchpadStyle);
		
		// Create block sprite
		blockTexture = new Texture(Gdx.files.internal("data/block.png"));
		blockSprite = new Sprite(blockTexture);
		blockSprite2 = new Sprite(blockTexture);
		blockSprite3 = new Sprite(blockTexture);
	}

	public static void dispose() {
		// We must dispose of the texture when we are finished.
		menuSkin.dispose();
		touchpadSkin.dispose();
		blockTexture.dispose();
	}
}
