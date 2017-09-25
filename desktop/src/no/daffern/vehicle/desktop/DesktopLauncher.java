package no.daffern.vehicle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import no.daffern.vehicle.Main;

public class DesktopLauncher {



	public static void main (String[] arg) {



		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 0;
		config.vSyncEnabled = false;
		config.samples = 5;

		new LwjglApplication(new Main(), config);
	}
}
