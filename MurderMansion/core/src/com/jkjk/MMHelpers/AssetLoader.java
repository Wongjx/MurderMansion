package com.jkjk.MMHelpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
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
import com.jkjk.MurderMansion.MurderMansion;

public class AssetLoader {

	public static Texture menuBackground;

	public static Touchpad touchpad;
	public static TouchpadStyle touchpadStyle;
	public static Drawable touchBackground;
	public static Drawable touchKnob;
	public static Texture emptySlot;

	// CIVILIAN
	public static Texture civ_weapon_bat_tex;
	public static TextureRegionDrawable civ_weapon_bat_draw;
	public static Texture civ_weapon_gun_tex;
	public static TextureRegionDrawable civ_weapon_gun_draw;
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
	public static Texture weapon_parts_counter;

	public static TextureRegion mur_rest;
	public static TextureRegion civ_rest;
	public static Texture civ_dead_lines;

	// GENERAL ITEMS
	public static Texture plantedTrapTexture;
	public static Texture trapSpriteTexture;
	public static Texture disarmTrapSpriteTexture;
	public static Texture batSpriteTexture;
	public static Texture knifeSpriteTexture;
	public static Texture shotgunPartTexture;

	// Character Animations and Textures
	public static Texture civ_walk;
	public static Animation civAnimation;
	public static Texture civ_disarm;
	public static Animation civDisarmAnimation;
	public static Texture civ_bat;
	public static Animation civBatAnimation;
	public static Texture civ_trapDeath;
	public static Animation civTrapDeathAnimation;
	public static Texture civ_knifeDeath;
	public static Animation civKnifeDeathAnimation;
	public static Texture civ_stun;
	public static Animation civStunAnimation;
	public static Texture civ_panic;
	public static Animation civPanicAnimation;
	public static Texture civ_shotgun;
	public static Animation civShotgunAnimation;
	public static Texture mur_walk;
	public static Animation murAnimation;
	public static Texture mur_knife;
	public static Animation murKnifeAnimation;
	public static Texture mur_plantTrap;
	public static Animation murPlantTrapAnimation;
	public static Texture mur_death;
	public static Animation murDeathAnimation;
	public static Texture mur_civTransformation;
	public static Animation murToCivAnimation;
	public static Texture civ_murTransformation;
	public static Animation civToMurAnimation;
	public static Texture ghost_haunt;
	public static Animation ghostHauntAnimation;
	public static Texture ghost_float;
	public static Animation ghostFloatAnimation;

	public static void load() {

		int gameWidth = (MurderMansion.V_WIDTH * MurderMansion.SCALE);

		menuBackground = new Texture(Gdx.files.internal("basic/menu.png"));

		basker32black = new BitmapFont(Gdx.files.internal("Fonts/Basker32.fnt"));
		basker45black = new BitmapFont(Gdx.files.internal("Fonts/Baskek45.fnt"));
		basker32blackTime = new BitmapFont(Gdx.files.internal("Fonts/Basker32.fnt"));
		basker32blackTime.scale((Gdx.graphics.getWidth() - gameWidth) / gameWidth / 3);

		logoTexture = new Texture(Gdx.files.internal("basic/logo.png"));
		logoTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		logo = new TextureRegion(logoTexture);

		// Create new skin for menu screen
		menuSkin = new Skin();
		// Set menu font
		menuSkin.add("basker32", basker32black);
		menuSkin.add("basker45", basker45black);
		// Set menu buttons
		menuSkin.add("buttonUp", new Texture("basic/butt1.png"));
		menuSkin.add("buttonDown", new Texture("basic/butt2.png"));
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
		touchpadSkin.add("touchBackground", new Texture("HUD/touchBackground.png"));
		touchpadSkin.add("touchKnob", new Texture("HUD/touchKnob.png"));
		touchBackground = touchpadSkin.getDrawable("touchBackground");
		touchKnob = touchpadSkin.getDrawable("touchKnob");
		touchpadStyle = new TouchpadStyle();
		touchpadStyle.background = touchBackground;
		touchpadStyle.knob = touchKnob;
		touchpad = new Touchpad(5, touchpadStyle);

		emptySlot = new Texture(Gdx.files.internal("HUD/slots.png"));

		// CIVILIANS HUD
		civ_weapon_bat_tex = new Texture(Gdx.files.internal("HUD/civ_weapon_bat.png"));
		civ_weapon_bat_draw = new TextureRegionDrawable(new TextureRegion(civ_weapon_bat_tex));
		civ_item_tex = new Texture(Gdx.files.internal("HUD/civ_item.png"));
		civ_item_draw = new TextureRegionDrawable(new TextureRegion(civ_item_tex));
		civ_dash_tex = new Texture(Gdx.files.internal("HUD/civ_dash.png"));
		civ_dash_draw = new TextureRegionDrawable(new TextureRegion(civ_dash_tex));
		civ_weapon_gun_tex = new Texture(Gdx.files.internal("HUD/civ_weapon_gun.png"));
		civ_weapon_gun_draw = new TextureRegionDrawable(new TextureRegion(civ_weapon_gun_tex));

		// MURDERER HUD
		mur_weapon_tex = new Texture(Gdx.files.internal("HUD/mur_weapon.png"));
		mur_weapon_draw = new TextureRegionDrawable(new TextureRegion(mur_weapon_tex));
		mur_item_tex = new Texture(Gdx.files.internal("HUD/mur_item.png"));
		mur_item_draw = new TextureRegionDrawable(new TextureRegion(mur_item_tex));
		mur_swap_tex = new Texture(Gdx.files.internal("HUD/mur_swap.png"));
		mur_swap_draw = new TextureRegionDrawable(new TextureRegion(mur_swap_tex));

		// TIMER
		time = new Texture(Gdx.files.internal("HUD/countdown.png"));
		time.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		weapon_parts_counter = new Texture(Gdx.files.internal("HUD/weapon_parts_counter.png"));
		weapon_parts_counter.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// MAP
		tiledMap = new TmxMapLoader().load("map/mansion2.tmx");

		// PICK UP ITEM TEXTURES
		plantedTrapTexture = new Texture(Gdx.files.internal("gamehelper/planted_trap.png"));
		trapSpriteTexture = new Texture(Gdx.files.internal("gamehelper/resting_trap.png"));
		disarmTrapSpriteTexture = new Texture(Gdx.files.internal("gamehelper/disarm.png"));
		batSpriteTexture = new Texture(Gdx.files.internal("gamehelper/knife.png"));
		knifeSpriteTexture = new Texture(Gdx.files.internal("gamehelper/knife.png"));
		shotgunPartTexture = new Texture(Gdx.files.internal("gamehelper/shotgun.png"));

		// CIVILIAN ANIMATIONS AND TEXTURE
		civ_walk = new Texture(Gdx.files.internal("animation/walk_animation_civ.png"));
		civ_walk.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] civilians = TextureRegion.split(civ_walk, 1800, 1800)[0];
		civAnimation = new Animation(0.9f, civilians);
		civAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		civ_rest = civilians[1];
		civ_dead_lines = new Texture(Gdx.files.internal("gamehelper/dead_lines.png"));

		civ_bat = new Texture(Gdx.files.internal("animation/bat_animation.png"));
		civ_bat.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] bat_swing = TextureRegion.split(civ_bat, 1800, 1800)[0];
		civBatAnimation = new Animation(1f, bat_swing);
		civBatAnimation.setPlayMode(PlayMode.NORMAL);
		
		civ_disarm = new Texture(Gdx.files.internal("animation/disarm_animation.png"));
		civ_disarm.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] disarm_swing = TextureRegion.split(civ_disarm, 1800, 1800)[0];
		civDisarmAnimation = new Animation(1f, disarm_swing);
		civDisarmAnimation.setPlayMode(PlayMode.NORMAL);
		
		
		civ_panic= new Texture(Gdx.files.internal("animation/panic_animation.png"));
		civ_panic.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] panic_region = TextureRegion.split(civ_panic, 1800, 1800)[0];
		civPanicAnimation = new Animation(1f, panic_region);
		civPanicAnimation.setPlayMode(PlayMode.NORMAL);
		
		civ_shotgun= new Texture(Gdx.files.internal("animation/shotgun_animation.png"));
		civ_shotgun.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] shotgun_blast = TextureRegion.split(civ_panic, 1800, 1800)[0];
		civShotgunAnimation = new Animation(1f, shotgun_blast);
		civShotgunAnimation.setPlayMode(PlayMode.NORMAL);
		
		civ_stun= new Texture(Gdx.files.internal("animation/stun_animation_civ.png"));
		civ_stun.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] stun_region = TextureRegion.split(civ_panic, 1800, 1800)[0];
		civStunAnimation = new Animation(1f, stun_region);
		civStunAnimation.setPlayMode(PlayMode.NORMAL);

		// ghost 
		ghost_float = new Texture(Gdx.files.internal("animation/ghostSingleFrame.png"));
	}

	public static void dispose() {
		// We must dispose of the texture when we are finished.
		menuSkin.dispose();
		logoTexture.dispose();
		touchpadSkin.dispose();
		menuBackground.dispose();
		time.dispose();
		weapon_parts_counter.dispose();
		tiledMap.dispose();
		emptySlot.dispose();
		civ_weapon_bat_tex.dispose();
		civ_item_tex.dispose();
		civ_dash_tex.dispose();
		mur_weapon_tex.dispose();
		mur_item_tex.dispose();
		mur_swap_tex.dispose();
		civ_walk.dispose();
		civ_dead_lines.dispose();
		civ_bat.dispose();
		// civ_bat.dispose();
		// civ_disarm.dispose();
		// civ_knifeDeath.dispose();
		// civ_trapDeath.dispose();
		// civ_stun.dispose();
		// civ_panic.dispose();
		// civ_shotgun.dispose();
		// mur_walk.dispose();
		// mur_knife.dispose();
		// mur_plantTrap.dispose();
		// mur_death.dispose();
		// mur_civTransformation.dispose();
		// civ_murTransformation.dispose();
		// ghost_haunt.dispose();
		ghost_float.dispose();
	}
}
