package no.daffern.vehicle.client.handlers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.player.ClientInventory;
import no.daffern.vehicle.client.player.ClientPlayer;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.network.MyClient;
import no.daffern.vehicle.network.packets.PlayerClickPacket;
import no.daffern.vehicle.network.packets.PlayerInputPacket;
import no.daffern.vehicle.utils.PriorityInputHandler;
import no.daffern.vehicle.utils.Tools;

/**
 * Created by Daffern on 08.11.2016.
 */
public class Controller implements InputProcessor, SystemInterface {


    public static final byte up = 0;
    public static final byte down = 1;
    public static final byte left = 2;
    public static final byte right = 3;
    public static final byte leftMouseClick = 4;
    public static final byte rightMouseClick = 5;

    MyClient myClient;

    private final float touchAreaScale = 0.2f;


    PlayerInputPacket playerInputPacket = new PlayerInputPacket();

    ClientPlayer currentPlayer;
    ClientInventory clientInventory;

    boolean isMobile = Gdx.app.getType() == Application.ApplicationType.Android;
    //boolean isMobile = true;


    public Controller() {
        myClient = C.myClient;
        clientInventory = C.clientInventory;

        PriorityInputHandler.getInstance().addInputProcessor(Controller.this, 100);

        if (isMobile) {
            GestureDetector gestureDetector = new GestureDetector(gestListener);
            PriorityInputHandler.getInstance().addInputProcessor(gestureDetector, 110);
        }

    }

    public void setCurrentPlayer(ClientPlayer clientPlayer) {
        this.currentPlayer = clientPlayer;
    }


    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
                playerInputPacket.upPressed = true;
                return true;
            /*case Input.Keys.S:
                playerInputPacket.upPressed = true;
                myClient.sendTCP(playerInputPacket);
                return true;*/
            case Input.Keys.A:
                playerInputPacket.leftPressed = true;
                return true;
            case Input.Keys.D:
                playerInputPacket.rightPressed = true;
                return true;
        }

	    return clientInventory.keyDown(keycode);

    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
                playerInputPacket.upPressed = false;
                return true;

            case Input.Keys.A:
                playerInputPacket.leftPressed = false;

                return true;
            case Input.Keys.D:
                playerInputPacket.rightPressed = false;

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

        if (clientInventory.touchDown(screenX,screenY,pointer,button)){
            return true;
        }
        else{
            ClientInventory.InventorySlot item = clientInventory.getSelected();
            if (item == null)
                return false;
            Vector3 pos = Tools.mouseToWorldCoordinates(C.cameraHandler.gameCamera, screenX, screenY);

            PlayerClickPacket playerClickPacket = new PlayerClickPacket();
            playerClickPacket.playerId = currentPlayer.playerId;
            playerClickPacket.itemId = item.getItemId();
            playerClickPacket.x = pos.x;
            playerClickPacket.y = pos.y;

            myClient.sendTCP(playerClickPacket);

        }

        if (button == Input.Buttons.LEFT) {

            //System.out.println("mouse clicked at: " + pos.x + "  " + pos.y);




        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if (isMobile) {
            playerInputPacket.upPressed = false;
            playerInputPacket.leftPressed = false;
            playerInputPacket.rightPressed = false;
        }
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



    @Override
    public void preStep() {

    }

    @Override
    public void postStep() {
        if (C.myClient.isConnected())
            C.myClient.sendUDP(playerInputPacket);
    }

    @Override
    public void render(Batch batch, float delta) {

    }

    GestureDetector.GestureListener gestListener = new GestureDetector.GestureListener() {
        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            if (isMobile) {
                if (x < (Gdx.graphics.getWidth() * touchAreaScale)) {
                    playerInputPacket.leftPressed = true;
                    return true;
                } else if (x > (Gdx.graphics.getWidth() - Gdx.graphics.getWidth() * touchAreaScale)) {
                    playerInputPacket.rightPressed = true;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            return false;
        }

        @Override
        public boolean longPress(float x, float y) {
            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {

            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
	        playerInputPacket.leftPressed = x < (Gdx.graphics.getWidth() * touchAreaScale);
	        playerInputPacket.rightPressed = x > (Gdx.graphics.getWidth() - Gdx.graphics.getWidth() * touchAreaScale);

	        playerInputPacket.upPressed = deltaY < -10;
            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            return false;
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }

        @Override
        public void pinchStop() {

        }
    };
}
