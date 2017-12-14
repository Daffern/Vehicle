package no.daffern.vehicle.client.handlers.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.network.packets.PlayerClickPacket;
import no.daffern.vehicle.utils.AbstractInputProcessor;
import no.daffern.vehicle.utils.PriorityInputHandler;
import no.daffern.vehicle.utils.Tools;

/**
 * Created by Daffern on 08.11.2016.
 */
public class ControllerDesktop extends Controller {

	//private VehicleTemplateDrawer templateDrawer = new VehicleTemplateDrawer();

	public ControllerDesktop() {

		PriorityInputHandler.getInstance().addInputProcessor(inputProcessor, 100);

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
			if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_6) {
				inventory.setSelectedItem(keycode - Input.Keys.NUM_1);
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

			Vector2 worldPos = Tools.mouseToWorldCoordinates(C.cameraHandler.gameCamera, screenX, screenY);

			//ClientVehicle vehicle = C.vehicleHandler.getVehicle(inventory.currentPlayer.vehicleId);
			//templateDrawer.startDraw(vehicle,worldPos.x, worldPos.y, inventory.getSelected().getTextureRegion());

			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {

			//templateDrawer.endDraw();

			Vector2 uiPos = Tools.mouseToWorldCoordinates(C.cameraHandler.uiCamera, screenX, screenY);

			if (inventory.trySetSelectedItem(uiPos.x, uiPos.y))
				return true;

			Vector2 worldPos = Tools.mouseToWorldCoordinates(C.cameraHandler.gameCamera, screenX, screenY);

			//float angle = templateDrawer.getAngle();
			//IntVector2 index = templateDrawer.getDragStartIndex();

			switch (button) {
				case Input.Buttons.LEFT:
					sendClickPacket(PlayerClickPacket.CLICK_TYPE_USE, worldPos.x, worldPos.y, 0);
					break;
				case Input.Buttons.RIGHT:
					sendClickPacket(PlayerClickPacket.CLICK_TYPE_INTERACT1, worldPos.x, worldPos.y, 0);
					break;
			}
			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {

			Vector2 pos = Tools.mouseToWorldCoordinates(C.cameraHandler.gameCamera, screenX, screenY);

			//templateDrawer.update(pos);

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


	@Override
	public void render(Batch batch, float delta) {
		//templateDrawer.render(batch);
	}
}
