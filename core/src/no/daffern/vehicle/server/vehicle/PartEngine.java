package no.daffern.vehicle.server.vehicle;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.GearJoint;
import com.badlogic.gdx.physics.box2d.joints.GearJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.server.world.CollisionCategories;

import java.util.List;
import java.util.Vector;

/**
 * Created by Daffern on 04.07.2017.
 */
public class PartEngine extends Part {

	private boolean running = false;

	private float maxSpeed = 3f;
	private float maxTorque = 20f;


	private Body engineBody;
	private RevoluteJoint engineJoint;//is the "engine"


	public PartEngine(int itemId) {
		super(itemId, GameItemTypes.PART_TYPE_ENGINE, true, 0.8f, 0.8f);
	}

	public RevoluteJoint getJoint(){
		return engineJoint;
	}
	public Body getBody(){
		return engineBody;
	}

	@Override
	public void attach(World world, Body vehicleBody, Wall wall) {

		Vector2 localPos = new Vector2(wall.getLocalX() + Wall.WALL_WIDTH / 2, wall.getLocalY() + Wall.WALL_HEIGHT / 2);
		Vector2 position = vehicleBody.getWorldPoint(localPos);

		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.fixedRotation = false;

		engineBody = world.createBody(bodyDef);

		MassData massData = new MassData();
		massData.mass = 0.3f;
		massData.I = 0.3f;
		engineBody.setMassData(massData);

		RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.bodyA = engineBody;
		revoluteJointDef.bodyB = vehicleBody;
		revoluteJointDef.localAnchorA.set(0,0);
		revoluteJointDef.localAnchorB.set(localPos);

		revoluteJointDef.enableMotor = true;
		revoluteJointDef.motorSpeed = maxSpeed;
		revoluteJointDef.maxMotorTorque = maxTorque;


		engineJoint = (RevoluteJoint)world.createJoint(revoluteJointDef);



		//update axlenetwork
		PartAxle partAxle = (PartAxle)wall.findPart(GameItemTypes.PART_TYPE_AXLE);
		if (partAxle != null) partAxle.buildAxleNetwork(world,wall);
	}

	@Override
	public void detach(World world, Body vehicleBody) {
		world.destroyJoint(engineJoint);
		world.destroyBody(engineBody);
		engineJoint = null;
		engineBody = null;
	}

	@Override
	public boolean checkCollision(Part otherPart) {
		return otherPart.getType() == this.getType();
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public float getAngle() {
		return engineBody.getAngle();
	}

	@Override
	public Vector2 getPosition() {
		return null;
	}

	@Override
	public boolean interact1() {
		running = true;
		return false;
	}

	@Override
	public boolean interact2() {
		running = false;
		return false;
	}



}
