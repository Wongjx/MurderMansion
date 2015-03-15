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
import com.jkjk.MurderMansion.murdermansion;

public class MenuScreen implements Screen{
    private float gameWidth;
    private float gameHeight;
    private float TITLE_PAD;
    private float BUTTON_WIDTH;
    private float BUTTON_HEIGHT;
    private float BUTTON_PAD;

    private TextButtonStyle normal = AssetLoader.normal;
    private LabelStyle titleStyle = AssetLoader.title;
    private Label title = new Label("Murder Mansion",titleStyle);

    private Stage stage = new Stage();
    private Table table = new Table();
    private TextButton buttonPlay = new TextButton("Enter", normal),
    		buttonLogin = new TextButton("Connect", normal),
    		buttonLogout = new TextButton("Logout",normal),
    		buttonQuick = new TextButton("Quick game", normal),
    		buttonInvite = new TextButton("Invite",normal),
    		buttonJoin = new TextButton("Join game",normal);
    
    murdermansion game;

    public MenuScreen(murdermansion game,float gameWidth, float gameHeight){
    	this.gameWidth = gameWidth;
    	this.gameHeight = gameHeight;
    	this.game=game;
    	BUTTON_HEIGHT=Gdx.graphics.getHeight()*.1f;
    	BUTTON_WIDTH=Gdx.graphics.getWidth()*.25f;
    	BUTTON_PAD=Gdx.graphics.getHeight()*.02f;
    	TITLE_PAD=Gdx.graphics.getHeight()*.04f;
    }
    
    @Override
    public void show() {
        //The elements are displayed in the order you add them.
        //The first appear on top, the last at the bottom.
    	
        buttonPlay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(game,gameWidth, gameHeight));
            }
        });
        buttonLogin.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	game.actionResolver.loginGPGS();

            }
        });
        buttonQuick.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Host multiplayer game
            	game.actionResolver.startQuickGame();
            	game.mMultiplayerSeisson.mState=game.mMultiplayerSeisson.ROOM_WAIT;
            	((Game)Gdx.app.getApplicationListener()).setScreen(new WaitScreen(game,gameWidth, gameHeight));
            }
        });
        buttonLogout.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	game.actionResolver.logoutGPGS();
            }
        });
        buttonInvite.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	game.actionResolver.sendInvitations();
            	game.mMultiplayerSeisson.mState=game.mMultiplayerSeisson.ROOM_WAIT;
            	((Game)Gdx.app.getApplicationListener()).setScreen(new WaitScreen(game,gameWidth, gameHeight));
            }
        });
        buttonJoin.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	game.actionResolver.seeInvitations();
            	game.mMultiplayerSeisson.mState=game.mMultiplayerSeisson.ROOM_WAIT;
            	((Game)Gdx.app.getApplicationListener()).setScreen(new WaitScreen(game,gameWidth, gameHeight));
            }
        });
        
        table.add(title).padBottom(TITLE_PAD).row();
        table.add(buttonPlay).size(this.BUTTON_WIDTH,this.BUTTON_HEIGHT).padBottom(this.BUTTON_PAD).row();
        table.add(buttonLogin).size(this.BUTTON_WIDTH,this.BUTTON_HEIGHT).padBottom(this.BUTTON_PAD).row();
        table.add(buttonLogout).size(this.BUTTON_WIDTH,this.BUTTON_HEIGHT).padBottom(this.BUTTON_PAD).row();
        table.add(buttonQuick).size(this.BUTTON_WIDTH,this.BUTTON_HEIGHT).padBottom(this.BUTTON_PAD).row();
        table.add(buttonInvite).size(this.BUTTON_WIDTH,this.BUTTON_HEIGHT).padBottom(this.BUTTON_PAD).row();
        table.add(buttonJoin).size(this.BUTTON_WIDTH,this.BUTTON_HEIGHT).padBottom(this.BUTTON_PAD).row();

        System.out.println("height: " + BUTTON_HEIGHT);
        System.out.println("width: " + BUTTON_WIDTH);


        table.setFillParent(true);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		
	}

}
