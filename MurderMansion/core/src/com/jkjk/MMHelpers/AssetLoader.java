package com.jkjk.MMHelpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jkjk.MurderMansion.murdermansion;

public class AssetLoader {
	
	public static Texture  menuBackground;
	
	public static Touchpad touchpad;
	public static TouchpadStyle touchpadStyle;
	public static Drawable touchBackground;
	public static Drawable touchKnob;
	public static Texture emptySlot;
	
	// CIVILIAN
	public static Texture civ_weapon_bat_tex;
	public static TextureRegionDrawable civ_weapon_bat_draw;
	public static Texture civ_item_tex;
	public static TextureRegionDrawable civ_item_draw;
	public static Texture civ_dash_tex;
	public static TextureRegionDrawable civ_dash_draw;
	
	// MURDERER
	public static Texture mur_weapon_tex;
	public static TextureRegionDrawable mur_weapon_draw;
	public static Texture mur_item_tex;
	public static TextureRegionDrawable mur_item_draw;
	public static Texture mur_swap_tex;
	public static TextureRegionDrawable mur_swap_draw;
	

	public static Texture logoTexture;
	public static TextureRegion logo;
	public static TextButtonStyle normal;
	public static BitmapFont basker32black;
	public static BitmapFont basker45black;
	public static BitmapFont basker32blackTime;
	public static Drawable buttonUp;
	public static Drawable buttonDown;

	public static LabelStyle title;

	public static Skin touchpadSkin;
	public static Skin menuSkin;
	
	public static TiledMap tiledMap;
	
	public static Texture time;
	public static Texture civ_profile;
	
	private static LabelStyle countdown;

	public static void load() {

		int gameWidth = (murdermansion.V_WIDTH * murdermansion.SCALE);

		menuBackground = new Texture(Gdx.files.internal("data/menu.png"));
		
		basker32black = new BitmapFont(Gdx.files.internal("Fonts/Basker32.fnt"));
		basker45black = new BitmapFont(Gdx.files.internal("Fonts/Baskek45.fnt"));
		basker32blackTime = new BitmapFont(Gdx.files.internal("Fonts/Basker32.fnt"));
		basker32blackTime.scale((Gdx.graphics.getWidth() - gameWidth) / gameWidth /3);
		
		logoTexture = new Texture(Gdx.files.internal("data/logo.png"));
		logoTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		logo = new TextureRegion(logoTexture);

		// Create new skin for menu screen
		menuSkin = new Skin();
		// Set menu font
		menuSkin.add("basker32", basker32black );
		menuSkin.add("basker45", basker45black );
		// Set menu buttons
		menuSkin.add("buttonUp", new Texture("data/butt1.png"));
		menuSkin.add("buttonDown", new Texture("data/butt2.png"));
		// Create Text button Style
		normal = new TextButtonStyle();
		normal.font = menuSkin.getFont("basker32");
		normal.font.scale((Gdx.graphics.getWidth() - gameWidth) / gameWidth);
		normal.up = menuSkin.getDrawable("buttonUp");
		normal.down = menuSkin.getDrawable("buttonDown");
		normal.pressedOffsetY = -4;
		// Set label style for title
		title = new LabelStyle();
		title.font = menuSkin.getFont("basker45");
		title.font.scale((Gdx.graphics.getWidth() - gameWidth) / gameWidth);

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

		emptySlot = new Texture (Gdx.files.internal("data/slots.png"));
		
		//CIVILIANS
		civ_weapon_bat_tex = new Texture(Gdx.files.internal("data/civ_weapon_bat.png"));
		civ_weapon_bat_draw = new TextureRegionDrawable(new TextureRegion(civ_weapon_bat_tex));
		civ_item_tex = new Texture(Gdx.files.internal("data/civ_item.png"));
		civ_item_draw = new TextureRegionDrawable (new TextureRegion(civ_item_tex));
		civ_dash_tex = new Texture(Gdx.files.internal("data/civ_dash.png"));
		civ_dash_draw = new TextureRegionDrawable (new TextureRegion(civ_dash_tex));
		
		// MURDERER
		mur_weapon_tex = new Texture(Gdx.files.internal("data/mur_weapon.png"));
		mur_weapon_draw = new TextureRegionDrawable(new TextureRegion(mur_weapon_tex));
		mur_item_tex = new Texture(Gdx.files.internal("data/mur_item.png"));
		mur_item_draw = new TextureRegionDrawable (new TextureRegion(mur_item_tex));
		mur_swap_tex = new Texture(Gdx.files.internal("data/mur_swap.png"));
		mur_swap_draw = new TextureRegionDrawable (new TextureRegion(mur_swap_tex));
		
		
		time = new Texture(Gdx.files.internal("data/countdown.png"));
		time.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		civ_profile = new Texture(Gdx.files.internal("data/civ_profile.png"));
		civ_profile.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		tiledMap = new TmxMapLoader().load("map/mansion2.tmx");
	}

	public static void dispose() {
		// We must dispose of the texture when we are finished.
		menuSkin.dispose();
		logoTexture.dispose();
		touchpadSkin.dispose();
		menuBackground.dispose();
		time.dispose();
		civ_profile.dispose();
		tiledMap.dispose();
		emptySlot.dispose();
		civ_weapon_bat_tex.dispose();
		civ_item_tex.dispose();
		civ_dash_tex.dispose();
		mur_weapon_tex.dispose();
		mur_item_tex.dispose();
		mur_swap_tex.dispose();
	}
}
