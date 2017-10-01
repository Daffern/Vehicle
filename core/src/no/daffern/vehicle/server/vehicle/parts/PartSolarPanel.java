package no.daffern.vehicle.server.vehicle.parts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.server.vehicle.Wall;
import no.daffern.vehicle.server.vehicle.parts.network.PartNode;

public class PartSolarPanel extends PartNode{

	private int powerGen = 100;

	public PartSolarPanel(int itemId) {
		super(itemId, GameItemTypes.PART_TYPE_SOLAR, false, 1f, 1f);
	}
	public int getPowerSupply(){
		return powerGen;
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
