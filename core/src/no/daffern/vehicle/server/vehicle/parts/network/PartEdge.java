package no.daffern.vehicle.server.vehicle.parts.network;


import no.daffern.vehicle.server.vehicle.parts.Part;

import java.util.ArrayList;
import java.util.List;


public abstract class PartEdge extends Part {

	PartEdge left, right, up, down;
	private List<PartNode> nodes;//TODO optimize with array

	public int networkId = -1;

	public PartEdge(int itemId, int type, boolean dynamic, float width, float height) {
		super(itemId, type, dynamic, width, height);
	}



	public List<PartEdge> getNeighbours(){
		List<PartEdge> neighbours = new ArrayList<>(2);
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
	public List<PartNode> getNodes(){
		return nodes;
	}
	public void setNodes(List<PartNode> nodes){
		this.nodes = nodes;
	}
	public void addNode(PartNode partNode){
		nodes.add(partNode);
	}
	public void removeNode(PartNode partNode){
		nodes.remove(partNode);
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
