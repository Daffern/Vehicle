package no.daffern.vehicle.server.vehicle.parts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import no.daffern.vehicle.common.GameItemStates;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.server.vehicle.Wall;
import no.daffern.vehicle.server.vehicle.parts.network.PartNode;

/**
 * Created by Daffern on 04.07.2017.
 */
public class PartEngine extends PartNode {

	private boolean running = false;

	private float maxSpeed = 6;
	private float maxTorque = 40;
	private int powerDemand = 50;

	private Body engineBody;
	private RevoluteJoint engineJoint;//is the "engine"


	public PartEngine(int itemId) {
		super(itemId, GameItemTypes.PART_TYPE_ENGINE, false, 0.8f, 0.8f);
	}

	public RevoluteJoint getJoint() {
		return engineJoint;
	}

	public Body getBody() {
		return engineBody;
	}

	//returns power not used
	public int supplyPower(int power) {
		if (!running)
			return power;

		if (power >= powerDemand) {
			calcMotorSpeed(powerDemand);
			return power - powerDemand;
		}
		else {
			calcMotorSpeed(power);
			return 0;
		}
	}

	public void resetMotor() {
		if (engineJoint != null)
			engineJoint.setMaxMotorTorque(0);
	}

	private void calcMotorSpeed(int power) {
		engineJoint.setMaxMotorTorque((float) power / (float) powerDemand * maxTorque);
		engineJoint.setMotorSpeed((float) power / (float) powerDemand * maxSpeed);
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
		revoluteJointDef.localAnchorA.set(0, 0);
		revoluteJointDef.localAnchorB.set(localPos);

		revoluteJointDef.enableMotor = true;
		revoluteJointDef.motorSpeed = 0;
		revoluteJointDef.maxMotorTorque = 0;


		engineJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

	}

	@Override
	public void detach(World world, Body vehicleBody, Wall wall) {
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
		return 0;
		//return engineBody.getAngle();
	}

	@Override
	public Vector2 getPosition() {
		return null;
	}

	@Override
	public boolean interact1() {
		running = !running;
		resetMotor();
		return true;
	}

	public byte getState() {
		if (running)
			return GameItemStates.ON;
		else return GameItemStates.OFF;
	}

}
