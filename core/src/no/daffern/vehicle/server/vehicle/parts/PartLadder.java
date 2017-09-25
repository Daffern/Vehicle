package no.daffern.vehicle.server.vehicle.parts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.server.vehicle.Wall;

/**
 * Created by Daffern on 15.06.2017.
 */
public class PartLadder extends Part {

	Fixture fixture;

	public PartLadder(int itemId) {
		super(itemId, GameItemTypes.PART_TYPE_LADDER, false, 1, 1);
	}

	@Override
	public void attach(World world, Body vehicleBody, Wall wall) {

		float x = wall.getLocalX();
		float y = wall.getLocalY();

		PolygonShape shape = new PolygonShape();
		shape.set(new float[]{
				x, y,
				x + 1f, y,
				x + 1f, y + 1f,
				x, y + 1f});

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.isSensor = true;

		fixture = vehicleBody.createFixture(fixtureDef);

		attached = true;
	}

	@Override
	public void detach(World world, Body vehicleBody, Wall wall) {

		vehicleBody.destroyFixture(fixture);
		fixture = null;

		attached = false;
	}

	@Override
	public boolean checkCollision(Part otherPart) {
		return true;
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
