package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MurderMansion.MurderMansion;

public class MenuScreen implements Screen{
    private float screenWidth;
    private float screenHeight;
    private float TITLE_PAD;
    private float BUTTON_WIDTH;
    private float BUTTON_HEIGHT;
    private float BUTTON_PAD;

    private TextButtonStyle normal = AssetLoader.normal;
    private LabelStyle titleStyle = AssetLoader.title;
    private Label title = new Label("Murder Mansion",titleStyle);

    private Stage stage = new Stage();
    private Table table = new Table();
    private TextButton buttonPlay = new TextButton("Enter", normal),buttonExit = new TextButton("Connect", normal),buttonMulti = new TextButton("Multiplayer", normal);
    
    MurderMansion game;

    public MenuScreen(MurderMansion game,float screenWidth, float screenHeight){
    	this.screenWidth = screenWidth;
    	this.screenHeight = screenHeight;
    	this.game=game;
    	this.BUTTON_HEIGHT=screenHeight*.3f;
    	this.BUTTON_WIDTH=screenWidth*.3f;
    	this.BUTTON_PAD=screenHeight*.02f;
    	this.TITLE_PAD=screenHeight*.04f;
    }
    
    @Override
    public void show() {
        //The elements are displayed in the order you add them.
        //The first appear on top, the last at the bottom.
    	
        buttonPlay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(screenWidth, screenHeight));
            }
        });
        buttonExit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	//To do : Switch method to connect to google play account
            	game.actionResolver.loginGPGS();
//                Gdx.app.exit();
                // or System.exit(0);
            }
        });
        buttonMulti.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Host multiplaer game
            }
        });
        
        table.add(title).padBottom(this.TITLE_PAD).row();
        table.add(buttonPlay).size(this.BUTTON_WIDTH,this.BUTTON_HEIGHT).padBottom(this.BUTTON_PAD).row();
        table.add(buttonExit).size(this.BUTTON_WIDTH,this.BUTTON_HEIGHT).padBottom(this.BUTTON_PAD).row();
        table.add(buttonMulti).size(this.BUTTON_WIDTH,this.BUTTON_HEIGHT).padBottom(this.BUTTON_PAD).row();

        table.setFillParent(true);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
		
	}

	@Override
	public void resize(int width, int height) {
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
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stage.dispose();
		
	}

}
