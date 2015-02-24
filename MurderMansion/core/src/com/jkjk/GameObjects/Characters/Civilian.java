package com.jkjk.GameObjects.Characters;

import com.jkjk.GameObjects.ItemSlot;
import com.jkjk.GameObjects.WeaponSlot;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;


public class Civilian extends GameCharacter implements ItemSlot, WeaponSlot {
	
	private int colour;
	
	Civilian(int colour){
		this.colour = colour;
	}

	@Override
	public void addWeapon(Weapon weapon) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeWeapon(Weapon weapon) {
		// TODO Auto-generated method stub
		
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
		
	}

	@Override
	public void removeItem(Item item) {
		// TODO Auto-generated method stub
		
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
