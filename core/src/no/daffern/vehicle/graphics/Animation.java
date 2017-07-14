package no.daffern.vehicle.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import no.daffern.vehicle.utils.Tools;

import java.util.List;

/**
 * Created by Daffern on 04.04.2017.
 */
public class Animation  {


    private TextureRegion[] frames;

    private int frameIndex = 0;
    private float accumulator = 0;

    private float frameDuration = 0.15f;

    private float x, y, angle;
    private Vector2 size;
    private Vector2 scale;
    private boolean flip;

    public Animation(Texture texture, int framesX, int framesY, float frameDuration) {

        frames = Tools.toSingleArray(TextureRegion.split(texture, texture.getWidth() / framesX, texture.getHeight() / framesY)).toArray(TextureRegion.class);

	    this.size = new Vector2(frames[0].getRegionWidth(),frames[0].getRegionHeight());
	    this.scale = new Vector2(1,1);
        this.frameDuration = frameDuration;
    }
    public Animation(TextureAtlas textureAtlas, String name, float frameDuration){

    	Array<TextureAtlas.AtlasRegion> regions = textureAtlas.findRegions(name);
	    frames = regions.toArray(TextureAtlas.AtlasRegion.class);

	    this.size = new Vector2(frames[0].getRegionWidth(),frames[0].getRegionHeight());
	    this.scale = new Vector2(1,1);
	    this.frameDuration = frameDuration;
    }

    public void setSize(Vector2 size) {
        this.size = size;
    }
    public Vector2 getSize(){
        return size;
    }
    public void setScale(Vector2 scale){
        this.scale = scale;
    }

    public void setPosition(float x, float y) {
        this.x = x - size.x/2;
        this.y = y - size.y/2;
    }
    public void setFrameDuration(float duration){
        this.frameDuration = duration;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void flip(boolean flip) {
        this.flip = flip;
    }

    public void render(Batch batch, float animateTime) {

        accumulator += animateTime;

        if (accumulator >= frameDuration) {
            frameIndex++;
            if (frameIndex >= frames.length) {
                frameIndex = 0;
            }
            accumulator -= frameDuration;
        }
        batch.draw(frames[frameIndex], x, y,
                size.x/2, size.y/2,
                size.x, size.y,
                flip ? -scale.x : scale.x, scale.y,
                angle);



    }
}
