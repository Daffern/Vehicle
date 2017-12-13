package no.daffern.vehicle.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.client.vehicle.ClientVehicle;
import no.daffern.vehicle.container.IntVector2;

public class VehicleTemplateDrawer {

	private Sprite sprite = new Sprite();
	private boolean drawing = false;

	private ClientVehicle clientVehicle;
	private IntVector2 dragStartIndex = new IntVector2();

	public void startDraw(ClientVehicle clientVehicle, float x, float y, TextureRegion textureRegion) {
		this.clientVehicle = clientVehicle;
		this.dragStartIndex = clientVehicle.findTileIndex(x, y);
		sprite.setColor(0.5f,1f, 0.5f, 0.7f);

		int itemId = clientVehicle.getClientWalls().getWallItemId(dragStartIndex);
		if (itemId == 0)//there is no wall, dont draw
			drawing = false;
		else {
			//set actual position
			sprite.setRegion(textureRegion);
			drawing = true;
		}
	}

	public void endDraw() {
		drawing = false;
	}

	public void update(Vector2 dragPos) {

		Vector2 pos = new Vector2(dragStartIndex.x * clientVehicle.wallWidth + clientVehicle.wallWidth / 2 - sprite.getWidth() / 2,
				dragStartIndex.y * clientVehicle.wallHeight + clientVehicle.wallHeight / 2 - sprite.getHeight() / 2);

		float angle = pos.sub(dragPos).angle();

		sprite.setPosition(pos.x, pos.y);
		sprite.setRotation(angle);
	}
	public float getAngle(){
		return sprite.getRotation();
	}

	public IntVector2 getDragStartIndex() {
		return dragStartIndex;
	}

	public void render(Batch batch) {
		if (drawing) {
			sprite.draw(batch);
		}
	}
}
