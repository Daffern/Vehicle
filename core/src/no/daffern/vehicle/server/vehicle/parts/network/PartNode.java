package no.daffern.vehicle.server.vehicle.parts.network;


import no.daffern.vehicle.server.vehicle.parts.Part;

public abstract class PartNode extends Part {


	public PartNode(int itemId, int type, boolean dynamic, float width, float height) {
		super(itemId, type, dynamic, width, height);
	}

}
