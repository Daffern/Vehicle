package no.daffern.vehicle.server.handlers;

import no.daffern.vehicle.server.vehicle.ServerVehicle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daff on 25.12.2016.
 */
public class ServerVehicleHandler implements ServerHandler {
    public Map<Integer, ServerVehicle> vehicles;


    public ServerVehicleHandler() {
        vehicles = new HashMap<Integer, ServerVehicle>();

    }

    public ServerVehicle spawnNewVehicle(){
        ServerVehicle serverVehicle = new ServerVehicle(0, 50);
        vehicles.put(serverVehicle.vehicleId, serverVehicle);

        sendVehicleLayouts();
        return serverVehicle;
    }
    public void sendVehicleLayouts(){
        for (Map.Entry<Integer, ServerVehicle> vehicle : vehicles.entrySet()) {
            vehicle.getValue().sendVehicleLayout();
        }
    }

    public ServerVehicle findVehicle(float x, float y){
        for (Map.Entry<Integer, ServerVehicle> entry : vehicles.entrySet()){
            if (entry.getValue().isInside(x,y)){
                return entry.getValue();
            }
        }
        return null;
    }




    @Override
    public void preStep() {
        for (Map.Entry<Integer, ServerVehicle> vehicle : vehicles.entrySet()) {
            vehicle.getValue().preStep();
        }
    }

    @Override
    public void postStep() {
        for (Map.Entry<Integer, ServerVehicle> vehicle : vehicles.entrySet()) {
            vehicle.getValue().postStep();
        }
    }
}
