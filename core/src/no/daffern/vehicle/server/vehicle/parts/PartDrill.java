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
import no.daffern.vehicle.utils.Box2dUtils;

import java.util.ArrayList;
import java.util.List;

public class PartDrill extends PartNode {

	private Body vehicleBody;
	private Fixture fixture;
	private Filter filter;

	private TickHandler.TickListener tickListener;

	private int drillInterval = 60; //once per 60 ticks
	private static int drillCheckInterval = 2; //check collisions every 2 ticks

	float[] drillVertices;

	public PartDrill(int itemId) {
		super(itemId, GameItemTypes.PART_TYPE_DRILL, true, 1.5f, 1.5f);
	}

	@Override
	public void attach(World world, Body vb, Wall wall) {
		this.vehicleBody = vb;


		Vector2 localPos = new Vector2(wall.getLocalX() + Wall.WALL_WIDTH / 2, wall.getLocalY() + Wall.WALL_HEIGHT / 2);
		Vector2 pos = vehicleBody.getWorldPoint(localPos);

		/*
		float[] drillVertices = new float[]{
				localPos.x-2, localPos.y,
				localPos.x+2, localPos.y,
				localPos.x+2,localPos.y-2,
				localPos.x-2,localPos.y-2};*/

		drillVertices = Box2dUtils.approxCircle(localPos.x, localPos.y, 2f,6);
		drillVertices = Box2dUtils.approxCircle(localPos.x, localPos.y, 2f,6);

		filter = new Filter();
		filter.categoryBits = CollisionCategories.vehicleOutside;
		filter.maskBits = CollisionCategories.terrain;

		PolygonShape shape = new PolygonShape();
		shape.set(Box2dUtils.approxCircle(localPos.x, localPos.y, 1.5f,6));

		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 1;
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = filter.categoryBits;
		fixtureDef.filter.maskBits = filter.maskBits;

		fixture = vehicleBody.createFixture(fixtureDef);

		shape.dispose();

		UserData.UserDataDrill userData = new UserData.UserDataDrill();
		fixture.setUserData(userData);


		tickListener = new TickHandler.TickListener() {
			int checks = 0;
			@Override
			protected void onTick() {
				//make sure the drill is in contact for a certain amount of time before clipping
				UserData.UserDataDrill ud = (UserData.UserDataDrill)fixture.getUserData();
				List<Fixture> fixtures = ud.fixtures;
				if (fixtures.size() > 0){
					checks++;
				}else{
					checks = 0;
				}
				if (checks >= drillInterval/drillCheckInterval){

					float[] clip = Box2dUtils.transformVertices(vehicleBody.getTransform(), drillVertices);

					List<Fixture> uniqueFixtures = new ArrayList<>(fixtures.size());
					for (int i = 0 ; i < fixtures.size() ; i++){
						if (uniqueFixtures.contains(fixtures.get(i)))
							continue;
						uniqueFixtures.add(fixtures.get(i));
					}

					for (Fixture fixture : uniqueFixtures){
						S.worldHandler.getWorldGenerator().clipFixture(fixture, clip);
					}

					checks = 0;
				}
			}
		};
		S.tickHandler.addTickListener(tickListener,(byte)drillCheckInterval);
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
