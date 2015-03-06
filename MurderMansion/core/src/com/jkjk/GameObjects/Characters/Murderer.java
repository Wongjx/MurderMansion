package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.jkjk.GameObjects.ItemSlot;
import com.jkjk.GameObjects.WeaponSlot;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;


public class Murderer extends GameCharacter implements ItemSlot, WeaponSlot {

	private boolean disguised;
	private Weapon weapon;
	private Item item;
	
	private Body body;
	private FixtureDef fdef;
	
	public Murderer(Body body) {
		this.body = body;
		// create player
		Vector2[] vertices = {new Vector2(0,0), new Vector2(-20,-10), new Vector2(-20,10)};
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("murderer");
	}
	
	public boolean isDisguised(){
		return disguised;
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

}
