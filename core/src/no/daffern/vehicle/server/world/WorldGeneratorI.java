package no.daffern.vehicle.server.world;

public interface WorldGeneratorI {
	void begin(float x, float y);
	void update();
	void sendWorld(int clientId);
}
