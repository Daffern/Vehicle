package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.ResourceManager;
import no.daffern.vehicle.client.handlers.ItemHandler;

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

        C.itemHandler.loadRegion(itemId, new ItemHandler.AtlasRegionListener(){

	        @Override
	        public void onRegionLoaded(TextureAtlas.AtlasRegion atlasRegion) {
		        region = atlasRegion;
	        }
        });

    }
}
