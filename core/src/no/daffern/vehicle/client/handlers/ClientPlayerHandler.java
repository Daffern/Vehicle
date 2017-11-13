package no.daffern.vehicle.client.handlers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.player.ClientPlayer;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.network.packets.GiveControlPacket;
import no.daffern.vehicle.network.packets.PlayerOutputPacket;
import no.daffern.vehicle.network.packets.PlayerPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daff on 24.12.2016.
 */
public class ClientPlayerHandler implements SystemInterface {
	public Map<Integer, ClientPlayer> players = new HashMap<Integer, ClientPlayer>();


	public ClientPlayerHandler() {
		C.myClient.addListener(new Listener() {
			public void received(Connection connection, Object object) {

				if (object instanceof PlayerOutputPacket) {
					PlayerOutputPacket outputPacket = (PlayerOutputPacket) object;

					ClientPlayer clientPlayer = players.get(outputPacket.playerId);
					if (clientPlayer != null)
						clientPlayer.receiveOutput(outputPacket);
				}
				else if (object instanceof PlayerPacket) {
					updatePlayer((PlayerPacket) object);
				}
				else if (object instanceof GiveControlPacket) {
					int i = 0;
				}

			}
		});
	}

	public ClientPlayer updatePlayer(PlayerPacket playerPacket) {
		ClientPlayer player = players.get(playerPacket.playerId);

		if (player == null) {
			player = new ClientPlayer(playerPacket);
			players.put(player.playerId, player);
		}
		return player;
	}


	public void preStep() {
		for (Map.Entry<Integer, ClientPlayer> player : players.entrySet()) {
			player.getValue().preStep();
		}
	}

	public void postStep() {
		for (Map.Entry<Integer, ClientPlayer> player : players.entrySet()) {
			player.getValue().postStep();
		}
	}

	@Override
	public void render(Batch batch, float delta) {
		for (Map.Entry<Integer, ClientPlayer> player : players.entrySet()) {
			player.getValue().render(batch, delta);
		}
	}


}
