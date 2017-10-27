package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.packets.PartOutputPacket;
import no.daffern.vehicle.network.packets.PartPacket;


public interface PartLayerI {

	void add(IntVector2 wallIndex, PartPacket partPacket);
	void remove(IntVector2 wallIndex);
	void update(IntVector2 wallIndex, PartOutputPacket pop);

	void render(Batch batch, float posX, float posY, float tileWidth, float tileHeight, float angle);
}
