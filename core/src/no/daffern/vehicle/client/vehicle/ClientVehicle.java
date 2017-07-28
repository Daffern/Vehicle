package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.client.player.ClientEntity;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.packets.VehicleLayoutPacket;
import no.daffern.vehicle.network.packets.VehicleOutputPacket;
import no.daffern.vehicle.network.packets.WallPacket;
import no.daffern.vehicle.utils.Tools;

/**
 * Created by Daffern on 17.11.2016.
 */
public class ClientVehicle extends ClientEntity{

	public int vehicleId;

	private float width, height;
	private float partWidth, partHeight;

	ClientWalls clientWalls;

	public ClientVehicle(final VehicleLayoutPacket vehicleLayoutPacket) {

		clientWalls = new ClientWalls();

		receiveLayout(vehicleLayoutPacket);


	}

	public void receiveOutput(VehicleOutputPacket vop) {


		super.receiveOutput(vop);


		if (vop.partUpdates != null && vop.partUpdates.length > 0)
			clientWalls.updateParts(vop.partUpdates);

	}


	public void receiveLayout(VehicleLayoutPacket vlp) {
		super.initialize(Common.toPixelCoordinates(vlp.x), Common.toPixelCoordinates(vlp.x), 0);


		this.vehicleId = vlp.vehicleId;
		this.width = Common.toPixelCoordinates(vlp.width);
		this.height = Common.toPixelCoordinates(vlp.height);
		this.partWidth = Common.toPixelCoordinates(vlp.partWidth);
		this.partHeight = Common.toPixelCoordinates(vlp.partHeight);

		clientWalls.initialize(vlp);

	}

	public void receiveWall(WallPacket wallPacket) {
		clientWalls.setWall(wallPacket);
	}



	public IntVector2 findTileIndex(float x, float y) {
		Vector2 pos = getAbsolutePosition();

		Vector2 rp = Tools.rotatePoint(x, y, pos.x, pos.y, getAbsoluteAngle());


		if (rp.x < pos.x || rp.x > pos.x + width || rp.y < pos.y || rp.y > pos.y + height)
			return null;

		IntVector2 vec = new IntVector2();


		vec.x = (int) ((rp.x - pos.x) / partWidth);
		vec.y = (int) ((rp.y - pos.y) / partHeight);

		return vec;
	}

	public void step() {

	}

	public void render(Batch batch) {

		interpolate();

		Vector2 pos = getPosition();

		clientWalls.render(batch,pos.x, pos.y, getAngle());
	}



}
