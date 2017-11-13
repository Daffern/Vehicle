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
	private Label updatesLabel;
	private Label worldLabel;

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
        updatesLabel = new Label("", skin);
        updatesLabel.setPosition(0, 30);
        updatesLabel.setColor(Color.RED);
        stage.addActor(updatesLabel);

        worldLabel = new Label("",skin);
	    worldLabel.setPosition(0,60);
	    worldLabel.setColor(Color.RED);
	    stage.addActor(worldLabel);
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

    	String updatedText = "FPS: " + fps + ", " + delay + "ms, COBps: " + clientbps + ", SOBps: " + serverbps;
    	if (S.myServer != null ) updatedText = updatedText + ", " + S.myServer.getBpsOut();
        updatesLabel.setText(updatedText);


        String worldText = "";
        if (S.worldHandler != null){
        	worldText += "Total bodies: " + S.worldHandler.world.getBodyCount() + ", Total fixtures: " + S.worldHandler.world.getFixtureCount()+"\n";
        	worldText += "WorldGen: " + S.worldHandler.getWorldGenerator().getDebugString();
        }

        worldLabel.setText(worldText);

        stage.act(delta);
        stage.draw();

    }

}
