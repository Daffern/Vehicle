package no.daffern.vehicle.server.vehicle.parts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.GearJoint;
import com.badlogic.gdx.physics.box2d.joints.GearJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.server.vehicle.Wall;
import no.daffern.vehicle.server.vehicle.parts.network.NetworkHandler;
import no.daffern.vehicle.server.vehicle.parts.network.PartEdge;
import no.daffern.vehicle.server.vehicle.parts.network.PartNode;

import java.util.Vector;

/**
 * TODO lets do this again some time
 * <p>
 * Created by Daffern on 04.07.2017.
 */
public class PartAxle extends PartEdge {


	@Override
	public NetworkHandler newNetworkHandler() {
		return new AxleNetworkHandler();
	}

	private class AxleNetworkHandler extends NetworkHandler {

		Vector<GearJoint> joints = new Vector<>();

		@Override
		public void onCreate(World world) {

			onDestroy(world);

			//temporary lists of all joints and bodies
			Vector<RevoluteJoint> nodeJoints = new Vector<>();
			Vector<Body> nodeBodies = new Vector<>();

			for (PartNode part : getNodes()) {
				switch (part.getType()) {
					case GameItemTypes.PART_TYPE_ENGINE:

						PartEngine pe = (PartEngine) part;

						if (nodeJoints.size() > 0) {

							GearJointDef gearJointDef = new GearJointDef();
							gearJointDef.joint1 = nodeJoints.lastElement();
							gearJointDef.joint2 = pe.getJoint();
							gearJointDef.bodyA = nodeBodies.lastElement();
							gearJointDef.bodyB = pe.getBody();
							gearJointDef.ratio = -1f;

							GearJoint gearJoint = (GearJoint) world.createJoint(gearJointDef);
							joints.add(gearJoint);
						}

						nodeBodies.add(pe.getBody());
						nodeJoints.add(pe.getJoint());

						break;
					case GameItemTypes.PART_TYPE_WHEEL:

						PartWheel pw = (PartWheel) part;

						if (nodeJoints.size() > 0) {

							GearJointDef gearJointDef = new GearJointDef();
							gearJointDef.joint1 = nodeJoints.lastElement();
							gearJointDef.joint2 = pw.getJoint();
							gearJointDef.bodyA = nodeBodies.lastElement();
							gearJointDef.bodyB = pw.getBody();
							gearJointDef.ratio = -1f;

							GearJoint gearJoint = (GearJoint) world.createJoint(gearJointDef);
							joints.add(gearJoint);
						}

						nodeBodies.add(pw.getBody());
						nodeJoints.add(pw.getJoint());

						break;
				}
			}

		}


		@Override
		public void onDestroy(World world) {
			if (joints != null) {
				for (GearJoint gearJoint : joints) {
					world.destroyJoint(gearJoint);
				}
				joints.clear();
			}
		}


	}


	public PartAxle(int itemId) {
		super(itemId, GameItemTypes.PART_TYPE_AXLE, false, 1f, 1f);

	}

	@Override
	public void attach(World world, Body vehicleBody, Wall wall) {

	}

	@Override
	public void detach(World world, Body vehicleBody, Wall wall) {

	}

	//called if this part is in the range of another part, return true if the other part should be placed
	@Override
	public boolean checkCollision(Part otherPart) {
		return otherPart.getType() == getType();
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public float getAngle() {
		return 0;
	}

	@Override
	public Vector2 getPosition() {
		return null;
	}

	@Override
	public boolean interact1() {
		return false;
	}

	@Override
	public boolean interact2() {
		return false;
	}


}
