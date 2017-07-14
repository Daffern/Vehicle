package no.daffern.vehicle.server.handlers;

/**
 * Created by Daffern on 24.06.2017.
 */
public class Entity {
	private static int nextEntityId;

	protected int type;
	protected int entityId;

	protected Entity(int type){
		this.type = type;
		this.entityId = nextEntityId++;
	}

	public int getType() {
		return type;
	}

	public int getEntityId() {
		return entityId;
	}
}
