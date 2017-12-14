package no.daffern.vehicle.menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.utils.PriorityInputHandler;

/**
 * Created by Daff on 13.11.2016.
 */
public class ClientMenu {

    private Stage stage;
    private Window window;
    private Skin skin;

    private static float pad = 2f;
    private static float height = 30f;

    String consoleString="";

    Label label;

    public ClientMenu() {
        ScreenViewport screenViewport = new ScreenViewport();
        //screenViewport.setUnitsPerPixel(0.5f);
        stage = new Stage(screenViewport);

        if (!VisUI.isLoaded())
            VisUI.load();
        skin = VisUI.getSkin();


        window = new Window("Client Menu", skin);
        window.setMovable(true);
        window.setResizable(true);

        window.setPosition(0, 0);

        stage.addActor(window);
    }

    public void loadClientMenu(final ClientMenuListener menuListener) {

        final TextField addressField = new TextField("", skin);
        final TextField tcpPortField = new TextField(Common.defaultTcpPort+"", skin);
        final TextField udpPortField = new TextField(Common.defaultUdpPort+"", skin);

        TextButton joinButton = new TextButton("Join", skin);

        joinButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    if (menuListener != null) {
                        int tcpPort = Integer.parseInt(tcpPortField.getText());
                        int udpPort = Integer.parseInt(udpPortField.getText());
                        menuListener.onJoinClicked(addressField.getText(), tcpPort, udpPort );
                    }
                    return true;
                }
                return false;
            }
        });

        label = new Label("", skin);
        label.setWrap(true);
        ScrollPane scrollPane = new ScrollPane(label);

        Table table = new Table(skin);

        table.add(new Label("IP address: ", skin)).size(100, height).pad(pad);
        table.add(addressField).size(100, height).pad(pad);
        table.row();
        table.add(new Label("TCP port: ", skin)).size(100, height).pad(pad);
        table.add(tcpPortField).size(100, height).pad(pad);
        table.row();
        table.add(new Label("UDP port: ", skin)).size(100, height).pad(pad);
        table.add(udpPortField).size(100, height).pad(pad);

        window.add(table);
        window.row();

        window.add(joinButton).expandX().fillX().pad(pad);
        window.row();
        window.add(scrollPane).size(200, height * 2);

        window.pack();
        window.setMovable(true);

        PriorityInputHandler.getInstance().addInputProcessor(stage, 0);
    }
    public void unload(){
        PriorityInputHandler.getInstance().removeInputProcessor(0);

        stage.clear();
    }

    public void appendConsole(String string){
        consoleString = consoleString + string + "\n";
        label.setText(consoleString);
    }

    public void render(float delta){
    	stage.act(delta);
        stage.draw();
    }
    public void resize(float width, float height){
	    stage.getViewport().setWorldSize(width,height);
	    stage.getViewport().apply();
    }

    public interface ClientMenuListener{
        void onJoinClicked(String address, int tcpPort, int udpPort);
    }
}
