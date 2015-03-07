package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.GameWorld.GameWorld;


public class Civilian extends GameCharacter {
	
	private GameWorld gWorld;
	private Body body;
	private FixtureDef fdef;
	
	private int colour;
	
	Civilian(int colour, Body body){
		this.colour = colour;
		fdef = new FixtureDef();
		this.body = body;
		// create player
		Vector2[] vertices = {new Vector2(0,0), new Vector2(-20,-10), new Vector2(-20,10)};
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("civilian");

	}
	
	public int getColour(){
		return colour;
	}
	
	public Body getBody(){
		return body;
	}
}
