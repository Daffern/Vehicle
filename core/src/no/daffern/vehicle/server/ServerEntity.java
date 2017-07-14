package no.daffern.vehicle.server;

/**
 * Created by Daffern on 20.06.2017.
 */
public class ServerEntity {
	private static int nextEntityId = 0;

	private int entityId;
	public ServerEntity(){
		entityId = nextEntityId++;
	}
}
