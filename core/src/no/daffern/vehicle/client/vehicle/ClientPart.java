package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.handlers.ItemHandler;
import no.daffern.vehicle.network.packets.GameItemPacket;

/**
 * Created by Daffern on 08.06.2017.
 */
public class ClientPart {
    int itemId;
    //position x,y on the vehicle
    float width, height;
    float angle;
    TextureAtlas.AtlasRegion region;

    public ClientPart(int itemId, float width, float height) {
        this(itemId, width, height, 0);
    }

    public ClientPart(int itemId, float width, float height, float angle) {
        this.itemId = itemId;

        this.width = width;
        this.height = height;
        this.angle = angle;

        C.itemHandler.loadGameItem(itemId, new ItemHandler.GameItemListener(){

	        @Override
	        public void onGameItemLoaded(TextureAtlas textureAtlas, GameItemPacket gameItem) {
		        region = textureAtlas.findRegion(gameItem.iconName);
	        }


        });

    }

	public void render(Batch batch, float posX, float posY, float angle) {


		if (region != null)
			batch.draw(region,
					posX - width / 2, posY - height / 2,
					width / 2, height / 2,
					width, height,
					1, 1, this.angle + angle);
	}
}
