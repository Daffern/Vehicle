package no.daffern.vehicle.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import net.dermetfan.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daffern on 08.11.2016.
 */
public class Tools {

    private static boolean debugOnly = false;

    public static final void log(Object object, String message){

        String objectString;
        if (object instanceof Class){
            objectString = ((Class) object).getSimpleName();
        }else{
            objectString = object.getClass().getSimpleName();
        }

        if (debugOnly)
            Gdx.app.debug(objectString, message);
        else{
            System.out.println(objectString + ": " + message);
        }
    }

    public static Vector3 mouseToWorldCoordinates(OrthographicCamera camera, int mouseX, int mouseY){
        Vector3 vec = new Vector3(mouseX, mouseY, 0);
        return camera.unproject(vec);
    }

    /**
     * Rotates x and y around originX and originY by angle
     * @param x
     * @param y
     * @param originX
     * @param originY
     * @param radianAngle
     * @return
     */
    public static Vector2 rotatePoint(float x, float y, float originX, float originY, float radianAngle){
        float s = (float)Math.sin(radianAngle);
        float c = (float)Math.cos(radianAngle);

        // translate point back to origin:
        x -= originX;
        y -= originY;

        // rotate point
        float xnew = x * c - y * s;
        float ynew = x * s + y * c;

        // translate point back:
        x = xnew + originX;
        y = ynew + originY;
        return new Vector2(x,y);
    }



    public static <T> List<T> toSingleList(T[][] array){
        List list = new ArrayList<T>();
        if (array == null)
            return list;
        for (T[] ts : array){
            for (T t : ts){
                list.add(t);
            }
        }
        return list;
    }

    public static <T> Array<T> toSingleArray(T[][] array){
        Array list = new Array<T>();
        if (array == null)
            return list;
        for (T[] ts : array){
            for (T t : ts){
                list.add(t);
            }
        }
        return list;
    }
}
