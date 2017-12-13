package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.client.player.ClientEntity;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.packets.PartOutputPacket;
import no.daffern.vehicle.network.packets.VehicleLayoutPacket;
import no.daffern.vehicle.network.packets.VehicleOutputPacket;
import no.daffern.vehicle.network.packets.WallPacket;
import no.daffern.vehicle.utils.Tools;

/**
 * Created by Daffern on 17.11.2016.
 */
public class ClientVehicle extends ClientEntity {

	public int vehicleId;

	public float width, height;
	public float wallWidth, wallHeight;

	ClientWalls clientWalls;

	public ClientVehicle(final VehicleLayoutPacket vehicleLayoutPacket) {

		clientWalls = new ClientWalls();

		receiveLayout(vehicleLayoutPacket);


	}

	public void receiveOutput(VehicleOutputPacket vop) {
		super.receiveOutput(vop);

		if (vop.partUpdates != null && vop.partUpdates.length > 0)
			for (PartOutputPacket pop : vop.partUpdates)
				clientWalls.updatePart(pop);

	}

	public void receivePartOutput(PartOutputPacket pop) {
		clientWalls.updatePart(pop);
	}


	public void receiveLayout(VehicleLayoutPacket vlp) {
		super.initialize(Common.toPixelCoordinates(vlp.x), Common.toPixelCoordinates(vlp.x), 0);


		this.vehicleId = vlp.vehicleId;
		this.width = Common.toPixelCoordinates(vlp.width);
		this.height = Common.toPixelCoordinates(vlp.height);
		this.wallWidth = Common.toPixelCoordinates(vlp.wallWidth);
		this.wallHeight = Common.toPixelCoordinates(vlp.wallHeight);

		clientWalls.initialize(vlp);

	}

	public void receiveWall(WallPacket wallPacket) {
		clientWalls.setWall(wallPacket);
	}


	public IntVector2 findTileIndex(float x, float y) {
		Vector2 pos = getAbsolutePosition();

		Vector2 rp = Tools.rotatePoint(x, y, pos.x, pos.y, -getAbsoluteAngle() * MathUtils.degreesToRadians);

		IntVector2 vec = new IntVector2();

		vec.x = MathUtils.floor((rp.x - pos.x) / wallWidth);
		vec.y = MathUtils.floor((rp.y - pos.y) / wallHeight);

		return vec;
	}
	public ClientWalls getClientWalls(){
		return clientWalls;
	}

	public void step() {

	}

	public void render(Batch batch) {

		interpolate();

		Vector2 pos = getPosition();

		clientWalls.render(batch, pos.x, pos.y, getAngle());
	}


}
