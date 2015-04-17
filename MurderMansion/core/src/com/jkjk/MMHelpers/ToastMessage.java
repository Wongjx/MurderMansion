package com.jkjk.MMHelpers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.GameObjects.Duration;

public class ToastMessage {
	
	private boolean show;
	private String message;
	private Duration ttl;
	
	public ToastMessage(){
		show = false;
		ttl = new Duration(5000);
	}
	
	public void setDisplayMessage(String s){
		ttl.startCountdown();
		this.message = s;
		show=true;
	}
	
	public void render(SpriteBatch batch){
		if(show==true){
			AssetLoader.basker32blackMessage.draw(batch, message, 220, 300);
			ttl.update();
		}
		if(!ttl.isCountingDown()){
			show = false;
		}
		
	}
	
}
