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
public class ServerMenu {

    private Stage stage;
    private Window window;
    private Skin skin;

    private String consoleString="";
    private Label label;

    private static float pad = 2f;
    private static float height = 30f;

    public ServerMenu(){
        ScreenViewport screenViewport = new ScreenViewport();
        screenViewport.setUnitsPerPixel(0.5f);
        stage = new Stage(screenViewport);

        if (!VisUI.isLoaded())
            VisUI.load();
        skin = VisUI.getSkin();

        //stage.setDebugAll(true);

        window = new Window("ServerHandler", skin);
        window.setMovable(true);
        window.setResizable(true);

        window.setPosition(0, 0);

        stage.addActor(window);
    }

    public void loadServerMenu(final ServerMenuListener menuListener){
        window.clear();

        final TextField tcpPortField = new TextField(Common.defaultTcpPort+"",skin);
        final TextField udpPortField = new TextField(Common.defaultUdpPort+"",skin);

        TextButton hostButton = new TextButton("Host", skin);

        hostButton.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.LEFT){
                    if (menuListener != null) {
                        int tcpPort = Integer.parseInt(tcpPortField.getText());
                        int udpPort = Integer.parseInt(udpPortField.getText());


                        menuListener.onHostClicked(tcpPort,udpPort);
                    }
                    return true;
                }
                return false;
            }
        });

        label = new Label("ServerHandler output:", skin);
        label.setWrap(true);
        ScrollPane scrollPane = new ScrollPane(label);

        Table table = new Table(skin);
        table.add(new Label("Port: ",skin));
        table.add(tcpPortField);
        table.row();
        table.add(new Label("UDP port: ",skin));
        table.add(udpPortField);

        window.add(table);
        window.row();
        window.add(hostButton);
        window.row();
        window.add(scrollPane).size(200, height * 2);

        window.pack();
        PriorityInputHandler.getInstance().addInputProcessor(stage,0);

    }
    public void unload(){
        PriorityInputHandler.getInstance().removeInputProcessor(0);
        window.clear();
    }

    public void appendConsole(String string){
        consoleString = consoleString + string + "\n";
        label.setText(consoleString);
    }
    public void render(float delta){
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }
    public interface ServerMenuListener{
        void onHostClicked(int tcpPort, int udpPort);
    }
}
