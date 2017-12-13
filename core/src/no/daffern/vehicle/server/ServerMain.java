package no.daffern.vehicle.server;

import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.common.Packets;
import no.daffern.vehicle.menu.ServerMenu;
import no.daffern.vehicle.network.MyServer;
import no.daffern.vehicle.server.handlers.ItemHandler;
import no.daffern.vehicle.server.handlers.ServerPlayerHandler;
import no.daffern.vehicle.server.handlers.ServerVehicleHandler;
import no.daffern.vehicle.server.handlers.TickHandler;
import no.daffern.vehicle.server.world.WorldHandler;

import java.io.IOException;

/**
 * Created by Daffern on 04.11.2016.
 */
public class ServerMain extends Thread {


	ServerMenu serverMenu;

	private long oldTime;
	private long accumulator = 0;


	boolean running = true;

	Object renderLock = new Object();

	public ServerMain() {
		super("ServerMain");

		S.myServer = new MyServer();
		S.worldHandler = new WorldHandler();
		S.itemHandler = new ItemHandler();
		S.playerHandler = new ServerPlayerHandler();
		S.vehicleHandler = new ServerVehicleHandler();
		S.tickHandler = new TickHandler();
/*
        AbstractInputProcessor abstractInputProcessor = new AbstractInputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.UP) {
                    orthographicCamera.translate(0, 10);
                }
                if (keycode == Input.Keys.DOWN) {
                    orthographicCamera.translate(0, -10);
                }
                if (keycode == Input.Keys.LEFT) {
                    orthographicCamera.translate(-10, 0);
                }
                if (keycode == Input.Keys.RIGHT) {
                    orthographicCamera.translate(10, 0);
                }
                orthographicCamera.update();
                return super.keyDown(keycode);
            }


            @Override
            public boolean scrolled(int amount) {
                orthographicCamera.zoom = orthographicCamera.zoom + ( (float)amount * 0.3f);
                if (orthographicCamera.zoom < 0.1f) {
                    orthographicCamera.zoom = 0.1f;
                }
                orthographicCamera.update();
                return super.scrolled(amount);
            }
        };

        PriorityInputHandler.getInstance().addInputProcessor(abstractInputProcessor, 3);
*/
		serverMenu = new ServerMenu();
		serverMenu.loadServerMenu(new ServerMenu.ServerMenuListener() {
			@Override
			public void onHostClicked(int tcpPort, int udpPort) {
				initServer(tcpPort, udpPort);
			}
		});

	}

	//for debugging
	public ServerMain(int tcpPort, int udpPort) {

		S.myServer = new MyServer();
		S.worldHandler = new WorldHandler();
		S.itemHandler = new ItemHandler();
		S.playerHandler = new ServerPlayerHandler();
		S.vehicleHandler = new ServerVehicleHandler();
		S.tickHandler = new TickHandler();


		initServer(tcpPort, udpPort);

		//S.worldHandler.loadWorld("test.tmx");
		S.worldHandler.loadDestructibleWorld();


		start();//start thread
	}

	private void initServer(int tcpPort, int udpPort) {

		S.myServer.register(Packets.networkClasses);

		try {

			S.myServer.bind(tcpPort, udpPort);
			S.myServer.start();

		} catch (IOException e) {
			e.printStackTrace();
			if (serverMenu != null)
				serverMenu.appendConsole("Failed to start on ports: " + tcpPort + " " + udpPort);
		} finally {
			if (serverMenu != null)
				serverMenu.appendConsole("Started server on ports: " + tcpPort + " " + udpPort);
		}
	}

	private int tickCounter = 0;
	private long time = System.currentTimeMillis();
	private int ups = 0;


	@Override
	public void run() {


		while (running) {


			if (System.currentTimeMillis() - time > 1000) {
				//Tools.log(this, "ticks a second: " + tickCounter);
				time = System.currentTimeMillis();
				tickCounter = 0;
			}


			//fix my timestep
			long NANOTIMESTEP = (long) Math.ceil((double) Common.TIME_STEP * 1000000000.0);


			long currentTime = System.nanoTime();
			long deltaTime = currentTime - oldTime;

			oldTime = currentTime;

			long frameTime = Math.min(deltaTime, 250000000);//max 1/4 second between each frame?
			accumulator += frameTime;


			while (accumulator >= NANOTIMESTEP) {

				tickCounter++;

				synchronized (renderLock) {

					S.myServer.dispatchQueues();

					S.playerHandler.preStep();
					S.vehicleHandler.preStep();

					S.worldHandler.worldStep();

					S.tickHandler.step();

					S.playerHandler.postStep();
					S.vehicleHandler.postStep();

				}


				accumulator -= NANOTIMESTEP;
			}
		}
	}


	public void render(float deltaTime) {

		if (Common.debugRender) {
			synchronized (renderLock) {
				S.worldHandler.debugRender();
			}
		}

		if (serverMenu != null)
			serverMenu.render(deltaTime);

	}

	public void resize(int width, int height) {
		S.worldHandler.camera.viewportWidth = width * Common.pixelToUnits;
		S.worldHandler.camera.viewportHeight = height * Common.pixelToUnits;
		S.worldHandler.camera.zoom = Common.cameraZoom;
		S.worldHandler.camera.update();
	}

}
