package no.daffern.vehicle.server.vehicle.parts.network;

import com.badlogic.gdx.physics.box2d.World;
import no.daffern.vehicle.server.vehicle.Wall;
import no.daffern.vehicle.server.vehicle.parts.Part;
import no.daffern.vehicle.utils.Tools;

import java.util.*;

public class NetworkManager {


	private int nextNetworkId = 0;

	private Map<Integer, NetworkHandler> networks = new HashMap<>();

	private World world;

	public NetworkManager(World world) {
		this.world = world;
	}

	/**
	 * @param part part to be included in a network
	 * @param wall the wall the part is attached to
	 */
	public void tryAddPart(Part part, Wall wall) {

		if (part instanceof PartEdge)
			addPartEdge((PartEdge) part, wall);

		else if (part instanceof PartNode)
			addPartNode((PartNode)part, wall);

	}
	/**
	 * The part partEdge needs to be removed from the wall beforehand
	 *
	 * @param part
	 * @param wall
	 */
	public void tryRemovePart(Part part, Wall wall) {

		if (part instanceof PartEdge)
			removePartEdge((PartEdge)part, wall);

		else if (part instanceof PartNode)
			removePartNode((PartNode)part, wall);
	}

	private void addPartNode(PartNode partNode, Wall wall){

		//check if wall has a Edge
		for(int i = 0 ; i < wall.getNumParts() ; i++){
			Part part = wall.getPart(i);

			if (part instanceof PartEdge){
				PartEdge partEdge = (PartEdge)part;

				//add Node the the network
				NetworkHandler networkHandler = networks.get(partEdge.networkId);

				if (networkHandler != null){
					networkHandler.getNodes().add(partNode);
					networkHandler.onCreate(world);
				}
			}
		}
	}

	private void removePartNode(PartNode partNode, Wall wall){
		//check if wall has a Edge
		for(int i = 0 ; i < wall.getNumParts() ; i++){
			Part part = wall.getPart(i);

			if (part instanceof PartEdge){
				PartEdge partEdge = (PartEdge)part;

				//add Node the the network
				NetworkHandler networkHandler = networks.get(partEdge.networkId);

				if (networkHandler != null){
					networkHandler.getNodes().remove(partNode);
					networkHandler.onCreate(world);
				}
			}
		}
	}

	private void addPartEdge(PartEdge partEdge, Wall wall){
		updateEdgeRelations(partEdge, wall, false);

		Queue<PartEdge> openEdges = new ArrayDeque<>();
		List<PartNode> foundNodes = new ArrayList<>();

		openEdges.offer(partEdge);

		int networkId = getNextNetworkId();

		while (openEdges.size() > 0) {

			PartEdge currentEdge = openEdges.poll();

			//if edge has not been included yet
			if (networkId != currentEdge.networkId) {

				//disconnect previous networks
				disconnectNetwork(currentEdge.networkId);

				currentEdge.networkId = networkId;

				//add all edges
				for (PartEdge pe : currentEdge.getNeighbours()) {
					openEdges.offer(pe);
				}

				//add all nodes
				for (PartNode pn : currentEdge.getNodes()) {
					foundNodes.add(pn);
				}
			}


		}

		connectNetwork(networkId, partEdge, foundNodes);

	}



	private void removePartEdge(PartEdge partEdge, Wall wall){

		disconnectNetwork(partEdge.networkId);

		List<PartEdge> neighbours = partEdge.getNeighbours();

		//update the relations of all neighbours
		updateEdgeRelations(partEdge, wall, true);

		//remove neighbours and nodes
		partEdge.clearData();

		int networkId = 0;

		for (int i = 0; i < neighbours.size(); i++) {

			//neighbour to the removed node
			PartEdge neighbour = neighbours.get(i);

			//if neighbour already has been included in a network (inside this iteration), continue
			if (neighbour.networkId == networkId)
				continue;

			networkId = getNextNetworkId();

			Queue<PartEdge> openEdges = new ArrayDeque<>();
			List<PartNode> foundNodes = new ArrayList<>();

			openEdges.offer(neighbour);


			while (openEdges.size() > 0) {

				PartEdge currentEdge = openEdges.poll();

				if (currentEdge.networkId != networkId) {

					//disconnect any previous network
					disconnectNetwork(currentEdge.networkId);

					currentEdge.networkId = networkId;

					//add all edges
					for (PartEdge pe : currentEdge.getNeighbours()) {
						openEdges.offer(pe);
					}

					//add all nodes
					for (PartNode pn : currentEdge.getNodes()) {
						foundNodes.add(pn);
					}
				}

			}


			connectNetwork(networkId, partEdge, foundNodes);
		}
	}

	/**
	 * @param networkId unique
	 * @param partEdge
	 * @param nodes     nodes to be connected
	 */
	private void connectNetwork(int networkId, PartEdge partEdge, List<PartNode> nodes) {
		NetworkHandler network = networks.get(networkId);

		if (network == null) {
			network = partEdge.newNetworkHandler();
			networks.put(networkId, network);
		}

		network.initOnCreate(nodes, world);

		Tools.log(this, "Created network with id: " + networkId);
		//printNodes(partEdge, nodes);
	}

	private void disconnectNetwork(int networkId) {
		NetworkHandler network = networks.get(networkId);

		if (network != null) {
			network.onDestroy(world);
			networks.remove(networkId);

			Tools.log(this, "destroyed network with Id: " + networkId);
		}
	}


	private void updateEdgeRelations(PartEdge partEdge, Wall wall, boolean remove) {

		int itemId = partEdge.getItemId();

		PartEdge left = null, right = null, up = null, down = null;

		if (wall.left != null) {
			left = (PartEdge) wall.left.findPartById(itemId);
		}
		if (wall.right != null) {
			right = (PartEdge) wall.right.findPartById(itemId);
		}
		if (wall.up != null) {
			up = (PartEdge) wall.up.findPartById(itemId);
		}
		if (wall.down != null) {
			down = (PartEdge) wall.down.findPartById(itemId);
		}

		if (!remove){
			partEdge.left = left;
			partEdge.right = right;
			partEdge.down = down;
			partEdge.up = up;
			if (left != null)
				left.right = partEdge;
			if (right != null)
				right.left = partEdge;
			if (down != null)
				down.up = partEdge;
			if (up != null)
				up.down = partEdge;

		}else{
			partEdge.left = null;
			partEdge.right = null;
			partEdge.down = null;
			partEdge.up = null;
			if (left != null)
				left.right = null;
			if (right != null)
				right.left = null;
			if (down != null)
				down.up = null;
			if (up != null)
				up.down = null;
		}


		List<PartNode> nodes = new Vector<>(1, 1);

		for (int i = 0; i < wall.getNumParts(); i++) {
			Part part = wall.getPart(i);
			if (part instanceof PartNode) {
				nodes.add((PartNode) part);
			}
		}

		partEdge.setNodes(nodes.toArray(new PartNode[nodes.size()]));

	}


	private int getNextNetworkId() {
		return nextNetworkId++;
	}


	public void printNodes(PartEdge partEdge, List<PartNode> partNodes){
		Tools.log(this, "Network id: " + partEdge.networkId + " has: " + partNodes.size() + " nodes");

	}
}
