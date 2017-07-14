package no.daffern.vehicle.client.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.player.ClientPlayer;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.network.packets.GiveControlPacket;
import no.daffern.vehicle.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daff on 24.12.2016.
 */
public class CameraHandler implements SystemInterface{

    public OrthographicCamera gameCamera;
    public OrthographicCamera debugCamera;
    public OrthographicCamera uiCamera;


    private float cameraInterpolation = 0.05f;

    private GiveControlPacket controlPacket;
    public ClientPlayer activePlayer;

    public float zoom = 1f;

    private List<CameraListener> cameraListeners = new ArrayList<CameraListener>();

    public CameraHandler() {
        this.gameCamera = new OrthographicCamera();
        this.debugCamera = new OrthographicCamera();
        this.uiCamera = new OrthographicCamera();

        updateScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        C.myClient.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof GiveControlPacket) {
                    controlPacket = ((GiveControlPacket) object);
                }
            }
        });


    }


    public void updatePosition(float delta) {

        if (activePlayer != null) {


            Vector2 playerPos = activePlayer.getPosition();

            float camX = MathUtils.lerp(gameCamera.position.x, playerPos.x, 1f - (float)Math.pow(cameraInterpolation, delta));
            float camY = MathUtils.lerp(gameCamera.position.y, playerPos.y, 1f - (float)Math.pow(cameraInterpolation, delta));
            //float camX = gameCamera.position.x + camSpeed * (activePlayer.posX - gameCamera.position.x);
            //float camY = gameCamera.position.y + camSpeed * (activePlayer.posY - gameCamera.position.y);



            setCameraPositions(playerPos.x, playerPos.y);

        } else if (controlPacket != null) {
            activePlayer = C.playerHandler.players.get(controlPacket.clientId);
	        Vector2 playerPos = activePlayer.getPosition();

	        setCameraPositions(playerPos.x, playerPos.y);

        }
    }

    private void setCameraPositions(float x, float y){
        gameCamera.position.x = x;
        gameCamera.position.y = y;
        gameCamera.update();

        debugCamera.position.x = Common.toWorldCoordinates(x);
        debugCamera.position.y = Common.toWorldCoordinates(y);
        debugCamera.update();
    }

    public void updateScreenSize(int width, int height) {
        gameCamera.viewportWidth = width * Common.cameraScaleX * zoom;
        gameCamera.viewportHeight = height * Common.cameraScaleY * zoom;
        gameCamera.update();

        debugCamera.viewportWidth = Common.toWorldCoordinates(width) * Common.cameraScaleX;
        debugCamera.viewportHeight = Common.toWorldCoordinates(height)* Common.cameraScaleY ;
        debugCamera.update();

        uiCamera.viewportWidth = width ;
        uiCamera.viewportHeight = height;
        uiCamera.update();


        for (CameraListener listener : cameraListeners){
            listener.sizeUpdated(gameCamera, uiCamera, debugCamera);
        }


    }

    public void useGameCamera(Batch batch) {
        batch.setProjectionMatrix(gameCamera.combined);
    }

    public void useUICamera(Batch batch) {
        batch.setProjectionMatrix(uiCamera.combined);
    }

    public void listen(CameraListener cameraListener){
        cameraListeners.add(cameraListener);
    }

    @Override
    public void preStep() {

    }

    @Override
    public void postStep() {

    }

    @Override
    public void render(Batch batch, float delta) {

    }

    public interface CameraListener {
        void sizeUpdated(OrthographicCamera gameCamera, OrthographicCamera uiCamera, OrthographicCamera debugCamera);
    }
}
