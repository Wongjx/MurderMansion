package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jkjk.GameObjects.ItemSlot;
import com.jkjk.GameObjects.WeaponSlot;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.GameWorld.GameWorld;


public class Civilian extends GameCharacter implements ItemSlot, WeaponSlot {
	
	private GameWorld gWorld;
	private Body body;
	private FixtureDef fdef;
	
	private int colour;
	private Weapon weapon;
	private Item item;
	
	Civilian(int colour, Body body){
		this.colour = colour;
		fdef = new FixtureDef();
		this.body = body;
		// create player
		Vector2[] vertices = {new Vector2(0,0), new Vector2(-20,-10), new Vector2(-20,10)};
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		fdef.friction = 0.9f;
		body.createFixture(fdef).setUserData("civilian");

	}

	@Override
	public void addWeapon(Weapon weapon) {
		// TODO Auto-generated method stub
		this.weapon = weapon;
	}

	@Override
	public void removeWeapon() {
		// TODO Auto-generated method stub
		this.weapon = null;
	}

	@Override
	public void cooldownWeapon() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void useWeapon() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addItem(Item item) {
		// TODO Auto-generated method stub
		this.item = item;
	}

	@Override
	public void removeItem() {
		// TODO Auto-generated method stub
		this.item = null;
	}

	@Override
	public void cooldownItem() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void useItem() {
		// TODO Auto-generated method stub
		
	}
	
	public int getColour(){
		return colour;
	}
	
	public Body getBody(){
		return body;
	}
}
