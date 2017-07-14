package no.daffern.vehicle.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Daffern on 26.03.2017.
 */
public class NinePatchRepeated {


    private TextureRegion topLeft;
    private TextureRegion topCenter;
    private TextureRegion topRight;
    private TextureRegion centerLeft;
    private TextureRegion center;
    private TextureRegion centerRight;
    private TextureRegion bottomLeft;
    private TextureRegion bottomRight;
    private TextureRegion bottomCenter;

    private int repeatX = 3, repeatY = 3;

    private float leftWidth, middleWidth, rightWidth;
    private float bottomHeight, middleHeight, topHeight;

    public NinePatchRepeated(Texture texture, int left, int right, int top, int bottom) {


        int middleWidth = texture.getWidth() - left - right;
        int middleHeight = texture.getHeight() - top - bottom;


        topLeft = new TextureRegion(texture, 0, 0, left, top);
        topCenter = new TextureRegion(texture, left, 0, middleWidth, top);
        topRight = new TextureRegion(texture, left + middleWidth, 0, right, top);

        centerLeft = new TextureRegion(texture, 0, top, left, middleHeight);
        center = new TextureRegion(texture, left, top, middleWidth, middleHeight);
        centerRight = new TextureRegion(texture, left + middleWidth, top, right, middleHeight);

        bottomLeft = new TextureRegion(texture, 0, top + middleHeight, left, bottom);
        bottomCenter = new TextureRegion(texture, left, top + middleHeight, middleWidth, bottom);
        bottomRight = new TextureRegion(texture, left + middleWidth, top + middleHeight, right, bottom);

        setStretch(1, 1);
    }

    public void setRepeats(int repeatX, int repeatY) {
        this.repeatX = repeatX;
        this.repeatY = repeatY;
    }

    public void setStretch(float stretchX, float stretchY) {
        leftWidth = bottomLeft.getRegionWidth() * stretchX;
        middleWidth = center.getRegionWidth() * stretchX;
        rightWidth = bottomRight.getRegionWidth() * stretchX;

        bottomHeight = bottomLeft.getRegionHeight() * stretchY;
        middleHeight = centerLeft.getRegionHeight() * stretchY;
        topHeight = topLeft.getRegionHeight() * stretchY;
    }

    public Vector2 getSize(){
        Vector2 size = new Vector2();
        size.x = leftWidth+(repeatX*middleWidth)+rightWidth;
        size.y = bottomHeight + (repeatY*middleHeight) + topHeight;
        return size;
    }

    public float getLeftWidth() {
        return leftWidth;
    }

    public float getMiddleWidth() {
        return middleWidth;
    }

    public float getRightWidth() {
        return rightWidth;
    }

    public float getBottomHeight() {
        return bottomHeight;
    }

    public float getMiddleHeight() {
        return middleHeight;
    }

    public float getTopHeight() {
        return topHeight;
    }


    public void draw(Batch batch, float x, float y) {

        batch.draw(bottomLeft, x, y, leftWidth, bottomHeight);
        for (int i = 0; i < repeatX; i++) {
            batch.draw(bottomCenter, x + leftWidth + (i * middleWidth), y, middleWidth, bottomHeight);
        }
        batch.draw(bottomRight, x + leftWidth + (repeatX * middleWidth), y, rightWidth, bottomHeight);


        for (int i = 0; i < repeatX; i++) {
            for (int j = 0; j < repeatY; j++) {
                batch.draw(center, x + leftWidth + (i * middleWidth), y + bottomHeight + (j * middleHeight), middleWidth, middleHeight);
            }
        }

        for (int i = 0; i < repeatY; i++) {
            batch.draw(centerLeft, x, y + bottomHeight + (i * middleHeight), leftWidth, middleHeight);
            batch.draw(centerRight, x + leftWidth + (repeatX * middleWidth), y + bottomHeight + (i * middleHeight), rightWidth, middleHeight);
        }


        batch.draw(topLeft, x, y + bottomHeight + (repeatY * middleHeight), leftWidth, topHeight);
        for (int i = 0; i < repeatX; i++) {
            batch.draw(topCenter, x + leftWidth + (i * middleWidth), y + bottomHeight + (repeatY * middleHeight), middleWidth, topHeight);
        }

        batch.draw(topRight, x + leftWidth + (repeatX * middleWidth), y + bottomHeight + (repeatY * middleHeight), rightWidth, topHeight);


    }
}
