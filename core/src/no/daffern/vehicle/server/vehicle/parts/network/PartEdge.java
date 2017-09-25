package no.daffern.vehicle.server.vehicle.parts.network;


import no.daffern.vehicle.server.vehicle.parts.Part;

import java.util.List;
import java.util.Vector;


public abstract class PartEdge extends Part {

	PartEdge left, right, up, down;
	private PartNode[] nodes;

	public int networkId = -1;

	public PartEdge(int itemId, int type, boolean dynamic, float width, float height) {
		super(itemId, type, dynamic, width, height);
	}



	public List<PartEdge> getNeighbours(){
		Vector<PartEdge> neighbours = new Vector<>(2,1);
		if (left != null)
			neighbours.add(left);
		if (right != null)
			neighbours.add(right);
		if (down != null)
			neighbours.add(down);
		if (up != null)
			neighbours.add(up);
		return neighbours;
	}
	public PartNode[] getNodes(){
		return nodes;
	}
	public void setNodes(PartNode[] nodes){
		this.nodes = nodes;
	}

	public void clearData(){
		left = null;
		right = null;
		up = null;
		down = null;
		nodes = null;
	}

	//convenience method for creating a network handler
	public abstract NetworkHandler newNetworkHandler();
}
