package no.daffern.vehicle.client.handlers.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.network.packets.PlayerClickPacket;
import no.daffern.vehicle.utils.AbstractInputProcessor;
import no.daffern.vehicle.utils.PriorityInputHandler;
import no.daffern.vehicle.utils.Tools;

/**
 * Created by Daffern on 08.11.2016.
 */
public class ControllerDesktop extends Controller {



    public ControllerDesktop() {

        PriorityInputHandler.getInstance().addInputProcessor(inputProcessor,100);

    }


    @Override
    public void render(Batch batch, float delta) {

    }


    private AbstractInputProcessor inputProcessor = new AbstractInputProcessor() {

	    @Override
	    public boolean keyDown(int keycode) {
		    switch (keycode) {
			    case Input.Keys.W:
				    setUp(true);
				    return true;
			    case Input.Keys.A:
				    setLeft(true);
				    return true;
			    case Input.Keys.D:
				   setRight(true);
				    return true;
		    }

		    return false;

	    }

	    @Override
	    public boolean keyUp(int keycode) {
		    switch (keycode) {
			    case Input.Keys.W:
				    setUp(false);
				    return true;
			    case Input.Keys.A:
				    setLeft(false);
				    return true;
			    case Input.Keys.D:
				    setRight(false);
				    return true;
		    }
		    return false;
	    }

	    @Override
	    public boolean keyTyped(char character) {
		    return false;
	    }

	    @Override
	    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		    Vector3 pos = Tools.mouseToWorldCoordinates(C.cameraHandler.gameCamera, screenX, screenY);

		    sendClickPacket(PlayerClickPacket.CLICK_TYPE_BUILD, pos.x, pos.y);

		    return false;
	    }

	    @Override
	    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		    return false;
	    }

	    @Override
	    public boolean touchDragged(int screenX, int screenY, int pointer) {
		    return false;
	    }

	    @Override
	    public boolean mouseMoved(int screenX, int screenY) {
		    return false;
	    }

	    @Override
	    public boolean scrolled(int amount) {
		    return false;
	    }

    };

}
