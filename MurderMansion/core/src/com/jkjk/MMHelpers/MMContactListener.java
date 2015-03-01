package com.jkjk.MMHelpers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * @author JunXiang
 *	Will handle contact between weapons and characters
 */
public class MMContactListener implements ContactListener {
	
	private boolean batContact;
	
	// called when two fixtures start to collide
	public void beginContact(Contact c){
		System.out.println("Begin contact");
		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();
		
		System.out.println(fa.getUserData() + ", " + fb.getUserData());
		System.out.println(fa.getUserData().equals("ground"));
		System.out.println(fb.getUserData().equals("foot"));
		if (fa.getUserData().equals("ground") && fb.getUserData().equals("foot"))
			batContact = true;
		
	}
	
	// called when two fixtures no longer collide
	public void endContact(Contact c){
		System.out.println("End contact");
	}
	
	public boolean isBatContact(){
		return batContact;
	}
	
	// collision detection - when two objects collide
	// presolve
	// collision handling - what happens when it happens
	// postsolve
	public void preSolve(Contact c, Manifold m){	}
	public void postSolve(Contact c, ContactImpulse ci){	}

}
