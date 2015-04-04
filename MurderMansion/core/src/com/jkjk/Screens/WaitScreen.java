package com.jkjk.Screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.MMClient;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MurderMansion.MurderMansion;

public class WaitScreen implements Screen {

	private Stage stage = new Stage();
	private SpriteBatch batcher;
	private Sprite sprite;
	private MurderMansion game;
	
	private float gameWidth;
	private float gameHeight;

	public WaitScreen(MurderMansion game, float gameWidth, float gameHeight) {
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
	}

	@Override
	public void show() {
		sprite = new Sprite(AssetLoader.logo);
		sprite.setColor(1, 1, 1, 1);

		float desiredWidth = gameWidth * .3f;
		float scale = desiredWidth / sprite.getWidth();

		sprite.setSize(sprite.getWidth() * scale, sprite.getHeight() * scale);
		sprite.setPosition((gameWidth / 2) - (sprite.getWidth() / 2), (gameHeight / 2)
				- (sprite.getHeight() / 2));
		Image logo = new Image(new SpriteDrawable(sprite));
		logo.setPosition((Gdx.graphics.getWidth()/2-sprite.getWidth()/2), (Gdx.graphics.getHeight()/2-sprite.getHeight()/2));
		stage.addActor(logo);
	}

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        if ((game.mMultiplayerSeisson.mState==game.mMultiplayerSeisson.ROOM_PLAY) && 
        		(game.mMultiplayerSeisson.serverAddress!=null) &&
        		(game.mMultiplayerSeisson.serverPort!=0)){
        	
            //Create MMClient and connect to server
    		GameWorld gWorld = GameWorld.getInstance();
    		GameRenderer renderer= GameRenderer.getInstance(gWorld, gameWidth, gameHeight);
        	
            try {
				game.mMultiplayerSeisson.setClient(MMClient.getInstance(gWorld, renderer, game.mMultiplayerSeisson.serverAddress,game.mMultiplayerSeisson.serverPort));
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        	((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(game,gameWidth, gameHeight,gWorld,renderer));
        	
        } else if (game.mMultiplayerSeisson.mState==game.mMultiplayerSeisson.ROOM_MENU){
        	game.mMultiplayerSeisson.mState=game.mMultiplayerSeisson.ROOM_NULL;
        	((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game,gameWidth, gameHeight));
        	
        }
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stage.dispose();
	}

}
