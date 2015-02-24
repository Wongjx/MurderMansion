package com.jkjk.GameObjects.Characters;

import com.jkjk.GameObjects.ItemSlot;
import com.jkjk.GameObjects.WeaponSlot;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;


public class Murderer extends GameCharacter implements ItemSlot, WeaponSlot {

	private boolean disguised;
	private Weapon weapon;
	private Item item;
	
	public Murderer() {
		// TODO Auto-generated constructor stub
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
