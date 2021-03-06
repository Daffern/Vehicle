package no.daffern.vehicle.client.handlers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.vehicle.ClientVehicle;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.network.packets.PartOutputPacket;
import no.daffern.vehicle.network.packets.VehicleLayoutPacket;
import no.daffern.vehicle.network.packets.VehicleOutputPacket;
import no.daffern.vehicle.network.packets.WallPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daff on 24.12.2016.
 */
public class ClientVehicleHandler implements SystemInterface{
    private Map<Integer, ClientVehicle> vehicles;

    public ClientVehicleHandler() {
        vehicles = new HashMap<>();

        C.myClient.addListener(new Listener() {
            public void received(Connection connection, Object object) {

                if (object instanceof VehicleLayoutPacket) {
                    VehicleLayoutPacket vlp = (VehicleLayoutPacket)object;
                    ClientVehicle vehicle = vehicles.get(vlp.vehicleId);

                    if (vehicle == null) {
                        vehicle = new ClientVehicle(vlp);
                        vehicles.put(vlp.vehicleId, vehicle);
                    }else {
                        vehicle.receiveLayout(vlp);
                    }
                }
                else if (object instanceof VehicleOutputPacket){
                    VehicleOutputPacket vop = (VehicleOutputPacket)object;

                    ClientVehicle vehicle = vehicles.get(vop.vehicleId);
                    if (vehicle != null)
                        vehicle.receiveOutput(vop);
                }
                else if (object instanceof PartOutputPacket){
                	PartOutputPacket pop = (PartOutputPacket)object;

	                ClientVehicle vehicle = vehicles.get(pop.vehicleId);
	                if (vehicle != null)
		                vehicle.receivePartOutput(pop);

                }
                else if (object instanceof WallPacket){
                	WallPacket wp = (WallPacket)object;

	                ClientVehicle vehicle = vehicles.get(wp.vehicleId);
					if (vehicle != null)
						vehicle.receiveWall(wp);

                }
            }
        });
    }


    public ClientVehicle getVehicle(int vehicleId){
        return vehicles.get(vehicleId);
    }

    @Override
    public void preStep() {
        for (Map.Entry<Integer, ClientVehicle> vehicle : vehicles.entrySet()) {
            vehicle.getValue().step();
        }
    }

    @Override
    public void postStep() {

    }

    @Override
    public void render(Batch batch, float delta) {
        for (Map.Entry<Integer, ClientVehicle> vehicle : vehicles.entrySet()) {
            vehicle.getValue().render(batch);
        }
    }
}
