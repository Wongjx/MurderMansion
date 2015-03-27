package com.jkjk.GameObjects.Characters;

import box2dLight.ConeLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.jkjk.MMHelpers.AssetLoader;

public class Civilian extends GameCharacter {

	private ConeLight coneLight;
	private Touchpad touchpad;
	private Animation civAnimation;
	private TextureRegion civ_rest;
	private Animation currentAnimation;
	private double hypothenuse;

	Civilian(int id, World world) {

		super("Civilian", id, world);
		
		touchpad = AssetLoader.touchpad;

		// create body of civilian
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		// Circular body fixture
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("civilian");

		// Create Light for player
		coneLight = new ConeLight(rayHandler, 100, null, 200, 0, 0, 0, 40);
		coneLight.attachToBody(body, 0, 0);
		ConeLight.setContactFilter((short) 2, (short) 2, (short) 1);

		// cone-ish Torch light fixture
		FixtureDef coneFdef = new FixtureDef();
		Vector2[] ConeLightVert = { new Vector2(0, 0), new Vector2(113, 99), new Vector2(122, 87),
				new Vector2(146, 34), new Vector2(150, 0), new Vector2(146, -34), new Vector2(122, -87),
				new Vector2(113, -99) };
		PolygonShape coneShape = new PolygonShape();
		coneFdef.isSensor = true;
		coneShape.set(ConeLightVert);
		coneFdef.shape = coneShape;
		coneFdef.filter.maskBits = 1;// cannot bump into other light bodies.
		body.createFixture(coneFdef).setUserData("lightBody");

		civAnimation = AssetLoader.civAnimation;
		civ_rest = AssetLoader.civ_rest;
		body.setUserData(civAnimation);
		batch = new SpriteBatch();
	}

	@Override
	public void render(OrthographicCamera cam) {
		
		runTime += Gdx.graphics.getRawDeltaTime();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		System.out.println(body.getUserData());
		currentAnimation = (Animation) body.getUserData();
		if(currentAnimation==AssetLoader.civBatAnimation||
				currentAnimation==AssetLoader.civDisarmAnimation||
				currentAnimation==AssetLoader.civKnifeDeathAnimation||
				currentAnimation==AssetLoader.civTrapDeathAnimation){
			if(currentAnimation.isAnimationFinished(runTime)){
				currentAnimation = AssetLoader.civAnimation;
			}
			else{
				body.setLinearVelocity(0, 0);
				body.setAngularVelocity(0);
			}
		}
		else{	
			if (touchpad.isTouched()){
				//hypothenuse = Math.sqrt((Math.pow(touchpad.getKnobPercentX(), 2)+Math.pow(touchpad.getKnobPercentY(),2)));
				//currentAnimation.setFrameDuration((float)hypothenuse*5);
				batch.draw(currentAnimation.getKeyFrame(runTime,true), body.getPosition().x-10, body.getPosition().y-10, 10, 10, 20, 20, 1, 1,(float) (body.getAngle()*180/Math.PI)-90);
			}
			else{
				batch.draw(civ_rest, body.getPosition().x-10, body.getPosition().y-10, 10, 10, 20, 20, 1, 1,(float) (body.getAngle()*180/Math.PI)-90);
			}
		}

		batch.end();
		super.render(cam);
		
	}
}
