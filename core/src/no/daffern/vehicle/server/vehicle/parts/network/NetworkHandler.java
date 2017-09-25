package no.daffern.vehicle.server.vehicle.parts.network;

import com.badlogic.gdx.physics.box2d.World;

import java.util.List;

public abstract class NetworkHandler {

	private List<PartNode> nodes;
	protected List<PartNode> getNodes(){
		return nodes;
	}
	void initOnCreate(List<PartNode> nodes, World world){
		this.nodes = nodes;
		onCreate(world);
	}
	protected abstract void onCreate(World world);
	protected abstract void onDestroy(World world);
}
