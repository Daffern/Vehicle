package no.daffern.vehicle.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

/**
 * Created by Daffern on 15.12.2016.
 */
public class StatusMenu {

	private Skin skin;

	private Stage stage;

    private float fps;
    private int delay;
	private Label label;

    public StatusMenu() {

        ScreenViewport screenViewport = new ScreenViewport();
        stage = new Stage(screenViewport);

        if (!VisUI.isLoaded())
            VisUI.load();
        skin = VisUI.getSkin();

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

    public void setFps(float fps){
        this.fps = fps;
    }
    public void setNetworkDelay(int delay){
    	this.delay = delay;
    }

    public void render(float delta){
        label.setText("FPS: " + fps + ", delay: " + delay + "ms");
        stage.act(delta);
        stage.draw();

    }

}
