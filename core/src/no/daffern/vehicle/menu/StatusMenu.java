package no.daffern.vehicle.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.FloatCounter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.server.S;

/**
 * Created by Daffern on 15.12.2016.
 */
public class StatusMenu {

	private Skin skin;
	private Stage stage;
	private Label label;

	private FloatCounter clientCounter;

    public StatusMenu() {

        ScreenViewport screenViewport = new ScreenViewport();
        stage = new Stage(screenViewport);

        if (!VisUI.isLoaded())
            VisUI.load();
        skin = VisUI.getSkin();

        clientCounter = new FloatCounter(50);
    }

    public void load(){
        label = new Label("FPS: ", skin);
        label.setPosition(0, 0);
        label.setColor(Color.RED);

        stage.addActor(label);
    }
    public void unload(){
        stage.clear();
    }



    public void render(float delta){
    	float fps = Gdx.graphics.getFramesPerSecond();
    	float delay = C.myClient.getReturnTripTime();

    	clientCounter.put(C.myClient.getBpsOutLastFrame());
	    float clientbps = clientCounter.mean.getMean();
	    float serverbps = 0;




    	String text = "FPS: " + fps + ", " + delay + "ms, COBps: " + clientbps + ", SOBps: " + serverbps + ", "+ S.myServer.getBpsOut();


        label.setText(text);
        stage.act(delta);
        stage.draw();

    }

}
