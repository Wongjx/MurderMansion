package com.jkjk.MMHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
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

	public static TextureRegion prohibitedButton;
	private static int gameWidth;

	public static Texture menuBackground;

	public static Touchpad touchpad;
	public static TouchpadStyle touchpadStyle;
	public static Drawable touchBackground;
	public static Drawable touchKnob;
	public static Texture emptySlot;
	public static Texture settings_button_tex;
	public static TextureRegionDrawable settings_button_draw;
	public static Texture settings_cancel_tex;
	public static TextureRegionDrawable settings_cancel_draw;
	public static Texture soundoff_tex;
	public static TextureRegionDrawable soundoff_draw;
	public static Texture soundon_tex;
	public static TextureRegionDrawable soundon_draw;
	
	public static Texture muteButton;
	public static Texture unmuteButton;

	// PAUSE SCREEN
	public static Texture pause_main;
	public static TextButtonStyle normalSettings;

	// SCORE SCREEN
	public static Texture score_background_texture;
	public static Animation score_background_animation;
	public static Texture score_texture;
	public static Skin scoreSkin;
	public static ImageButtonStyle normal1;
	public static Texture rip;
	public static Texture civ_char0;
	public static Texture civ_char1;
	public static Texture civ_char2;
	public static Texture civ_char3;
	public static Texture mur_char;
	public static LabelStyle scoreLabelStyle;

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

	// GHOST
	public static Texture haunt_tex;
	public static TextureRegionDrawable haunt_draw;

	public static Texture logoTexture;
	public static TextureRegion logo;
	public static TextButtonStyle normal;
	
	public static BitmapFont crimesFont36;
	public static BitmapFont crimesFont48;
	public static BitmapFont crimesFont36Time;
	public static BitmapFont crimesFont36Sync;
	public static BitmapFont crimesFont36Settings;
	public static BitmapFont crimesFont36Message;
	public static BitmapFont crimesFont36Black;
	public static BitmapFont basker32Message;
	
	public static Drawable buttonUp;
	public static Drawable buttonDown;

	public static LabelStyle title;
	public static Label message;

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
	public static final int NUM_CIVILIAN_TEXTURES = 3; // starting from zero.

	public static Texture cooldownTexture;
	public static Animation PanicCoolDownAnimation;
	public static Animation DisguiseCoolDownAnimation;
	public static Animation HauntCoolDownAnimation;
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
	// public static Animation civTrapDeathAnimation;
	// public static Animation civKnifeDeathAnimation;
	public static Animation civStunAnimation0;
	public static Animation civPanicAnimation0;
	public static TextureRegion civ_panic_rest0;
	public static Animation civShotgunAnimation0;

	public static TextureRegion civ_rest1;
	public static Animation civAnimation1;
	public static Animation civDisarmAnimation1;
	public static Animation civDropDisarmAnimation1;
	public static Animation civBatAnimation1;
	// public static Animation civTrapDeathAnimation;
	// public static Animation civKnifeDeathAnimation;
	public static Animation civStunAnimation1;
	public static Animation civPanicAnimation1;
	public static TextureRegion civ_panic_rest1;
	public static Animation civShotgunAnimation1;

	public static TextureRegion civ_rest2;
	public static Animation civAnimation2;
	public static Animation civDisarmAnimation2;
	public static Animation civDropDisarmAnimation2;
	public static Animation civBatAnimation2;
	// public static Animation civTrapDeathAnimation;
	// public static Animation civKnifeDeathAnimation;
	public static Animation civStunAnimation2;
	public static Animation civPanicAnimation2;
	public static TextureRegion civ_panic_rest2;
	public static Animation civShotgunAnimation2;

	public static TextureRegion civ_rest3;
	public static Animation civAnimation3;
	public static Animation civDisarmAnimation3;
	public static Animation civDropDisarmAnimation3;
	public static Animation civBatAnimation3;
	// public static Animation civTrapDeathAnimation;
	// public static Animation civKnifeDeathAnimation;
	public static Animation civStunAnimation3;
	public static Animation civPanicAnimation3;
	public static TextureRegion civ_panic_rest3;
	public static Animation civShotgunAnimation3;

	public static Texture murderer;
	public static Animation murAnimation;
	public static Animation murKnifeAnimation;
	public static Animation murPlantTrapAnimation;
	public static Animation murDeathAnimation;
	public static Animation murToCivAnimation0;
	public static Animation civToMurAnimation0;
	public static Animation murToCivAnimation1;
	public static Animation civToMurAnimation1;
	public static Animation murToCivAnimation2;
	public static Animation civToMurAnimation2;
	public static Animation murToCivAnimation3;
	public static Animation civToMurAnimation3;
	public static Animation murStunAnimation;
	public static Animation civPlantTrapAnimation0;
	public static Animation civPlantTrapAnimation1;
	public static Animation civPlantTrapAnimation2;
	public static Animation civPlantTrapAnimation3;

	public static Texture ghostHauntT;
	public static Animation ghostHauntAnimation;
	public static Texture ghost_float;
	public static Animation ghostFloatAnimation;

	// OBSTACLES
	public static Texture obstacle;
	public static Texture main_door;

	// LOAD SCREEN / TUTORIAL SCREEN
	public static Texture civLoad;
	public static Texture murLoad;
	public static Texture civTut;
	public static Texture murTut;
	public static Texture tutorialP1;
	public static Texture hudTutorial;
	public static Texture screenTutorial;
	public static Texture mapTutorial;
	public static Texture civButton;
	public static Texture civButtonDown;
	public static Texture murButton;
	public static Texture murButtonDown;
	public static Texture backButton;
	public static Texture nextButton;
	
	public static Texture civCharTut;
	public static Texture murCharTut;
	public static Texture ghostCharTut;
	public static Texture hudOverlay;
	public static Texture itemTutBegin;
	public static Texture abilityTutCiv;
	public static Texture abilityTutMur;
	public static Texture weaponTutCiv;
	public static Texture weaponTutMur;
	public static Texture itemTutCiv;
	public static Texture itemTutMur;
	public static Texture shotgunTut;
	public static Texture shotgunTutMur;
	public static Texture nextButtonToMenu;
	
	public static void loadTutorialScreen() {
		civTut = new Texture(Gdx.files.internal("tutorial/Tutorial-Civilian.png"));
		murTut = new Texture(Gdx.files.internal("tutorial/Tutorial-Murderer.png"));
		tutorialP1 = new Texture(Gdx.files.internal("tutorial/Tutorial-Page-1.png"));
		hudTutorial = new Texture(Gdx.files.internal("tutorial/HUD-Tutorial.png"));
		screenTutorial = new Texture(Gdx.files.internal("tutorial/Screen-Tutorial.png"));
		mapTutorial = new Texture(Gdx.files.internal("tutorial/Map-Tutorial.png"));
		civButton = new Texture(Gdx.files.internal("tutorial/civButton.png"));
		civButtonDown = new Texture(Gdx.files.internal("tutorial/civButtonDown.png"));
		murButton = new Texture(Gdx.files.internal("tutorial/murButton.png"));
		murButtonDown = new Texture(Gdx.files.internal("tutorial/murButtonDown.png"));
		backButton = new Texture(Gdx.files.internal("tutorial/backButton.png"));
		nextButton = new Texture(Gdx.files.internal("tutorial/nextButton.png"));
		
		civCharTut = new Texture(Gdx.files.internal("tutorial/Character-Tutorial-Civ.png"));
		murCharTut = new Texture(Gdx.files.internal("tutorial/Character-Tutorial-Mur.png"));
		ghostCharTut = new Texture(Gdx.files.internal("tutorial/Character-Tutorial-Ghost.png"));
		hudOverlay = new Texture(Gdx.files.internal("tutorial/HUD-Overlay-Tutorial.png"));
		itemTutBegin = new Texture(Gdx.files.internal("tutorial/Item-Tutorial-Begin.png"));
		abilityTutCiv = new Texture(Gdx.files.internal("tutorial/Ability-Tutorial-Civ.png"));
		abilityTutMur = new Texture(Gdx.files.internal("tutorial/Ability-Tutorial-Mur.png"));
		weaponTutCiv = new Texture(Gdx.files.internal("tutorial/Weapon-Tutorial-Civ.png"));
		weaponTutMur = new Texture(Gdx.files.internal("tutorial/Weapon-Tutorial-Mur.png"));
		itemTutCiv = new Texture(Gdx.files.internal("tutorial/Item-Tutorial-Civ.png"));
		itemTutMur = new Texture(Gdx.files.internal("tutorial/Item-Tutorial-Mur.png"));
		shotgunTut = new Texture(Gdx.files.internal("tutorial/Shotgun-Tutorial.png"));
		shotgunTutMur = new Texture(Gdx.files.internal("tutorial/Shotgun-Tutorial-Mur.png"));
		nextButtonToMenu = new Texture(Gdx.files.internal("tutorial/nextButtonToMenu.png"));
	}

	// SOUNDS
	public static Music menuMusic;
	public static Music gameMusic;
	public static Sound walkSound;
	public static Sound runSound;
	public static Sound plantTrapSound;
	public static Sound knifeStabSound;
	public static Sound batSwingSound;
	public static Sound disarmTrapSound;
	public static Sound pickUpItemSound;
	public static Sound shotgunBlastSound;
	public static Sound batHitSound;
	public static Sound trappedSound;
	public static Sound trapDisarmedSound;
	public static Sound knifeThrustSound;
	public static Sound lightningSound;
	public static Sound obstacleSound1;
	public static Sound obstacleSound2;
	public static Sound obstacleSound3;
	public static Music obstacleSoundmd;
	public static Sound hauntSound1;
	public static Sound hauntSound2;
	public static Sound hauntSound3;
	public static Sound characterDeathSound;
	public static Sound clickSound;

	private static ArrayList<Music> musicBox;
	private static ArrayList<Sound> soundBox;
	public static float VOLUME;

	private static Random random;

	public static void initiate() {
		gameWidth = (MurderMansion.V_WIDTH * MurderMansion.SCALE);
		random = new Random();
		VOLUME = 1;
		musicBox = new ArrayList<Music>();
		soundBox = new ArrayList<Sound>();
	}

	public static void loadAll() {
		loadLogo();
		loadFont();
		loadMenuScreen();
		loadMenuSfx();

		loadScoreScreen();
		loadHUD();
		loadCharacters();
		loadMapSprites();
		loadGameSfx();

		loadTutorialScreen();
		loadLoadingScreen();
	}

	public static void loadLogo() {
		logoTexture = new Texture(Gdx.files.internal("basic/logo.png"));
		logoTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		logo = new TextureRegion(logoTexture);

	}

	public static void loadFont() {
		crimesFont36 = new BitmapFont(Gdx.files.internal("Fonts/crimesFont36.fnt"));
		crimesFont48 = new BitmapFont(Gdx.files.internal("Fonts/crimesFont48.fnt"));
		crimesFont36Time = new BitmapFont(Gdx.files.internal("Fonts/crimesFont36.fnt"));
		crimesFont36Time.setScale(0.7f, 0.7f);
		crimesFont36Sync = new BitmapFont(Gdx.files.internal("Fonts/crimesFont36.fnt"));
		crimesFont36Sync.setScale(Gdx.graphics.getWidth() / gameWidth);
		crimesFont36Settings = new BitmapFont(Gdx.files.internal("Fonts/crimesFont36.fnt"));
		crimesFont36Black = new BitmapFont(Gdx.files.internal("Fonts/crimesFont36Black.fnt"));
		basker32Message = new BitmapFont(Gdx.files.internal("Fonts/Basker32.fnt"));
	}

	public static void loadMenuScreen() {
		menuBackground = new Texture(Gdx.files.internal("basic/menu.png"));
		// Create new skin for menu screen
		menuSkin = new Skin();
		// Set menu font
		menuSkin.add("crimesFont36", crimesFont36);
		menuSkin.add("crimesFont48", crimesFont48);
		// Set menu buttons
		menuSkin.add("buttonUp", new Texture("basic/button_up.png"));
		menuSkin.add("buttonDown", new Texture("basic/button_down.png"));
		// Create Text button Style
		normal = new TextButtonStyle();
		normal.font = menuSkin.getFont("crimesFont36");
		normal.font.setScale(0.65f, 0.65f);
		normal.up = menuSkin.getDrawable("buttonUp");
		normal.down = menuSkin.getDrawable("buttonDown");
		normal.pressedOffsetY = -1;
		
		muteButton = new Texture(Gdx.files.internal("menu_screen/muteButton.png"));
		unmuteButton = new Texture(Gdx.files.internal("menu_screen/unmuteButton.png"));

		// MAP
		tiledMap = new TmxMapLoader().load("map/mansion2.tmx");
	}

	public static void loadScoreScreen() {
		// SCORE SCREEN
		
		score_background_texture = new Texture(Gdx.files.internal("score_screen/score_animation.png"));
		score_background_texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] score_background = TextureRegion.split(score_background_texture, 796, 448)[0];
		score_background_animation = new Animation(0.3f, score_background);
		score_background_animation.setPlayMode(Animation.PlayMode.LOOP);
		
		score_texture = new Texture("score_screen/score_background.png");
		
		scoreSkin = new Skin();
		scoreSkin.add("crimesFont36", crimesFont36);
		scoreSkin.add("crimesFont36Black", crimesFont36Black);
		scoreSkin.add("crimesFont48", crimesFont48);
		scoreSkin.add("buttonUp", new Texture("score_screen/next_button_up.png"));
		scoreSkin.add("buttonDown", new Texture("score_screen/next_button_down.png"));
		scoreSkin.add("namebox", new Texture("score_screen/namebox.png"));
		normal1 = new ImageButtonStyle();
		normal1.up = scoreSkin.getDrawable("buttonUp");
		normal1.down = scoreSkin.getDrawable("buttonDown");
		normal1.pressedOffsetY = -1;
		rip = new Texture(Gdx.files.internal("score_screen/rip.png"));
		civ_char0 = new Texture(Gdx.files.internal("score_screen/civ0.png"));
		civ_char1 = new Texture(Gdx.files.internal("score_screen/civ1.png"));
		civ_char2 = new Texture(Gdx.files.internal("score_screen/civ2.png"));
		civ_char3 = new Texture(Gdx.files.internal("score_screen/civ3.png"));
		mur_char = new Texture(Gdx.files.internal("score_screen/mur.png"));
		scoreLabelStyle = new LabelStyle();
		scoreLabelStyle.font = scoreSkin.getFont("crimesFont36Black");
		scoreLabelStyle.font.setScale(0.5f, 0.5f);
		scoreLabelStyle.background = scoreSkin.getDrawable("namebox");
	}

	public static void loadHUD() {
		// HUD COOLDOWN
		cooldownTexture = new Texture(Gdx.files.internal("animation/cooldown_animation.png"));
		cooldownTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[] cooldown = TextureRegion.split(cooldownTexture, 50, 50)[0];
		prohibitedButton = cooldown[0];
		DisguiseCoolDownAnimation = new Animation(1.67f, cooldown);
		DisguiseCoolDownAnimation.setPlayMode(PlayMode.NORMAL);
		HauntCoolDownAnimation = new Animation(3.33f, cooldown);
		HauntCoolDownAnimation.setPlayMode(PlayMode.NORMAL);
		PanicCoolDownAnimation = new Animation(50f, cooldown);
		PanicCoolDownAnimation.setPlayMode(PlayMode.NORMAL);
		WeaponsCoolDownAnimation = new Animation(0.83f, cooldown);
		WeaponsCoolDownAnimation.setPlayMode(PlayMode.NORMAL);

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

		// PAUSE SCREEN
		normalSettings = new TextButtonStyle();
		normalSettings.font = crimesFont36Settings;
		normalSettings.font.setScale(0.4f, 0.6f);
		normalSettings.up = menuSkin.getDrawable("buttonUp");
		normalSettings.down = menuSkin.getDrawable("buttonDown");
		normalSettings.pressedOffsetY = -1;

		settings_button_tex = new Texture(Gdx.files.internal("HUD/settings_button.png"));
		settings_button_draw = new TextureRegionDrawable(new TextureRegion(settings_button_tex));
		settings_cancel_tex = new Texture(Gdx.files.internal("basic/cancel.png"));
		settings_cancel_draw = new TextureRegionDrawable(new TextureRegion(settings_cancel_tex));
		soundoff_tex = new Texture(Gdx.files.internal("HUD/sound_off.png"));
		soundoff_draw = new TextureRegionDrawable(new TextureRegion(soundoff_tex));
		soundon_tex = new Texture(Gdx.files.internal("HUD/sound_on.png"));
		soundon_draw = new TextureRegionDrawable(new TextureRegion(soundon_tex));

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

		// GHOST HUD
		haunt_tex = new Texture(Gdx.files.internal("HUD/haunt.png"));
		haunt_draw = new TextureRegionDrawable(new TextureRegion(haunt_tex));

		// TIMER
		time = new Texture(Gdx.files.internal("HUD/countdown.png"));
		time.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		weapon_parts_counter = new Texture(Gdx.files.internal("HUD/weapon_parts_counter.png"));
		weapon_parts_counter.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// PAUSE SCREEN
		pause_main = new Texture(Gdx.files.internal("basic/pause_background.png"));
		
//		// OVER TIME
//		LabelStyle labelStyle = new LabelStyle();
//		labelStyle.font = menuSkin.getFont("basker45");
//		labelStyle.font.scale(((Gdx.graphics.getWidth() - gameWidth) / gameWidth)/0.2f);
//		message = new Label("The gates are open...", labelStyle);
		
	}

	public static void loadCharacters() {
		// CIVILIAN ANIMATIONS AND TEXTURE
		civilianTexture0 = new Texture(Gdx.files.internal("animation/CIV0.png"));
		civilianTexture0.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[][] civilianTR0 = TextureRegion.split(civilianTexture0, 250, 250);

		civAnimation0 = new Animation(0.9f, Arrays.copyOfRange(civilianTR0[0], 0, 3));
		civAnimation0.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

		civ_rest0 = civilianTR0[0][1];
		civ_dead_lines = new Texture(Gdx.files.internal("gamehelper/dead_lines.png"));

		civPanicAnimation0 = new Animation(0.5f, Arrays.copyOfRange(civilianTR0[1], 0, 3));
		civPanicAnimation0.setPlayMode(PlayMode.LOOP);
		civ_panic_rest0 = civilianTR0[1][0];


		TextureRegion[] civStunParts0 = Arrays.copyOfRange(civilianTR0[2], 0, 3);
		TextureRegion[] civStun0 = new TextureRegion[21];
		for (int i = 0; i < civStun0.length - 2; i+=3) {
			civStun0[i] = civStunParts0[0];
			civStun0[i + 1] = civStunParts0[1];
			civStun0[i + 2] = civStunParts0[2];
		}
		civStunAnimation0 = new Animation(0.208f, civStun0);
		civStunAnimation0.setPlayMode(PlayMode.NORMAL);

		civBatAnimation0 = new Animation(0.1f, Arrays.copyOfRange(civilianTR0[3], 0, 3));
		civBatAnimation0.setPlayMode(PlayMode.NORMAL);

		civShotgunAnimation0 = new Animation(0.2f, Arrays.copyOfRange(civilianTR0[4], 0, 3));
		civShotgunAnimation0.setPlayMode(PlayMode.NORMAL);

		civDisarmAnimation0 = new Animation(0.3f, civilianTR0[5]);
		civDisarmAnimation0.setPlayMode(PlayMode.NORMAL);

		civDropDisarmAnimation0 = new Animation(0.1f, civilianTR0[5]);
		civDropDisarmAnimation0.setPlayMode(PlayMode.NORMAL);

		civilianTexture1 = new Texture(Gdx.files.internal("animation/CIV1.png"));
		civilianTexture1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[][] civilianTR1 = TextureRegion.split(civilianTexture1, 250, 250);

		civAnimation1 = new Animation(0.9f, Arrays.copyOfRange(civilianTR1[0], 0, 3));
		civAnimation1.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

		civ_rest1 = civilianTR1[0][1];

		civPanicAnimation1 = new Animation(0.5f, Arrays.copyOfRange(civilianTR1[1], 0, 3));
		civPanicAnimation1.setPlayMode(PlayMode.LOOP);
		civ_panic_rest1 = civilianTR1[1][0];


		TextureRegion[] civStunParts1 = Arrays.copyOfRange(civilianTR1[2], 0, 3);
		TextureRegion[] civStun1 = new TextureRegion[21];
		for (int i = 0; i < civStun1.length - 2; i+=3) {
			civStun1[i] = civStunParts1[0];
			civStun1[i + 1] = civStunParts1[1];
			civStun1[i + 2] = civStunParts1[2];
		}
		civStunAnimation1 = new Animation(0.208f, civStun1);
		civStunAnimation1.setPlayMode(PlayMode.NORMAL);

		civBatAnimation1 = new Animation(0.1f, Arrays.copyOfRange(civilianTR1[3], 0, 3));
		civBatAnimation1.setPlayMode(PlayMode.NORMAL);

		civShotgunAnimation1 = new Animation(0.2f, Arrays.copyOfRange(civilianTR1[4], 0, 3));
		civShotgunAnimation1.setPlayMode(PlayMode.NORMAL);

		civDisarmAnimation1 = new Animation(0.3f, civilianTR1[5]);
		civDisarmAnimation1.setPlayMode(PlayMode.NORMAL);

		civDropDisarmAnimation1 = new Animation(0.1f, civilianTR1[5]);
		civDropDisarmAnimation1.setPlayMode(PlayMode.NORMAL);

		civilianTexture2 = new Texture(Gdx.files.internal("animation/CIV2.png"));
		civilianTexture2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[][] civilianTR2 = TextureRegion.split(civilianTexture2, 250, 250);

		civAnimation2 = new Animation(0.9f, Arrays.copyOfRange(civilianTR2[0], 0, 3));
		civAnimation2.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

		civ_rest2 = civilianTR2[0][1];

		civPanicAnimation2 = new Animation(0.5f, Arrays.copyOfRange(civilianTR2[1], 0, 3));
		civPanicAnimation2.setPlayMode(PlayMode.LOOP);
		civ_panic_rest2 = civilianTR2[1][0];


		TextureRegion[] civStunParts2 = Arrays.copyOfRange(civilianTR2[2], 0, 3);
		TextureRegion[] civStun2 = new TextureRegion[21];
		for (int i = 0; i < civStun2.length - 2; i+=3) {
			civStun2[i] = civStunParts2[0];
			civStun2[i + 1] = civStunParts2[1];
			civStun2[i + 2] = civStunParts2[2];
		}
		civStunAnimation2 = new Animation(0.208f, civStun2);
		civStunAnimation2.setPlayMode(PlayMode.NORMAL);

		civBatAnimation2 = new Animation(0.1f, Arrays.copyOfRange(civilianTR2[3], 0, 3));
		civBatAnimation2.setPlayMode(PlayMode.NORMAL);

		civShotgunAnimation2 = new Animation(0.2f, Arrays.copyOfRange(civilianTR2[4], 0, 3));
		civShotgunAnimation2.setPlayMode(PlayMode.NORMAL);

		civDisarmAnimation2 = new Animation(0.3f, civilianTR2[5]);
		civDisarmAnimation2.setPlayMode(PlayMode.NORMAL);

		civDropDisarmAnimation2 = new Animation(0.1f, civilianTR2[5]);
		civDropDisarmAnimation2.setPlayMode(PlayMode.NORMAL);

		civilianTexture3 = new Texture(Gdx.files.internal("animation/CIV3.png"));
		civilianTexture3.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[][] civilianTR3 = TextureRegion.split(civilianTexture3, 250, 250);

		civAnimation3 = new Animation(0.9f, Arrays.copyOfRange(civilianTR3[0], 0, 3));
		civAnimation3.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

		civ_rest3 = civilianTR3[0][1];

		civPanicAnimation3 = new Animation(0.5f, Arrays.copyOfRange(civilianTR3[1], 0, 3));
		civPanicAnimation3.setPlayMode(PlayMode.LOOP);
		civ_panic_rest3 = civilianTR3[1][0];


		TextureRegion[] civStunParts3 = Arrays.copyOfRange(civilianTR3[2], 0, 3);
		TextureRegion[] civStun3 = new TextureRegion[21];
		for (int i = 0; i < civStun3.length - 2; i+=3) {
			civStun3[i] = civStunParts3[0];
			civStun3[i + 1] = civStunParts3[1];
			civStun3[i + 2] = civStunParts3[2];
		}
		civStunAnimation3 = new Animation(0.208f, civStun3);
		civStunAnimation3.setPlayMode(PlayMode.NORMAL);

		civBatAnimation3 = new Animation(0.1f, Arrays.copyOfRange(civilianTR3[3], 0, 3));
		civBatAnimation3.setPlayMode(PlayMode.NORMAL);

		civShotgunAnimation3 = new Animation(0.2f, Arrays.copyOfRange(civilianTR3[4], 0, 3));
		civShotgunAnimation3.setPlayMode(PlayMode.NORMAL);

		civDisarmAnimation3 = new Animation(0.3f, civilianTR3[5]);
		civDisarmAnimation3.setPlayMode(PlayMode.NORMAL);

		civDropDisarmAnimation3 = new Animation(0.1f, civilianTR3[5]);
		civDropDisarmAnimation3.setPlayMode(PlayMode.NORMAL);

		murderer = new Texture(Gdx.files.internal("animation/MUR_CIV0.png"));
		murderer.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		TextureRegion[][] murTR = TextureRegion.split(murderer, 250, 250);
		for (int i = 0; i < murTR.length; i++) {
			for (int j = 0; j < murTR[0].length; j++) {
				murTR[i][j].setRegion(murTR[i][j].getRegionX() - 25, murTR[i][j].getRegionY() - 25, 300, 300);
			}
		}
		
		
		murAnimation = new Animation(0.9f, Arrays.copyOfRange(murTR[0], 0, 3));
		murAnimation.setPlayMode(PlayMode.LOOP_PINGPONG);
		mur_rest = murTR[0][1];

		TextureRegion[] murStun = new TextureRegion[15];
		TextureRegion[] murStunParts = Arrays.copyOfRange(murTR[1], 0, 3);
		for (int i = 0; i < murStun.length - 2; i += 3) {
			murStun[i] = murStunParts[0];
			murStun[i + 1] = murStunParts[1];
			murStun[i + 2] = murStunParts[2];
		}
		murStunAnimation = new Animation(0.208f, murStun);
		murStunAnimation.setPlayMode(PlayMode.NORMAL);

		murKnifeAnimation = new Animation(0.1f, Arrays.copyOfRange(murTR[2], 0, 4));
		murKnifeAnimation.setPlayMode(PlayMode.NORMAL);
		
		murPlantTrapAnimation = new Animation(0.25f, murTR[4]);
		murPlantTrapAnimation.setPlayMode(PlayMode.NORMAL);
		
		civPlantTrapAnimation0 = new Animation(0.25f, civilianTR0[6]);
		civPlantTrapAnimation0.setPlayMode(PlayMode.NORMAL);
		civPlantTrapAnimation1 = new Animation(0.25f, civilianTR1[6]);
		civPlantTrapAnimation1.setPlayMode(PlayMode.NORMAL);
		civPlantTrapAnimation2 = new Animation(0.25f, civilianTR2[6]);
		civPlantTrapAnimation2.setPlayMode(PlayMode.NORMAL);
		civPlantTrapAnimation3 = new Animation(0.25f, civilianTR3[6]);
		civPlantTrapAnimation3.setPlayMode(PlayMode.NORMAL);

		TextureRegion[] civmur0 = new TextureRegion[6];
		TextureRegion[] civmur1 = new TextureRegion[6];
		TextureRegion[] civmur2 = new TextureRegion[6];
		TextureRegion[] civmur3 = new TextureRegion[6];

		for (int i = 0; i < civmur0.length - 1; i += 2) {
			civmur0[i] = mur_rest;
			civmur0[i + 1] = civ_rest0;
		}
		for (int i = 0; i < civmur1.length - 1; i += 2) {
			civmur1[i] = mur_rest;
			civmur1[i + 1] = civ_rest1;
		}
		for (int i = 0; i < civmur2.length - 1; i += 2) {
			civmur2[i] = mur_rest;
			civmur2[i + 1] = civ_rest2;
		}
		for (int i = 0; i < civmur3.length - 1; i += 2) {
			civmur3[i] = mur_rest;
			civmur3[i + 1] = civ_rest3;
		}

		civToMurAnimation0 = new Animation(0.2f, civmur0);
		civToMurAnimation0.setPlayMode(PlayMode.NORMAL);
		civToMurAnimation1 = new Animation(0.2f, civmur1);
		civToMurAnimation1.setPlayMode(PlayMode.NORMAL);
		civToMurAnimation2 = new Animation(0.2f, civmur2);
		civToMurAnimation2.setPlayMode(PlayMode.NORMAL);
		civToMurAnimation3 = new Animation(0.2f, civmur3);
		civToMurAnimation3.setPlayMode(PlayMode.NORMAL);

		TextureRegion[] murciv0 = new TextureRegion[6];
		TextureRegion[] murciv1 = new TextureRegion[6];
		TextureRegion[] murciv2 = new TextureRegion[6];
		TextureRegion[] murciv3 = new TextureRegion[6];

		for (int i = 0; i < murciv0.length - 1; i += 2) {
			murciv0[i] = civ_rest0;
			murciv0[i + 1] = mur_rest;
		}
		for (int i = 0; i < murciv1.length - 1; i += 2) {
			murciv1[i] = civ_rest1;
			murciv1[i + 1] = mur_rest;
		}
		for (int i = 0; i < murciv2.length - 1; i += 2) {
			murciv2[i] = civ_rest2;
			murciv2[i + 1] = mur_rest;
		}
		for (int i = 0; i < murciv3.length - 1; i += 2) {
			murciv3[i] = civ_rest3;
			murciv3[i + 1] = mur_rest;
		}

		murToCivAnimation0 = new Animation(0.2f, murciv0);
		murToCivAnimation0.setPlayMode(PlayMode.NORMAL);
		murToCivAnimation1 = new Animation(0.2f, murciv1);
		murToCivAnimation1.setPlayMode(PlayMode.NORMAL);
		murToCivAnimation2 = new Animation(0.2f, murciv2);
		murToCivAnimation2.setPlayMode(PlayMode.NORMAL);
		murToCivAnimation3 = new Animation(0.2f, murciv3);
		murToCivAnimation3.setPlayMode(PlayMode.NORMAL);

		// ghost
		ghost_float = new Texture(Gdx.files.internal("animation/ghostSingleFrame.png"));
		ghostHauntT = new Texture(Gdx.files.internal("animation/ghostHauntAnimation.png"));
		TextureRegion ghostHauntTR[] = TextureRegion.split(ghostHauntT, 100, 100)[0];
		ghostHauntAnimation = new Animation(0.2f, ghostHauntTR);
		ghostHauntAnimation.setPlayMode(PlayMode.NORMAL);

	}

	public static void loadMapSprites() {

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

		// OBSTACLES
		obstacle = new Texture(Gdx.files.internal("map/barrels.png"));
		main_door = new Texture(Gdx.files.internal("map/main-door.png"));
	}

	public static void loadMenuSfx() {
		musicBox.add(menuMusic = Gdx.audio.newMusic(Gdx.files.internal("bgm/MenuScreen Music.mp3")));
		menuMusic.setLooping(true);
		soundBox.add(clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.mp3")));
		clickSound.setLooping(0, false);

		soundBox.add(walkSound = Gdx.audio.newSound(Gdx.files.internal("sfx/walking.mp3")));

		soundBox.add(runSound = Gdx.audio.newSound(Gdx.files.internal("sfx/running.mp3")));
	}

	public static void loadGameSfx() {
		musicBox.add(gameMusic = Gdx.audio.newMusic(Gdx.files.internal("bgm/GameScreen Music.mp3")));
		gameMusic.setLooping(true);

		soundBox.add(plantTrapSound = Gdx.audio.newSound(Gdx.files.internal("sfx/plantTrap.mp3")));
		soundBox.add(knifeStabSound = Gdx.audio.newSound(Gdx.files.internal("sfx/Knife Stab.mp3")));

		soundBox.add(batSwingSound = Gdx.audio.newSound(Gdx.files.internal("sfx/bat swing.mp3")));
		soundBox.add(disarmTrapSound = Gdx.audio.newSound(Gdx.files.internal("sfx/disarm trap.mp3")));
		soundBox.add(pickUpItemSound = Gdx.audio.newSound(Gdx.files.internal("sfx/pick up disarm trap.mp3")));

		soundBox.add(shotgunBlastSound = Gdx.audio.newSound(Gdx.files.internal("sfx/shotgun blast.mp3")));
		soundBox.add(knifeThrustSound = Gdx.audio.newSound(Gdx.files.internal("sfx/knifeMiss.mp3")));
		soundBox.add(trapDisarmedSound = Gdx.audio.newSound(Gdx.files.internal("sfx/trap disarmed.mp3")));
		soundBox.add(trappedSound = Gdx.audio.newSound(Gdx.files.internal("sfx/trapped sound.mp3")));
		soundBox.add(batHitSound = Gdx.audio.newSound(Gdx.files.internal("sfx/bat hit.mp3")));

		soundBox.add(characterDeathSound = Gdx.audio.newSound(Gdx.files.internal("sfx/character death.mp3")));
		soundBox.add(lightningSound = Gdx.audio.newSound(Gdx.files.internal("sfx/lightning.mp3")));

		soundBox.add(obstacleSound1 = Gdx.audio.newSound(Gdx.files.internal("sfx/obstacle1.mp3")));
		soundBox.add(obstacleSound2 = Gdx.audio.newSound(Gdx.files.internal("sfx/obstacle2.mp3")));
		soundBox.add(obstacleSound3 = Gdx.audio.newSound(Gdx.files.internal("sfx/obstacle3.wav")));
		musicBox.add(obstacleSoundmd = Gdx.audio.newMusic(Gdx.files.internal("sfx/obstacle main door.mp3")));

		soundBox.add(hauntSound1 = Gdx.audio.newSound(Gdx.files.internal("sfx/haunt1.mp3")));
		soundBox.add(hauntSound2 = Gdx.audio.newSound(Gdx.files.internal("sfx/haunt2.mp3")));
		soundBox.add(hauntSound3 = Gdx.audio.newSound(Gdx.files.internal("sfx/haunt3.mp3")));
	}

	public static void loadLoadingScreen() {
		civLoad = new Texture(Gdx.files.internal("tutorial/Civilian-load-screen.png"));
		murLoad = new Texture(Gdx.files.internal("tutorial/Murderer-load-screen.png"));
	}


	public static void obstacleSFX() {
		int randomInt = random.nextInt(3);
		if (randomInt == 0) {
			obstacleSound1.play(AssetLoader.VOLUME);
		} else if (randomInt == 1) {
			obstacleSound2.play(AssetLoader.VOLUME);
		} else if (randomInt == 2) {
			obstacleSound3.play(AssetLoader.VOLUME);
		}
	}

	public static void hauntSFX() {
		int randomInt = random.nextInt(3);
		if (randomInt == 0) {
			hauntSound1.play(AssetLoader.VOLUME);
		} else if (randomInt == 1) {
			hauntSound2.play(AssetLoader.VOLUME);
		} else if (randomInt == 2) {
			hauntSound3.play(AssetLoader.VOLUME);
		}
	}

	public static void muteSFX() {
		for (Sound s : soundBox) {
			s.setVolume(0, 0f);
		}
		for (Music m : musicBox) {
			m.setVolume(0f);
		}
		VOLUME = 0f;
	}

	public static void unmuteSFX() {
		for (Sound s : soundBox) {
			s.setVolume(0, 1f);
		}
		for (Music m : musicBox) {
			m.setVolume(1f);
		}
		VOLUME = 1f;
	}

	public static void dispose() {
		// We must dispose of the texture when we are finished.
		try {
			muteButton.dispose();
			unmuteButton.dispose();
			menuSkin.dispose();
			logoTexture.dispose();
			touchpadSkin.dispose();
			menuBackground.dispose();
			score_texture.dispose();
			score_background_texture.dispose();
			pause_main.dispose();
			cooldownTexture.dispose();
			time.dispose();
			weapon_parts_counter.dispose();
			tiledMap.dispose();
			emptySlot.dispose();
			settings_button_tex.dispose();
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
			ghost_float.dispose();
			plantedTrapTexture.dispose();
			restingTrapTexture.dispose();
			disarmTrapSpriteTexture.dispose();
			batSpriteTexture.dispose();
			knifeSpriteTexture.dispose();
			shotgunPartTexture.dispose();
			haunt_tex.dispose();
			ghostHauntT.dispose();
			walkSound.dispose();
			runSound.dispose();
			civLoad.dispose();
			murLoad.dispose();
			civTut.dispose();
			murTut.dispose();
			tutorialP1.dispose();
			hudOverlay.dispose();
			hudTutorial.dispose();
			screenTutorial.dispose();
			mapTutorial.dispose();
			civButton.dispose();
			civButtonDown.dispose();
			murButton.dispose();
			murButtonDown.dispose();
			civ_char0.dispose();
			civ_char1.dispose();
			civ_char2.dispose();
			civ_char3.dispose();
			mur_char.dispose();
			civCharTut.dispose();
			murCharTut.dispose();
			itemTutBegin.dispose();
			abilityTutCiv.dispose();
			abilityTutMur.dispose();
			weaponTutCiv.dispose();
			weaponTutMur.dispose();
			itemTutCiv.dispose();
			itemTutMur.dispose();
			shotgunTut.dispose();
			shotgunTutMur.dispose();
			nextButtonToMenu.dispose();
			ghostCharTut.dispose();

			// Dispose Sound
			menuMusic.dispose();
			clickSound.dispose();
			walkSound.dispose();
			runSound.dispose();
		} catch (NullPointerException e) {

		}
	}

	public static void disposeSFX() {
		try {
			plantTrapSound.dispose();
			knifeStabSound.dispose();
			batSwingSound.dispose();
			disarmTrapSound.dispose();
			pickUpItemSound.dispose();
			gameMusic.dispose();
			shotgunBlastSound.dispose();
			knifeThrustSound.dispose();
			trapDisarmedSound.dispose();
			trappedSound.dispose();
			batHitSound.dispose();
			lightningSound.dispose();
			obstacleSound1.dispose();
			obstacleSound2.dispose();
			obstacleSound3.dispose();
			obstacleSoundmd.dispose();
			hauntSound1.dispose();
			hauntSound2.dispose();
			hauntSound3.dispose();
			characterDeathSound.dispose();
		} catch (NullPointerException e) {

		}
	}
}
