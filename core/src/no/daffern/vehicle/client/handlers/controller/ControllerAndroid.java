package no.daffern.vehicle.client.handlers.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.network.packets.PlayerClickPacket;
import no.daffern.vehicle.utils.AbstractInputProcessor;
import no.daffern.vehicle.utils.PriorityInputHandler;
import no.daffern.vehicle.utils.Tools;

public class ControllerAndroid extends Controller {

	private float touchAreaScale = 0.2f;

	public ControllerAndroid() {
		GestureDetector gestureDetector = new GestureDetector(gestListener);
		PriorityInputHandler.getInstance().addInputProcessor(input,100);
		PriorityInputHandler.getInstance().addInputProcessor(gestureDetector,110);
	}

	@Override
	public void render(Batch batch, float delta) {

	}

	AbstractInputProcessor input = new AbstractInputProcessor() {
		@Override
		public boolean keyDown(int keycode) {
			return super.keyDown(keycode);
		}

		@Override
		public boolean keyUp(int keycode) {
			return super.keyUp(keycode);
		}

		@Override
		public boolean keyTyped(char character) {
			return super.keyTyped(character);
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {



			if (screenX < (Gdx.graphics.getWidth() * touchAreaScale)) {
				setLeft(true);
				return true;
			}
			else if (screenX > (Gdx.graphics.getWidth() - Gdx.graphics.getWidth() * touchAreaScale)) {
				setRight(true);
				return true;
			}else{
				Vector3 pos = Tools.mouseToWorldCoordinates(C.cameraHandler.gameCamera, screenX, screenY);

				sendClickPacket(PlayerClickPacket.CLICK_TYPE_BUILD, pos.x, pos.y);
			}
			return true;

		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			setLeft(false);
			setRight(false);
			setUp(false);
			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			return super.touchDragged(screenX, screenY, pointer);
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			return super.mouseMoved(screenX, screenY);
		}

		@Override
		public boolean scrolled(int amount) {
			return super.scrolled(amount);
		}
	};

	GestureDetector.GestureListener gestListener = new GestureDetector.GestureListener() {
		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {



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
			setLeft(x < (Gdx.graphics.getWidth() * touchAreaScale));
			setRight(x > (Gdx.graphics.getWidth() - Gdx.graphics.getWidth() * touchAreaScale));


			setUp(deltaY < -10);
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
