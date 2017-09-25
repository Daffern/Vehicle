package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import no.daffern.vehicle.container.IntVector2;


public interface PartLayerI {

	void add(IntVector2 wallIndex, int itemId, float width, float height);
	void remove(IntVector2 wallIndex);
	void update(IntVector2 wallIndex, float angle);

	void render(Batch batch, float posX, float posY, float tileWidth, float tileHeight, float angle);
}
