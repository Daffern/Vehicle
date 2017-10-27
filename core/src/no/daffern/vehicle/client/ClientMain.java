package no.daffern.vehicle.client;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.client.handlers.*;
import no.daffern.vehicle.client.handlers.controller.ControllerAndroid;
import no.daffern.vehicle.client.handlers.controller.ControllerDesktop;
import no.daffern.vehicle.client.player.ClientInventory;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.common.Packets;
import no.daffern.vehicle.common.SystemSystem;
import no.daffern.vehicle.menu.ClientMenu;
import no.daffern.vehicle.menu.StatusMenu;
import no.daffern.vehicle.network.MyClient;

import java.io.IOException;

/**
 * Created by Daffern on 04.11.2016.
 */
public class ClientMain extends Thread {


    ClientMenu clientMenu;
    StatusMenu statusMenu;

    Batch batch;

    private float accumulator = 0;
    double oldTime = 0;
    boolean running = true;

    SystemSystem systemSystem;

    public ClientMain(String ip, int tcpPort, int udpPort) {

        initialize();

        start();//start thread

        try {
	        C.myClient.start();
	        C.myClient.connect(100000, ip, tcpPort, udpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        statusMenu = new StatusMenu();
        statusMenu.load();


    }

    private void initialize() {

        batch = new PolygonSpriteBatch();




        systemSystem = new SystemSystem(
                C.myClient = new MyClient(),
                C.cameraHandler = new CameraHandler(),
                C.itemHandler = new ItemHandler(),

                C.clientInventory = new ClientInventory(),

                C.controller = (Gdx.app.getType() == Application.ApplicationType.Android ? new ControllerAndroid() : new ControllerDesktop()),

                C.vehicleHandler = new ClientVehicleHandler(),
                C.playerHandler = new ClientPlayerHandler(),
                C.mapHandler = new MapHandler()
        );

        C.myClient.register(Packets.networkClasses);
        C.myClient.addListener(new Listener(){
            @Override
            public void disconnected (Connection connection) {
                try {
                    C.myClient.reconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public ClientMain() {

        initialize();

        start();

        clientMenu = new ClientMenu();
        clientMenu.loadClientMenu(new ClientMenu.ClientMenuListener() {
            @Override
            public void onJoinClicked(String address, int tcpPort, int udpPort) {

                try {
	                C.myClient.start();
	                C.myClient.connect(10000, address, tcpPort, udpPort);
                } catch (IOException e) {
                    clientMenu.appendConsole("failed to connect");
                    e.printStackTrace();
                } finally {
                    clientMenu.appendConsole("connected to server");
                    clientMenu.unload();

                }

            }
        });

    }

    @Override
    public void run() {

        while (running) {

            /*
            try {
                C.myClient.update(25);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            double currentTime = (double) System.currentTimeMillis() / 1000.0;
            float delta = (float) (currentTime - oldTime);
            oldTime = currentTime;

            float frameTime = Math.min(delta, 0.25f);
            accumulator += frameTime;

            while (accumulator >= Common.TIME_STEP) {

                //Tools.log(this, C.cameraHandler.gameCamera.position.toString());

                systemSystem.preStep();
                C.mapHandler.worldStep();
                systemSystem.postStep();


                accumulator -= Common.TIME_STEP;
            }

            if (clientMenu != null)
                clientMenu.step(delta);
        }
    }

    public void render(float delta) {

        C.myClient.dispatchQueues();

        ResourceManager.update();

        C.cameraHandler.updatePosition(delta);

        batch.begin();

        C.cameraHandler.useGameCamera(batch);

        C.mapHandler.render(batch,delta);
        C.vehicleHandler.render(batch,delta);
        C.playerHandler.render(batch,delta);

        C.cameraHandler.useUICamera(batch);

        C.clientInventory.render(batch,delta);


        batch.end();

        if (clientMenu != null)
            clientMenu.render();

        if (statusMenu != null) {

            statusMenu.render(delta);
        }

       // C.mapHandler.debugRender();

    }

    public void resize(int width, int height) {

        C.cameraHandler.updateScreenSize(width,height);
    }



}
