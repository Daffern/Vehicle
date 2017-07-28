package no.daffern.vehicle.server.vehicle;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.GearJoint;
import com.badlogic.gdx.physics.box2d.joints.GearJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import no.daffern.vehicle.common.GameItemTypes;

import java.util.List;
import java.util.Vector;

/**
 * TODO lets do this again some time
 *
 * Created by Daffern on 04.07.2017.
 */
public class PartAxle extends Part {


	private AxleNetwork axleNetwork;

	public PartAxle(int itemId) {
		super(itemId, GameItemTypes.PART_TYPE_AXLE, false, 1f, 1f);

	}

	@Override
	public void attach(World world, Body vehicleBody, Wall wall) {

		buildAxleNetwork(world,wall);

	}
	@Override
	public void detach(World world, Body vehicleBody, Wall wall) {

		//destroy previous network
		if (axleNetwork != null)
			axleNetwork.destroyJoints(world);

		//create networks for nearby walls
		List<Wall> nearbyWalls = wall.getNearbyWalls();

		AxleNetwork newAxleNetwork = new AxleNetwork();

		//loop through nearby walls and create the axlenetwork
		for (Wall nearbyWall : nearbyWalls){
			PartAxle partAxle = (PartAxle)nearbyWall.findPart(GameItemTypes.PART_TYPE_AXLE);

			if (partAxle != null){

				partAxle.buildAxleNetwork(nearbyWall, newAxleNetwork);

				//create new network if previous network didnt connect
				if (partAxle.getAxleNetwork() != newAxleNetwork){
					newAxleNetwork = new AxleNetwork();
				}

			}


		}

	}


	public void buildAxleNetwork(World world, Wall wall){

		if (this.axleNetwork != null)
			this.axleNetwork.destroyJoints(world);

		AxleNetwork axleNetwork = new AxleNetwork();
		buildAxleNetwork(wall, axleNetwork);

		axleNetwork.createJoints(world);

	}


	/**
	 * Flood fill find parts of itemTypes connected through PartAxles
	 * @param wall
	 * @param axleNetwork
	 * @return true if already filled
	 */
	private boolean buildAxleNetwork(Wall wall, AxleNetwork axleNetwork) {

		if (this.axleNetwork == axleNetwork){
			return true;
		}else{

			this.axleNetwork = axleNetwork;


		}

		Part partEngine = wall.findPart(GameItemTypes.PART_TYPE_ENGINE);
		if (partEngine != null) {
			axleNetwork.engines.add((PartEngine) partEngine);

		}
		Part partWheel = wall.findPart(GameItemTypes.PART_TYPE_WHEEL);
		if (partWheel != null)
			axleNetwork.wheels.add((PartWheel)partWheel);


		iterateWall(wall.left, axleNetwork);
		iterateWall(wall.right, axleNetwork);
		iterateWall(wall.down, axleNetwork);
		iterateWall(wall.up, axleNetwork);

		return false;
	}
	//calls buildAxleNetwork on a wall (if it has a PartAxle)
	private void iterateWall(Wall wall, AxleNetwork axleNetwork){
		if (wall == null)
			return;

		PartAxle part = (PartAxle)wall.findPart(GameItemTypes.PART_TYPE_AXLE);
		if (part == null)
			return;

		part.buildAxleNetwork(wall, axleNetwork);
	}


	public AxleNetwork getAxleNetwork(){
		return axleNetwork;
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

	private class AxleNetwork{
		private List<PartWheel> wheels = new Vector<>(5,5);
		private List<PartEngine> engines = new Vector<>(5,5);

		private List<GearJoint> joints = new Vector<>();


		public void createJoints(World world){

			//create joint between first engine and wheel
			if (wheels.size() > 0 && engines.size() > 0){

				PartWheel wheel = wheels.get(0);
				PartEngine engine = engines.get(0);

				createJoint(wheel.getJoint(), engine.getJoint(), wheel.getBody(),engine.getBody(), world);

			}
			//create joints between all engines
			for (int i = 0 ; i < engines.size()-1 ; i++){
				PartEngine engine1 = engines.get(i);
				PartEngine engine2 = engines.get(i+1);

				createJoint(engine1.getJoint(),engine2.getJoint(), engine1.getBody(), engine2.getBody(), world);
			}
			//create joints between all wheels
			for (int i = 0 ;i < wheels.size()-1 ; i++){
				PartWheel wheel1 = wheels.get(i);
				PartWheel wheel2 = wheels.get(i+1);

				createJoint(wheel1.getJoint(), wheel2.getJoint(),wheel1.getBody(), wheel2.getBody(), world);
			}


		}
		private void createJoint(RevoluteJoint joint1, RevoluteJoint joint2, Body bodyA, Body bodyB, World world){
			GearJointDef gearJointDef = new GearJointDef();
			gearJointDef.joint1 = joint1;
			gearJointDef.joint2 = joint2;
			gearJointDef.bodyA = bodyA;
			gearJointDef.bodyB = bodyB;
			gearJointDef.ratio = -1f;


			GearJoint gearJoint = (GearJoint)world.createJoint(gearJointDef);
			joints.add(gearJoint);
		}

		public void destroyJoints(World world){
			for (GearJoint gearJoint : joints){
				world.destroyJoint(gearJoint);
			}
			joints.clear();
		}
	}

}
