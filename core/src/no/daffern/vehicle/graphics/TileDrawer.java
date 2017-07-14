package no.daffern.vehicle.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daffern on 14.12.2016.
 */
public class TileDrawer {


    float posX, posY;
    float angle;
    float tileWidth, tileHeight;

    int noTile = 0;

    int[][] layout;

    AtlasRegion[][] textureLayout;


    Map<Integer, TileContainer> tileSets = new HashMap<Integer, TileContainer>();

    public TileDrawer(int defaultNoTile, float tileWidth, float tileHeight){
        this.noTile = defaultNoTile;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public void addTileset(int tileSetId, TextureAtlas textureAtlas){
        tileSets.put(tileSetId, new TileContainer(textureAtlas));
        updateTextures();
    }

    public void setLayout(int[][] layout){
        this.layout = layout;

        updateTextures();
    }

    public void updateTextures(){
        textureLayout = new AtlasRegion[layout.length][layout[0].length];

        for (int x = 0; x < layout.length; x++) {
            for (int y = 0; y < layout[x].length; y++) {


                int tileId = layout[x][y];
                int up;
                int down;
                int left;
                int right;

                if (y < layout[x].length - 1)
                    up = layout[x][y + 1];
                else
                    up = noTile;

                if (y > 0)
                    down = layout[x][y - 1];
                else
                    down = noTile;

                if (x < layout.length - 1)
                    right = layout[x + 1][y];
                else
                    right = noTile;

                if (x > 0)
                    left = layout[x - 1][y];
                else
                    left = noTile;


                textureLayout[x][y] = getTexture(tileId, up, down, left, right);
            }
        }
    }


    //center is tileid
    private AtlasRegion getTexture(int tileId, int up, int down, int left, int right) {
        if (tileId == noTile)
            return null;

        TileContainer tileContainer = tileSets.get((int)tileId);
        if (tileContainer == null)
            return null;


        if (up != noTile && down != noTile && left != noTile && right != noTile) {
            return tileContainer.center;
        }

        if (up != noTile && down != noTile && right != noTile && left == noTile) {
            return tileContainer.centerLeft;
        }
        if (up != noTile && down != noTile && right == noTile && left != noTile) {
            return tileContainer.centerRight;
        }

        if (up != noTile && down == noTile && right != noTile && left != noTile) {
            return tileContainer.bottomCenter;
        }
        if (up == noTile && down != noTile && right != noTile && left != noTile) {
            return tileContainer.topCenter;
        }


        if (up == noTile && down != noTile && right != noTile && left == noTile) {
            return tileContainer.topLeft;
        }
        if (up == noTile && down != noTile && right == noTile && left != noTile) {
            return tileContainer.topRight;
        }

        if (up != noTile && down == noTile && right != noTile && left == noTile) {
            return tileContainer.bottomLeft;
        }
        if (up != noTile && down == noTile && right == noTile && left != noTile) {
            return tileContainer.bottomRight;
        }


        return tileContainer.center;
    }

    public void update(float x, float y, float angle){
        this.posX = x;
        this.posY = y;
        this.angle = angle;
    }

    public void render(Batch batch){

        for (int i = 0; i < textureLayout.length; i++) {
            for (int j = 0; j < textureLayout[i].length; j++) {

                if (textureLayout[i][j] != null)
                    batch.draw(textureLayout[i][j],
                            posX + (i * tileWidth), posY + (j * tileHeight),
                            -(i * tileWidth), -(j * tileHeight),
                            textureLayout[i][j].packedWidth, textureLayout[i][j].packedHeight,
                            1, 1, angle);

            }
        }
    }

    private class TileContainer{

        public AtlasRegion topLeft;
        public AtlasRegion topCenter;
        public AtlasRegion topRight;
        public AtlasRegion centerRight;
        public AtlasRegion bottomRight;
        public AtlasRegion bottomCenter;
        public AtlasRegion bottomLeft;
        public AtlasRegion centerLeft;
        public AtlasRegion center;

        public TileContainer(TextureAtlas textureAtlas){
            topLeft = textureAtlas.findRegion("topLeft");
            topCenter = textureAtlas.findRegion("topCenter");
            topRight = textureAtlas.findRegion("topRight");
            centerRight = textureAtlas.findRegion("centerRight");
            bottomRight = textureAtlas.findRegion("bottomRight");
            bottomCenter = textureAtlas.findRegion("bottomCenter");
            bottomLeft = textureAtlas.findRegion("bottomLeft");
            centerLeft = textureAtlas.findRegion("centerLeft");
            center = textureAtlas.findRegion("center");
        }
    }

}
