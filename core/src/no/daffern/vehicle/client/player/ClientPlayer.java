package no.daffern.vehicle.client.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.client.ResourceManager;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.graphics.Animation;
import no.daffern.vehicle.network.packets.PlayerOutputPacket;
import no.daffern.vehicle.network.packets.PlayerPacket;

/**
 * Created by Daff on 11.11.2016.
 */
public class ClientPlayer extends ClientEntity{




	private float width, height;
	private float speed;



    private Animation animation;
    private TextureRegion testTexture;

    public int playerId;

    public ClientPlayer(PlayerPacket pp) {
        this.playerId = pp.playerId;
        this.width = Common.toPixelCoordinates(pp.width);
        this.height = Common.toPixelCoordinates(pp.height);

        snapPosition(Common.toPixelCoordinates(pp.x), Common.toPixelCoordinates(pp.y));


        ResourceManager.loadAsset("packed/pack.atlas", TextureAtlas.class, new ResourceManager.AssetListener<TextureAtlas>() {
            @Override
            public void onAssetLoaded(TextureAtlas asset) {
                animation = new Animation(asset,"player/yellow",1);
                animation.setSize(new Vector2(width*1.3f,height*1.3f));
            }
        });

        ResourceManager.loadAsset("crate.png",Texture.class, new ResourceManager.AssetListener<Texture>(){

	        @Override
	        public void onAssetLoaded(Texture asset) {
		        testTexture = new TextureRegion(asset);
	        }
        });

        //C.myClient.addListener(new Listener.LagListener(150,300, listener);
    }

    public void receiveOutput(PlayerOutputPacket playerOutputPacket) {

    	super.receiveOutput(playerOutputPacket);

        speed = playerOutputPacket.speed;

    }


    public void preStep() {

    }

    public void postStep() {

    }


    public void render(Batch batch, float delta) {

		interpolate();
/*
	    if (animation != null) {


            animation.setPosition(inter.x, inter.y);

            if (speed > 0.5f){
                animation.flip(false);
            }else if (speed < -0.5f){
                animation.flip(true);
            }

            animation.render(batch, Math.abs(speed * delta * 3));
        }*/
        if (testTexture != null){

        	Vector2 pos = getPosition();

        	batch.draw(testTexture,pos.x-width/2,pos.y-height/2,width/2,height/2,width,height, 1,1, getNewAngle());

	        //batch.draw(testTexture,posX-width/2,posY-height/2,width/2,height/2,width,height,1,1,angle);

        }
    }

}
