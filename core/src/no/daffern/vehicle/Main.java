package no.daffern.vehicle;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.kotcrab.vis.ui.VisUI;
import no.daffern.vehicle.client.ClientMain;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.menu.StartMenu;
import no.daffern.vehicle.server.ServerMain;
import no.daffern.vehicle.utils.PriorityInputHandler;
import no.daffern.vehicle.utils.TexturePackerHelper;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main implements ApplicationListener {


	StartMenu startMenu;

	ClientMain clientMain;
	ServerMain serverMain;


	@Override
	public void create() {


		TexturePackerHelper.ensureAtlasUpdated("images","packed/pack");
		TexturePackerHelper.updateAtlas("images","packed/pack");


		startMenu = new StartMenu();
		startMenu.load(new StartMenu.StartMenuListener() {

			@Override
			public void onClientClicked() {
				Gdx.graphics.setTitle("Client");
				startMenu.unload();
				clientMain = new ClientMain();
			}

			@Override
			public void onServerClicked() {
				Gdx.graphics.setTitle("Server");
				startMenu.unload();
				serverMain = new ServerMain();

			}

			@Override
			public void onQuickstartClicked() {
				Gdx.graphics.setTitle("Debug");
				startMenu.unload();
				serverMain = new ServerMain(Common.defaultTcpPort, Common.defaultUdpPort);
				String inet = "";
				try {
					inet = InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				clientMain = new ClientMain(inet, Common.defaultTcpPort, Common.defaultUdpPort);


			}
		});


		PriorityInputHandler priorityInputHandler = PriorityInputHandler.getInstance();
		Gdx.input.setInputProcessor(priorityInputHandler);

	}


	@Override
	public void render() {
		Gdx.gl.glClearColor(0.9f, 0.9f, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();


		startMenu.render(deltaTime);


		if (clientMain != null)
			clientMain.render(deltaTime);

		if (serverMain != null)
			serverMain.render(deltaTime);
	}


	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		VisUI.dispose();
	}

	@Override
	public void resize(int width, int height) {
		if (clientMain != null)
			clientMain.resize(width, height);
		if (serverMain != null)
			serverMain.resize(width, height);
	}
}
