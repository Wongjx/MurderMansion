package com.jkjk.MMHelpers;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
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
	public static Texture pause_button_tex;
	public static TextureRegionDrawable pause_button_draw;
	
	//PAUSE SCREEN
	public static Texture pause_main;

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
	public static Texture mur_swap_C_tex;
	public static TextureRegionDrawable mur_swap_C_draw;
	public static Texture mur_swap_M_tex;
	public static TextureRegionDrawable mur_swap_M_draw;

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
	
	public static Texture civ_dead_lines;

	// GENERAL ITEMS
	public static Texture plantedTrapTexture;
	public static Animation plantedTrapAnimation;
	public static Texture restingTrapTexture;
	public static Animation restingTrapAnimation;
	public static Texture disarmTrapSpriteTexture;
	public static Animation disarmTrapSpriteAnimation;
	public static Texture batSpriteTexture;
	public static Animation batSpriteAnimation;
	public static Texture knifeSpriteTexture;
	public static Animation knifeSpriteAnimation;
	public static Texture shotgunPartTexture;
	public static Animation shotgunPartSpriteAnimation;

	// Character Animations and Textures
	public static final int NUM_CIVILIAN_TEXTURES = 3; //starting from zero.
	
	public static Texture cooldownTexture;
	public static Animation PanicCoolDownAnimation;
	public static Animation DisguiseCoolDownAnimation;
	public static Animation WeaponsCoolDownAnimation;
	public static Texture civilianTexture0;
	public static Texture civilianTexture1;
	public static Texture civilianTexture2;
	public static Texture civilianTexture3;
	public static TextureRegion civ_rest0;
	public static Animation civAnimation0;
	public static Animation civDisarmAnimation0;
	public static Animation civDropDisarmAnimation0;
	public static Animation civBatAnimation0;
//	public static Animation civTrapDeathAnimation;
//	public static Animation civKnifeDeathAnimation;
	public static Animation civStunAnimation0;
	public static Animation civPanicAnimation0;
	public static TextureRegion civ_panic_rest0;
	public static Animation civShotgunAnimation0;
	
	public static TextureRegion civ_rest1;
	public static Animation civAnimation1;
	public static Animation civDisarmAnimation1;
	public static Animation civDropDisarmAnimation1;
	public static Animation civBatAnimation1;
//	public static Animation civTrapDeathAnimation;
//	public static Animation civKnifeDeathAnimation;
	public static Animation civStunAnimation1;
	public static Animation civPanicAnimation1;
	public static TextureRegion civ_panic_rest1;
	public static Animation civShotgunAnimation1;
	
	public static TextureRegion civ_rest2;
	public static Animation civAnimation2;
	public static Animation civDisarmAnimation2;
	public static Animation civDropDisarmAnimation2;
	public static Animation civBatAnimation2;
//	public static Animation civTrapDeathAnimation;
//	public static Animation civKnifeDeathAnimation;
	public static Animation civStunAnimation2;
	public static Animation civPanicAnimation2;
	public static TextureRegion civ_panic_rest2;
	public static Animation civShotgunAnimation2;
	
	public static TextureRegion civ_rest3;
	public static Animation civAnimation3;
	public static Animation civDisarmAnimation3;
	public static Animation civDropDisarmAnimation3;
	public static Animation civBatAnimation3;
//	public static Animation civTrapDeathAnimation;
//	public static Animation civKnifeDeathAnimation;
	public static Animation civStunAnimation3;
	public static Animation civPanicAnimation3;
	public static TextureRegion civ_panic_rest3;
	public static Animation civShotgunAnimation3;
	
	public static Texture murderer;
	//public static Texture mur_walk;
	public static Animation murAnimation;
	//public static Texture mur_knife;
	public static Animation murKnifeAnimation;
	//public static Texture mur_plantTrap;
	public static Animation murPlantTrapAnimation;
	//public static Texture mur_death;
	public static Animation murDeathAnimation;
	//public static Texture mur_civTransformation;
	public static Animation murToCivAnimation;
	//public static Texture civ_murTransformation;
	public static Animation civToMurAnimation;
	//public static Texture mur_stun;
	public static Animation murStunAnimation;
	
	public static Texture ghost_haunt;
	public static Animation ghostHauntAnimation;
	public static Texture ghost_float;
	public static Animation ghostFloatAnimation;
	
	// OBSTACLES
	public static Texture obstacle;
	public static Texture main_door;

	public static TextureRegion prohibitedButton;
	
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

		// PAUSE SCREEN
		pause_main = new Texture(Gdx.files.internal("paused_screen/main.png"));
		
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
		pause_button_tex = new Texture(Gdx.files.internal("HUD/pause_button.png"));
		pause_button_draw = new TextureRegionDrawable(new TextureRegion(pause_button_tex));
		

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
		mur_swap_C_tex = new Texture(Gdx.files.internal("HUD/mur_swap_C.png"));
		mur_swap_C_draw = new TextureRegionDrawable(new TextureRegion(mur_swap_C_tex));
		mur_swap_M_tex = new Texture(Gdx.files.internal("HUD/mur_swap_M.png"));
		mur_swap_M_draw = new TextureRegionDrawable(new TextureRegion(mur_swap_M_tex));

		// TIMER
		time = new Texture(Gdx.files.internal("HUD/countdown.png"));
		time.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		weapon_parts_counter = new Texture(Gdx.files.internal("HUD/weapon_parts_counter.png"));
		weapon_parts_counter.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// MAP
		tiledMap = new TmxMapLoader().load("map/mansion2.tmx");

		// PICK UP ITEM TEXTURES
		plantedTrapTexture = new Texture(Gdx.files.internal("gamehelper/planted_trap_animation.png"));
		plantedTrapTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] planted_trap = TextureRegion.split(plantedTrapTexture, 120, 120)[0];
		plantedTrapAnimation = new Animation(0.4f, planted_trap);
		plantedTrapAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		restingTrapTexture = new Texture(Gdx.files.internal("gamehelper/resting_trap_animation.png"));
		restingTrapTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] trap_sprite = TextureRegion.split(restingTrapTexture, 120, 120)[0];
		restingTrapAnimation = new Animation(0.4f, trap_sprite);
		restingTrapAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		disarmTrapSpriteTexture = new Texture(Gdx.files.internal("gamehelper/disarm_sprite_animation.png"));
		disarmTrapSpriteTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] disarm_trap = TextureRegion.split(disarmTrapSpriteTexture, 120, 120)[0];
		disarmTrapSpriteAnimation = new Animation(0.4f, disarm_trap);
		disarmTrapSpriteAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		batSpriteTexture = new Texture(Gdx.files.internal("gamehelper/bat_sprite_animation.png"));
		batSpriteTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] bat_sprite = TextureRegion.split(batSpriteTexture, 120, 120)[0];
		batSpriteAnimation = new Animation(0.4f, bat_sprite);
		batSpriteAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		knifeSpriteTexture = new Texture(Gdx.files.internal("gamehelper/knife_sprite_animation.png"));
		knifeSpriteTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] knife_sprite = TextureRegion.split(knifeSpriteTexture, 120, 120)[0];
		knifeSpriteAnimation = new Animation(0.4f, knife_sprite);
		knifeSpriteAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		shotgunPartTexture = new Texture(Gdx.files.internal("gamehelper/shotgun_sprite_animation.png"));
		shotgunPartTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] shotgun_sprite = TextureRegion.split(shotgunPartTexture, 120, 120)[0];
		shotgunPartSpriteAnimation = new Animation(0.4f, shotgun_sprite);
		shotgunPartSpriteAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

		// CIVILIAN ANIMATIONS AND TEXTURE
		civilianTexture0 = new Texture(Gdx.files.internal("animation/CIV0.png"));
		civilianTexture0.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[][] civilianTR0 = TextureRegion.split(civilianTexture0, 250, 250);
		
		civAnimation0 = new Animation(0.9f, Arrays.copyOfRange(civilianTR0[0],0,3));
		civAnimation0.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		civ_rest0 = civilianTR0[0][1];
		civ_dead_lines = new Texture(Gdx.files.internal("gamehelper/dead_lines.png"));

		civPanicAnimation0 = new Animation(0.5f, Arrays.copyOfRange(civilianTR0[1],0,3));
		civPanicAnimation0.setPlayMode(PlayMode.LOOP);
		civ_panic_rest0 = civilianTR0[1][0];
		
		civStunAnimation0 = new Animation(0.5f, Arrays.copyOfRange(civilianTR0[2],0,3));
		civStunAnimation0.setPlayMode(PlayMode.NORMAL);
		
		civBatAnimation0 = new Animation(0.1f, Arrays.copyOfRange(civilianTR0[3],0,3));
		civBatAnimation0.setPlayMode(PlayMode.NORMAL);
		
		civShotgunAnimation0 = new Animation(0.2f, Arrays.copyOfRange(civilianTR0[4],0,3));
		civShotgunAnimation0.setPlayMode(PlayMode.NORMAL);
		
		civDisarmAnimation0 = new Animation(0.3f, civilianTR0[5]);
		civDisarmAnimation0.setPlayMode(PlayMode.NORMAL);
		
		civDropDisarmAnimation0 = new Animation(0.1f,civilianTR0[5]);
		civDropDisarmAnimation0.setPlayMode(PlayMode.NORMAL);
		
		civilianTexture1 = new Texture(Gdx.files.internal("animation/CIV1.png"));
		civilianTexture1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[][] civilianTR1 = TextureRegion.split(civilianTexture1, 250, 250);
		
		civAnimation1 = new Animation(0.9f, Arrays.copyOfRange(civilianTR1[0],0,3));
		civAnimation1.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		civ_rest1 = civilianTR1[0][1];
		//civ_dead_lines = new Texture(Gdx.files.internal("gamehelper/dead_lines.png"));

		civPanicAnimation1 = new Animation(0.5f, Arrays.copyOfRange(civilianTR1[1],0,3));
		civPanicAnimation1.setPlayMode(PlayMode.LOOP);
		civ_panic_rest1 = civilianTR1[1][0];
		
		civStunAnimation1 = new Animation(0.5f, Arrays.copyOfRange(civilianTR1[2],0,3));
		civStunAnimation1.setPlayMode(PlayMode.NORMAL);
		
		civBatAnimation1 = new Animation(0.1f, Arrays.copyOfRange(civilianTR1[3],0,3));
		civBatAnimation1.setPlayMode(PlayMode.NORMAL);
		
		civShotgunAnimation1 = new Animation(0.2f, Arrays.copyOfRange(civilianTR1[4],0,3));
		civShotgunAnimation1.setPlayMode(PlayMode.NORMAL);
		
		civDisarmAnimation1 = new Animation(0.3f, civilianTR1[5]);
		civDisarmAnimation1.setPlayMode(PlayMode.NORMAL);
		
		civDropDisarmAnimation1 = new Animation(0.1f,civilianTR1[5]);
		civDropDisarmAnimation1.setPlayMode(PlayMode.NORMAL);
		
		
		civilianTexture2 = new Texture(Gdx.files.internal("animation/CIV2.png"));
		civilianTexture2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[][] civilianTR2 = TextureRegion.split(civilianTexture2, 250, 250);
		
		civAnimation2 = new Animation(0.9f, Arrays.copyOfRange(civilianTR2[0],0,3));
		civAnimation2.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		civ_rest2 = civilianTR2[0][1];
		//civ_dead_lines = new Texture(Gdx.files.internal("gamehelper/dead_lines.png"));

		civPanicAnimation2 = new Animation(0.5f, Arrays.copyOfRange(civilianTR2[1],0,3));
		civPanicAnimation2.setPlayMode(PlayMode.LOOP);
		civ_panic_rest2 = civilianTR2[1][0];
		
		civStunAnimation2 = new Animation(0.5f, Arrays.copyOfRange(civilianTR2[2],0,3));
		civStunAnimation2.setPlayMode(PlayMode.NORMAL);
		
		civBatAnimation2 = new Animation(0.1f, Arrays.copyOfRange(civilianTR2[3],0,3));
		civBatAnimation2.setPlayMode(PlayMode.NORMAL);
		
		civShotgunAnimation2 = new Animation(0.2f, Arrays.copyOfRange(civilianTR2[4],0,3));
		civShotgunAnimation2.setPlayMode(PlayMode.NORMAL);
		
		civDisarmAnimation2 = new Animation(0.3f, civilianTR2[5]);
		civDisarmAnimation2.setPlayMode(PlayMode.NORMAL);
		
		civDropDisarmAnimation2 = new Animation(0.1f,civilianTR2[5]);
		civDropDisarmAnimation2.setPlayMode(PlayMode.NORMAL);
		
		
		civilianTexture3 = new Texture(Gdx.files.internal("animation/CIV3.png"));
		civilianTexture3.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[][] civilianTR3 = TextureRegion.split(civilianTexture3, 250, 250);
		
		civAnimation3 = new Animation(0.9f, Arrays.copyOfRange(civilianTR3[0],0,3));
		civAnimation3.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		civ_rest3 = civilianTR3[0][1];
		//civ_dead_lines = new Texture(Gdx.files.internal("gamehelper/dead_lines.png"));

		civPanicAnimation3 = new Animation(0.5f, Arrays.copyOfRange(civilianTR3[1],0,3));
		civPanicAnimation3.setPlayMode(PlayMode.LOOP);
		civ_panic_rest3 = civilianTR3[1][0];
		
		civStunAnimation3 = new Animation(0.5f, Arrays.copyOfRange(civilianTR3[2],0,3));
		civStunAnimation3.setPlayMode(PlayMode.NORMAL);
		
		civBatAnimation3 = new Animation(0.1f, Arrays.copyOfRange(civilianTR3[3],0,3));
		civBatAnimation3.setPlayMode(PlayMode.NORMAL);
		
		civShotgunAnimation3 = new Animation(0.2f, Arrays.copyOfRange(civilianTR3[4],0,3));
		civShotgunAnimation3.setPlayMode(PlayMode.NORMAL);
		
		civDisarmAnimation3 = new Animation(0.3f, civilianTR3[5]);
		civDisarmAnimation3.setPlayMode(PlayMode.NORMAL);
		
		civDropDisarmAnimation3 = new Animation(0.1f,civilianTR3[5]);
		civDropDisarmAnimation3.setPlayMode(PlayMode.NORMAL);
		

		murderer = new Texture(Gdx.files.internal("animation/MUR.png"));
		murderer.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[][] murTR = TextureRegion.split(murderer, 250, 250);
		
		murAnimation = new Animation(0.9f, Arrays.copyOfRange(murTR[0],0,3));
		murAnimation.setPlayMode(PlayMode.LOOP_PINGPONG);
		mur_rest = murTR[0][1];
		
		murKnifeAnimation = new Animation(0.2f, Arrays.copyOfRange(murTR[1],0,3));
		murKnifeAnimation.setPlayMode(PlayMode.NORMAL);
		
		
		// HUD COOLDOWN
		cooldownTexture = new Texture(Gdx.files.internal("animation/cooldown_animation.png"));
		cooldownTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] cooldown = TextureRegion.split(cooldownTexture, 50, 50)[0];
		prohibitedButton = cooldown[0];
		DisguiseCoolDownAnimation = new Animation(8.3f, cooldown);
		DisguiseCoolDownAnimation.setPlayMode(PlayMode.NORMAL);
		PanicCoolDownAnimation = new Animation(50f, cooldown);
		PanicCoolDownAnimation.setPlayMode(PlayMode.NORMAL);
		WeaponsCoolDownAnimation = new Animation(0.83f, cooldown);
		WeaponsCoolDownAnimation.setPlayMode(PlayMode.NORMAL);
		// ghost 
		ghost_float = new Texture(Gdx.files.internal("animation/ghostSingleFrame.png"));
		
		// OBSTACLES
		obstacle = new Texture(Gdx.files.internal("map/barrels.png"));
		main_door = new Texture(Gdx.files.internal("map/main-door.png"));
	}

	public static void dispose() {
		// We must dispose of the texture when we are finished.
		menuSkin.dispose();
		logoTexture.dispose();
		touchpadSkin.dispose();
		menuBackground.dispose();
		pause_main.dispose();
		cooldownTexture.dispose();
		time.dispose();
		weapon_parts_counter.dispose();
		tiledMap.dispose();
		emptySlot.dispose();
		pause_button_tex.dispose();
		civ_weapon_bat_tex.dispose();
		civ_item_tex.dispose();
		civ_dash_tex.dispose();
		mur_weapon_tex.dispose();
		mur_item_tex.dispose();
		mur_swap_C_tex.dispose();
		mur_swap_M_tex.dispose();
		civilianTexture0.dispose();
		civilianTexture1.dispose();
		civilianTexture2.dispose();
		civ_dead_lines.dispose();
		// ghost_haunt.dispose();
		ghost_float.dispose();
		plantedTrapTexture.dispose();
		restingTrapTexture.dispose();
		disarmTrapSpriteTexture.dispose();
		batSpriteTexture.dispose();
		knifeSpriteTexture.dispose();
		shotgunPartTexture.dispose();
	}
}
