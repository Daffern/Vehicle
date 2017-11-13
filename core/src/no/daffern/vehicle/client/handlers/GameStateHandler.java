package no.daffern.vehicle.client.handlers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.player.ClientPlayer;
import no.daffern.vehicle.client.vehicle.ClientVehicle;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.network.packets.GiveControlPacket;

public class GameStateHandler implements SystemInterface {


	public GameStateHandler() {

		C.myClient.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if (object instanceof GiveControlPacket) {

					GiveControlPacket giveControlPacket = (GiveControlPacket) object;

					ClientPlayer currentPlayer = C.playerHandler.updatePlayer(giveControlPacket.playerPacket);
					ClientVehicle currentVehicle = C.vehicleHandler.getVehicle(giveControlPacket.vehicleId);

					C.cameraHandler.setCurrentPlayer(currentPlayer);
					C.cameraHandler.setCurrentVehicle(currentVehicle);

					C.clientInventory.setCurrentPlayer(currentPlayer);
				}
			}
		});


	}


	@Override
	public void preStep() {

	}

	@Override
	public void postStep() {

	}

	@Override
	public void render(Batch batch, float delta) {

	}
}
