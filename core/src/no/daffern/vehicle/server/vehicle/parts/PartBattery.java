package no.daffern.vehicle.server.vehicle.parts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.server.vehicle.Wall;
import no.daffern.vehicle.server.vehicle.parts.network.PartNode;

public class PartBattery extends PartNode{

	public PartBattery(int itemId) {
		super(itemId, GameItemTypes.PART_TYPE_BATTERY, true, 0.9f, 0.9f);
	}

	int capacity = 10000;
	int level = 0;
	int maxFlow = 200;

	public int getCapacity() {
		return capacity;
	}

	public int getMaxOutput(){
		if (level == 0)
			return 0;
		else
			return maxFlow;
	}

	public void setFlow(int flow){

	}

	@Override
	public void attach(World world, Body vehicleBody, Wall wall) {

	}

	@Override
	public void detach(World world, Body vehicleBody, Wall wall) {

	}

	@Override
	public boolean checkCollision(Part otherPart) {
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
	public boolean isActive() {
		return false;
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
