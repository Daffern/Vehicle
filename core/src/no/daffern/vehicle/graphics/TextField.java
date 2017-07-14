package no.daffern.vehicle.graphics;

import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import no.daffern.vehicle.client.ResourceManager;
import no.daffern.vehicle.server.S;

/**
 * Created by Daffern on 05.04.2017.
 */
public class TextField {


    BitmapFont bitmapFont;
    String text = "";


    public TextField(String text) {

        FreetypeFontLoader.FreeTypeFontLoaderParameter parameters = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        parameters.fontFileName = "fonts/Oswald.ttf";
        parameters.fontParameters.size = 10;


        ResourceManager.loadAsset("Oswald.ttf", BitmapFont.class, new ResourceManager.AssetListener<BitmapFont>() {
            @Override
            public void onAssetLoaded(BitmapFont asset) {
                bitmapFont = asset;
            }
        }, parameters);
        this.text = text;
    }


    public void setText(String text) {
        this.text = text;
    }

    public void render(Batch batch, float x, float y) {
        if (bitmapFont != null)
            bitmapFont.draw(batch, text, x, y);

    }

}
