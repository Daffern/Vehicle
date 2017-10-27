package no.daffern.vehicle.server.world.destructible;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.seisw.util.geom.Poly;

public class DestructibleChunk {
	public Body body;
	public Fixture fixture;
	public Poly poly;
}
