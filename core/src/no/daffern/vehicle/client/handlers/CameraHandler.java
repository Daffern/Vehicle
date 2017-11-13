package no.daffern.vehicle.client.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import no.daffern.vehicle.client.player.ClientPlayer;
import no.daffern.vehicle.client.vehicle.ClientVehicle;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.network.packets.GiveControlPacket;

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
    private ClientPlayer currentPlayer;
    private ClientVehicle currentVehicle;


    private List<CameraListener> cameraListeners = new ArrayList<CameraListener>();

    public CameraHandler() {
        this.gameCamera = new OrthographicCamera();
        this.debugCamera = new OrthographicCamera();
        this.uiCamera = new OrthographicCamera();

        updateScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }

    public void setCurrentPlayer(ClientPlayer clientPlayer){
        this.currentPlayer = clientPlayer;
    }
    public void setCurrentVehicle(ClientVehicle clientVehicle){
    	this.currentVehicle = clientVehicle;
    }


    public void updatePosition(float delta) {

        if (currentPlayer != null) {

	        Vector2 playerPos = currentPlayer.getPosition();
	        float camX = MathUtils.lerp(gameCamera.position.x, playerPos.x, 1f - (float) Math.pow(cameraInterpolation, delta));
	        float camY = MathUtils.lerp(gameCamera.position.y, playerPos.y, 1f - (float) Math.pow(cameraInterpolation, delta));
	        //float camX = gameCamera.position.x + camSpeed * (activePlayer.posX - gameCamera.position.x);
	        //float camY = gameCamera.position.y + camSpeed * (activePlayer.posY - gameCamera.position.y);

	        if (currentVehicle == null) {
				setCamera(playerPos.x, playerPos.y,0);
			}else{
		        setCamera(playerPos.x, playerPos.y,0);
		        //setCamera(playerPos.x, playerPos.y, currentVehicle.getAngle());
	        }

        }
    }

    private void setCamera(float x, float y, float angle){
	    gameCamera.up.x = MathUtils.sinDeg(-angle);
	    gameCamera.up.y = MathUtils.cosDeg(-angle);
        gameCamera.position.x = x;
        gameCamera.position.y = y;
        gameCamera.update();

	    debugCamera.rotate(new Vector3(0,0,-1), angle);
        debugCamera.position.x = Common.toWorldCoordinates(x);
        debugCamera.position.y = Common.toWorldCoordinates(y);
	    debugCamera.update();
    }

    public void updateScreenSize(int width, int height) {
        gameCamera.viewportWidth = width;
        gameCamera.viewportHeight = height;
        gameCamera.zoom = Common.cameraZoom;
        gameCamera.update();

        debugCamera.viewportWidth = Common.toWorldCoordinates(width);
        debugCamera.viewportHeight = Common.toWorldCoordinates(height);
        debugCamera.zoom = Common.cameraZoom;
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
