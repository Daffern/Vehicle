package no.daffern.vehicle.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import no.daffern.vehicle.network.packets.GameItemPacket;

/**
 * Created by Daffern on 09.06.2017.
 */
public class GameItemLoader {

    //json type names
    private final static String NAME = "name";
    private final static String TYPE = "type";
    private final static String FILE = "file";
    private final static String FILETYPE = "filetype";
    private final static String REGION = "region";
    private final static String INFINITE = "infinite";


    //item types
    private final static String WHEEL = "wheel";
    private final static String ERASER = "eraser";
    private final static String SQUARE_WALL = "square_wall";
    private final static String TRIANGLE_WALL = "triangle_wall";


    //filetypes
    private final static String ATLAS = "atlas";
    private final static String TEXTURE = "texture";



    JsonReader jsonReader;



    public GameItemPacket[] loadFile(String json){
        jsonReader = new JsonReader();
        JsonValue root = jsonReader.parse(Gdx.files.internal(json));

        GameItemPacket[] packets = new GameItemPacket[root.size];

        for (int i = 0 ; i < root.size ;i++){

            JsonValue element = root.get(i);

            GameItemPacket item = new GameItemPacket();

            try {

                item.name = element.getString(NAME);

                item.packName = element.getString(FILE);
                if (element.getString(FILETYPE).equals(ATLAS))
                    item.iconName = element.getString(REGION);

                item.itemId = i;
                item.type = getItemType(element.getString(TYPE));
                item.isInfinite = element.getBoolean(INFINITE);


            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
            }finally {
                packets[i] = new GameItemPacket();
            }
        }
        return packets;
    }

    private int getItemType(String s){
        switch (s){
            case WHEEL: return GameItemTypes.PART_TYPE_WHEEL;
            case ERASER: return GameItemTypes.REMOVE_TOOL;
            case SQUARE_WALL: return GameItemTypes.WALL_TYPE_SQUARE;
            case TRIANGLE_WALL: return GameItemTypes.WALL_TYPE_TRIANGLE;

            default: return -1;
        }
    }
}
