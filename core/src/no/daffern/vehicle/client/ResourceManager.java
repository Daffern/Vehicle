package no.daffern.vehicle.client;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Daffern on 22.11.2016.
 */
public class ResourceManager {


    private static Map<String, List<AssetListener>> listeners = new ConcurrentHashMap<String, List<AssetListener>>();

    private static AssetManager assetManager = new AssetManager();

    static {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

    }


    public static synchronized void loadAsset(String fileName, Class c, AssetListener assetListener) {

        if (assetManager.isLoaded(fileName, c)){
            Object object = assetManager.get(fileName, c);
            assetListener.onAssetLoaded(object);
        }
        else{
            assetManager.load(fileName, c);
            List<AssetListener> list = listeners.get(fileName);

            if (list == null){
                list = new ArrayList<AssetListener>();
            }
            list.add(assetListener);

            listeners.put(fileName, list);
        }
    }

    //TODO parameters will be ignored if the asset is loaded a second time
    public static synchronized void loadAsset(String fileName, Class c, AssetListener assetListener, AssetLoaderParameters parameters) {

        if (assetManager.isLoaded(fileName, c)){
            Object object = assetManager.get(fileName, c);
            assetListener.onAssetLoaded(object);
        }
        else{
            assetManager.load(fileName, c, parameters);

            List<AssetListener> list = listeners.get(fileName);

            if (list == null){
                list = new ArrayList<AssetListener>();
            }
            list.add(assetListener);

            listeners.put(fileName, list);
        }
    }


    public static void update() {

        assetManager.update();

        for (Map.Entry<String, List<AssetListener>> entry : listeners.entrySet()) {

            if (assetManager.isLoaded(entry.getKey())) {

                Object asset = assetManager.get(entry.getKey());

                for (AssetListener assetListener : entry.getValue()){
                    assetListener.onAssetLoaded(asset);
                }

                listeners.remove(entry.getKey());
                break;
            }
        }

    }

    public static abstract class AssetListener<T> {
        public abstract void onAssetLoaded(T asset);
    }
}
