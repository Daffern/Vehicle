package no.daffern.vehicle.server.world;

import com.badlogic.gdx.physics.box2d.Fixture;

public interface WorldGeneratorI {
	void begin(float x, float y);
	void update();
	void sendWorld(int clientId);
	void clipFixture(Fixture fixture, float[] clip);
	void tryClipFixture(float[] clip);
	String getDebugString();
}
