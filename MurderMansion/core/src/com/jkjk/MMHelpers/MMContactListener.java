package com.jkjk.MMHelpers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.jkjk.GameWorld.GameWorld;

/**
 * @author JunXiang Will handle contact between weapons and characters
 */
public class MMContactListener implements ContactListener {

	private Fixture fa;
	private Fixture fb;
	private Array<Body> itemsToRemove;
	private Array<Body> weaponsToRemove;
	private GameWorld gWorld;

	public MMContactListener(GameWorld gWorld) {
		itemsToRemove = new Array<Body>();
		weaponsToRemove = new Array<Body>();
		this.gWorld = gWorld;
	}

	// called when two fixtures start to collide
	public void beginContact(Contact c) {
		fa = c.getFixtureA();
		fb = c.getFixtureB();

		System.out.println("Begin contact: fa: " + fa.getUserData() + ", fb: " + fb.getUserData());
		if (fa.getUserData() != null && fb.getUserData() != null) {
			if (fa.getUserData().equals("civilian") && fb.getUserData().equals("item")
					&& gWorld.getPlayer().getItem() == null) {
				itemsToRemove.add(fb.getBody());
			} else if (fa.getUserData().equals("murderer") && fb.getUserData().equals("item")
					&& gWorld.getPlayer().getItem() == null) {
				itemsToRemove.add(fb.getBody());
			} else if (fa.getUserData().equals("civilian") && fb.getUserData().equals("weapon")
					&& gWorld.getPlayer().getWeapon() == null) {
				weaponsToRemove.add(fb.getBody());
			} else if (fa.getUserData().equals("murderer") && fb.getUserData().equals("weapon")
					&& gWorld.getPlayer().getWeapon() == null) {
				weaponsToRemove.add(fb.getBody());
			} 
			
			
			if (fb.getUserData().equals("lightBody")&&!fa.getUserData().equals("lightBody")){//not into contact with another light body
				System.out.println("draw sprite");
			}
		}
		

	}

	// called when two fixtures no longer collide
	public void endContact(Contact c) {
	}

	public Array<Body> getItemsToRemove() {
		return itemsToRemove;
	}

	public Array<Body> getWeaponsToRemove() {
		return weaponsToRemove;
	}

	// collision detection - when two objects collide
	// presolve
	// collision handling - what happens when it happens
	// postsolve
	public void preSolve(Contact c, Manifold m) {
	}

	public void postSolve(Contact c, ContactImpulse ci) {
	}

}
