package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.client.ResourceManager;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.graphics.TileDrawer;
import no.daffern.vehicle.network.MyClient;
import no.daffern.vehicle.network.packets.*;
import no.daffern.vehicle.utils.Tools;

/**
 * Created by Daffern on 17.11.2016.
 */
public class ClientVehicle {

	public int vehicleId;

	private float posX, posY, width, height;
	private float partWidth, partHeight;
	private float angle = 0;
	private float radAngle = 0;

	ClientWalls clientWalls;

	public ClientVehicle(final VehicleLayoutPacket vehicleLayoutPacket) {

		clientWalls = new ClientWalls();

		receiveLayout(vehicleLayoutPacket);


	}

	public void receiveOutput(VehicleOutputPacket vop) {
		posX = Common.toPixelCoordinates(vop.x);
		posY = Common.toPixelCoordinates(vop.y);
		angle = (float) Math.toDegrees(vop.angle);
		radAngle = vop.angle;

		clientWalls.update(posX, posY, angle);

		if (vop.partUpdates != null && vop.partUpdates.length > 0)
			clientWalls.updateParts(vop.partUpdates);

	}


	public void receiveLayout(VehicleLayoutPacket vlp) {
		this.vehicleId = vlp.vehicleId;
		this.posX = Common.toPixelCoordinates(vlp.x);
		this.posY = Common.toPixelCoordinates(vlp.y);
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
		Vector2 rp = Tools.rotatePoint(x, y, posX, posY, -radAngle);


		if (rp.x < posX || rp.x > posX + width || rp.y < posY || rp.y > posY + height)
			return null;

		IntVector2 vec = new IntVector2();


		vec.x = (int) ((rp.x - posX) / partWidth);
		vec.y = (int) ((rp.y - posY) / partHeight);

		return vec;
	}

	public void step() {

	}

	public void render(Batch batch) {

		clientWalls.render(batch);
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

}
