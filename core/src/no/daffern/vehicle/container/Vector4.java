package no.daffern.vehicle.container;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daffern on 16.11.2016.
 */
public class Vector4 {
    public float x1, y1, x2, y2;

    public Vector4(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public boolean isEqual(Vector4 vector4) {
        if ((x2 == vector4.x1 && x1 == vector4.x2) || (x1 == vector4.x1 && x2 == vector4.x2)) {
            if ((y2 == vector4.y1 && y1 == vector4.y2) || (y1 == vector4.y1 && y2 == vector4.y2)) {
                return true;
            }
        }
        return false;
    }

    public boolean followsAxisX(Vector4 vector4) {

        if (y1 == vector4.y1 && y2 == vector4.y2) {

            if (x1 == vector4.x2){
                return true;
            }

        }
        return false;
    }

    public boolean followsAxisY(Vector4 vector4) {

        if (x1 == vector4.x1 && x2 == vector4.x2) {
            if (y1 == vector4.y2) {
                return true;
            }
        }
        return false;
    }

    public Vector4 mergeAxisX(Vector4 vector4) {

        x1 = Math.min(x1, vector4.x1);
        x2 = Math.max(x2, vector4.x2);

        return this;
    }

    public Vector4 mergeAxisY(Vector4 vector4) {

        y1 = Math.min(y1, vector4.y1);
        y2 = Math.max(y2, vector4.y2);

        return this;
    }

    /**
     * tries to merge this line with another line
     * @param vector4
     * @return true if the lines were merged
     */
    public boolean tryMerge(Vector4 vector4){
        return true;
    }



    /**
     * Merges aligned lines
     *
     *
     * @param lines
     * @return
     */


    public static List<Vector4> mergeLines(List<Vector4> lines) {

        List<Vector4> combinedLines = new ArrayList<Vector4>();

        for (int i = 0; i < lines.size(); i++) {

            Vector4 line = lines.get(i);


            combinedLines.add(line);

            for (int j = 0; j < combinedLines.size() -1; j++) {

                Vector4 lines2 = combinedLines.get(j);

                if (line.followsAxisX(lines2)) {
                    lines2.mergeAxisX(line);
                    combinedLines.remove(line);
                }
                else if (line.followsAxisY(lines2)) {
                    lines2.mergeAxisY(line);
                    combinedLines.remove(line);
                }
            }
        }
        return combinedLines;
    }

    public static ArrayList<Vector4> mergeLines2(ArrayList<Vector4> vector4s) {

        ArrayList<Vector4> combinedVector4s = new ArrayList<Vector4>();

        for (int i = 0; i < vector4s.size(); i++) {

            Vector4 vector = vector4s.get(i);


            combinedVector4s.add(vector);

            for (int j = 0; j < combinedVector4s.size() -1; j++) {

                Vector4 vector2 = combinedVector4s.get(j);


            }
        }
        return combinedVector4s;
    }

}













