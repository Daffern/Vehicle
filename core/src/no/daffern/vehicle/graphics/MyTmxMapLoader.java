package no.daffern.vehicle.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.XmlReader;

/**
 * Created by Daffern on 24.03.2017.
 */
public class MyTmxMapLoader extends TmxMapLoader {

    @Override
    protected void loadImageLayer (TiledMap map, XmlReader.Element element, FileHandle tmxFile, ImageResolver imageResolver) {
        if (element.getName().equals("imagelayer")) {

            String sx,sy;

            try {
                sx = element.getAttribute("x");
            }catch (GdxRuntimeException e){
                sx = element.getAttribute("offsetx","0");
            }
            try{
                sy = element.getAttribute("y");
            }catch (GdxRuntimeException e){
                sy = element.getAttribute("offsety","0");
            }

            int x = (int)Float.parseFloat(sx);
            int y = (int)Float.parseFloat(sy);

            if (flipY) y = mapHeightInPixels - y;

            TextureRegion texture = null;

            XmlReader.Element image = element.getChildByName("image");

            if (image != null) {
                String source = image.getAttribute("source");
                FileHandle handle = getRelativeFileHandle(tmxFile, source);
                texture = imageResolver.getImage(handle.path());
                y -= texture.getRegionHeight();
            }

            TiledMapImageLayer layer = new TiledMapImageLayer(texture, x, y);

            loadBasicLayerInfo(layer, element);

            XmlReader.Element properties = element.getChildByName("properties");
            if (properties != null) {
                loadProperties(layer.getProperties(), properties);
            }

            map.getLayers().add(layer);
        }

    }
}
