package com.jkjk.MurderMansion;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.jkjk.MMHelpers.ActionResolver;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MMHelpers.MultiplayerSeissonInfo;
import com.jkjk.MMHelpers.SocketHelper;
import com.jkjk.Screens.MenuScreen;

public class MurderMansion extends Game {
	public ActionResolver actionResolver;
	public MultiplayerSeissonInfo mMultiplayerSeisson;
	public SocketHelper socketHelper;

	public static final String TITLE = "Murder Mansion";
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 180;
	public static final int SCALE = 2;
	

	public MurderMansion(ActionResolver actionResolver, MultiplayerSeissonInfo mMultiplayerSeisson,SocketHelper sock){
		this.actionResolver=actionResolver;
		this.mMultiplayerSeisson=mMultiplayerSeisson; 
		this.socketHelper= sock;
	}
	

	@Override
	public void create() {

		
		GdxNativesLoader.load();
		AssetLoader.load();
//		setScreen(new SplashScreen(this, V_WIDTH * SCALE, V_HEIGHT * SCALE));
//		setScreen(new WaitScreen(this, V_WIDTH * SCALE, V_HEIGHT * SCALE));
		setScreen(new MenuScreen(this,V_WIDTH * SCALE, V_HEIGHT * SCALE));
//		setScreen(new GameScreen(V_WIDTH * SCALE, V_HEIGHT * SCALE));
	}

	@Override
	public void dispose() {
		super.dispose();
		AssetLoader.dispose();
	}
}