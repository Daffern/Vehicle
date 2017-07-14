package no.daffern.vehicle.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;

import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;


import static com.badlogic.gdx.graphics.g2d.Batch.*;

/**
 * Created by Daffern on 18.11.2016.
 */
public class MyOrthogonalTiledMapRenderer extends OrthogonalTiledMapRenderer {


    public MyOrthogonalTiledMapRenderer(TiledMap map, Batch batch) {
        super(map, batch);

    }

    @Override
    protected void beginRender () {
        AnimatedTiledMapTile.updateAnimationBaseTime();
    }

    @Override
    protected void endRender () {
    }
    @Override
    public void setView (OrthographicCamera camera) {
        float width = camera.viewportWidth * camera.zoom;
        float height = camera.viewportHeight * camera.zoom;
        float w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x);
        float h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);
        viewBounds.set(camera.position.x - w / 2, camera.position.y - h / 2, w, h);
    }

    @Override
    public void renderImageLayer(TiledMapImageLayer layer){
        final Color batchColor = batch.getColor();
        final float color = Color.toFloatBits(batchColor.r,
                batchColor.g,
                batchColor.b,
                batchColor.a * layer.getOpacity());

        final float[] vertices = this.vertices;

        TextureRegion region = layer.getTextureRegion();

        if (region == null) {
            return;
        }

        final float x = layer.getX();
        final float y = layer.getY();
        final float x1 = x * unitScale;
        final float y1 = y * unitScale;
        final float x2 = x1 + region.getRegionWidth() * unitScale;
        final float y2 = y1 + region.getRegionHeight() * unitScale;

        imageBounds.set(x1, y1, x2 - x1, y2 - y1);

        if (viewBounds.contains(imageBounds) || viewBounds.overlaps(imageBounds)) {
            final float u1 = region.getU();
            final float v1 = region.getV2();
            final float u2 = region.getU2();
            final float v2 = region.getV();

            vertices[X1] = x1;
            vertices[Y1] = y1;
            vertices[C1] = color;
            vertices[U1] = u1;
            vertices[V1] = v1;

            vertices[X2] = x1;
            vertices[Y2] = y2;
            vertices[C2] = color;
            vertices[U2] = u1;
            vertices[V2] = v2;

            vertices[X3] = x2;
            vertices[Y3] = y2;
            vertices[C3] = color;
            vertices[U3] = u2;
            vertices[V3] = v2;

            vertices[X4] = x2;
            vertices[Y4] = y1;
            vertices[C4] = color;
            vertices[U4] = u2;
            vertices[V4] = v1;

            batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
        }
    }
/*
    @Override
    public void setView (OrthographicCamera gameCamera) {
        float WALL_WIDTH = gameCamera.viewportWidth * gameCamera.zoom;
        float WALL_HEIGHT = gameCamera.viewportHeight * gameCamera.zoom;
        float w = WALL_WIDTH * Math.abs(gameCamera.up.y) + WALL_HEIGHT * Math.abs(gameCamera.up.x);
        float h = WALL_HEIGHT * Math.abs(gameCamera.up.y) + WALL_WIDTH * Math.abs(gameCamera.up.x);
        viewBounds.set(gameCamera.position.x - w / 2, gameCamera.position.y - h / 2, w, h);

        Tools.log(this, "pos: " + gameCamera.position.toString());
        Tools.log(this, "view " + w + ", " + h);
    }*/
}
