package no.daffern.vehicle.server.handlers;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.network.packets.GiveControlPacket;
import no.daffern.vehicle.network.packets.PlayerClickPacket;
import no.daffern.vehicle.network.packets.PlayerInputPacket;
import no.daffern.vehicle.network.packets.PlayerPacket;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.server.player.ServerPlayer;
import no.daffern.vehicle.server.vehicle.ServerVehicle;

import java.util.HashMap;
import java.util.Map;

import static no.daffern.vehicle.server.S.myServer;

/**
 * Created by Daff on 25.12.2016.
 */
public class ServerPlayerHandler implements ServerHandler {

    public Map<Integer, ServerPlayer> players;


    public ServerPlayerHandler() {
        players = new HashMap<Integer, ServerPlayer>();
        myServer.addListener(new Listener() {
            public void connected(Connection connection) {


                ServerVehicle serverVehicle;
                if (S.vehicleHandler.vehicles.size() == 0) {
                    serverVehicle = S.vehicleHandler.spawnNewVehicle();
                } else {
                    serverVehicle = S.vehicleHandler.vehicles.entrySet().iterator().next().getValue();
                    serverVehicle.sendVehicleLayout();
                }


                int playerId = connection.getID();

                Vector2 spawn = serverVehicle.findPlayerSpawnPoint();
                ServerPlayer player = new ServerPlayer(playerId, serverVehicle, spawn.x, spawn.y);
                players.put(playerId, player);

                PlayerPacket playerPacket = player.getPlayerInfo();

                GiveControlPacket giveControlPacket = new GiveControlPacket();
                giveControlPacket.clientId = playerId;
                giveControlPacket.playerPacket = playerPacket;

                S.myServer.sendToTCP(playerId, giveControlPacket);
                S.myServer.sendToAllTCP(playerPacket);

                //TODO #######REMOVE IMPORTANT#########
                player.inventory.addItem(S.itemHandler.getItemByName("vehicle/1/center"), 1, true);
                player.inventory.addItem(S.itemHandler.getItemByName("tools/eraser"), 1, true);
                player.inventory.addItem(S.itemHandler.getItemByName("tools/shovel"), 1, true);
                //player.inventory.addItem(S.itemHandler.getItemByName("vehicle/triangle_wall.png"), 1, false);
                player.inventory.addItem(S.itemHandler.getItemsOfType(GameItemTypes.PART_TYPE_WHEEL).get(0), 1, true);
                player.inventory.addItem(S.itemHandler.getItemsOfType(GameItemTypes.PART_TYPE_AXLE).get(0), 1, true);
                player.inventory.addItem(S.itemHandler.getItemsOfType(GameItemTypes.PART_TYPE_ENGINE).get(0), 1, true);
                player.inventory.addItem(S.itemHandler.getItemsOfType(GameItemTypes.PART_TYPE_BATTERY).get(0), 1, true);
                player.inventory.addItem(S.itemHandler.getItemsOfType(GameItemTypes.PART_TYPE_WIRE).get(0), 1, true);
                player.inventory.addItem(S.itemHandler.getItemsOfType(GameItemTypes.PART_TYPE_SOLAR).get(0), 1, true);
                player.inventory.addItem(S.itemHandler.getItemsOfType(GameItemTypes.PART_TYPE_DRILL).get(0), 1, true);



            }

            public void received(Connection connection, Object object) {
                int clientId = connection.getID();

                ServerPlayer player = players.get(clientId);

                if (object instanceof PlayerInputPacket)
                    player.receiveInput((PlayerInputPacket) object);

                else if (object instanceof PlayerClickPacket)
                    player.receiveInventoryUsePacket((PlayerClickPacket)object);

            }
        });
    }


    @Override
    public void preStep() {
        for (Map.Entry<Integer, ServerPlayer> player : players.entrySet()) {
            player.getValue().preStep();
        }
    }

    @Override
    public void postStep() {
        for (Map.Entry<Integer, ServerPlayer> player : players.entrySet()) {
            player.getValue().postStep();
        }
    }
}
