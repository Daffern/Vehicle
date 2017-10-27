package no.daffern.vehicle.server.vehicle.parts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.server.handlers.TickHandler;
import no.daffern.vehicle.server.vehicle.Wall;
import no.daffern.vehicle.server.vehicle.Walls;
import no.daffern.vehicle.server.vehicle.parts.network.PartNode;
import no.daffern.vehicle.server.world.CollisionCategories;
import no.daffern.vehicle.server.world.UserData;

public class PartDrill extends PartNode {

	Fixture fixture;
	TickHandler.TickListener tickListener;

	public PartDrill(int itemId) {
		super(itemId, GameItemTypes.PART_TYPE_DRILL, true, 1.5f, 1.5f);
	}

	@Override
	public void attach(World world, Body vehicleBody, Wall wall) {
		Vector2 localPos = new Vector2(wall.getLocalX() + Wall.WALL_WIDTH / 2, wall.getLocalY() + Wall.WALL_HEIGHT / 2);
		Vector2 pos = vehicleBody.getWorldPoint(localPos);


		float[] collisionVertices = new float[]{
				localPos.x-1,localPos.y-0.5f,
				localPos.x+1,localPos.y-0.5f,
				localPos.x,localPos.y-2
		};

		float[] drillVertices = new float[]{
				localPos.x-2, localPos.y,
				localPos.x+2, localPos.y,
				localPos.x+2,localPos.y-2,
				localPos.x-2,localPos.y-2};


		PolygonShape shape = new PolygonShape();
		shape.set(drillVertices);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 1;
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = CollisionCategories.vehicleOutside;
		fixtureDef.filter.maskBits = CollisionCategories.terrain;

		fixture = vehicleBody.createFixture(fixtureDef);

		UserData.UserDataDrill userData = new UserData.UserDataDrill(drillVertices);

		fixture.setUserData(userData);

		tickListener = new TickHandler.TickListener() {
			@Override
			protected void onTick() {
				((UserData.UserDataDrill)fixture.getUserData()).recentChunks.clear();//clear to enable the same chunks to be clipped again
			}
		};
		S.tickHandler.addTickListener(tickListener,(byte)30);
	}

	@Override
	public void detach(World world, Body vehicleBody, Wall wall) {
		vehicleBody.destroyFixture(fixture);
		S.tickHandler.removeTickListener(tickListener);
	}


	@Override
	public boolean canPlace(Walls walls, IntVector2 i){

		if (walls.get(i.x-1,i.y-1) != null)
			return false;
		if (walls.get(i.x,i.y-1) != null)
			return false;
		return walls.get(i.x + 1, i.y - 1) == null;
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
