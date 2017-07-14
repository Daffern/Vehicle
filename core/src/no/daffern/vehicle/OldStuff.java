package no.daffern.vehicle;

/**
 * Created by Daffern on 16.11.2016.
 */
public class OldStuff {
    /*public Vector2[] createBox(Vector4 line) {
        Vector2[] box = new Vector2[4];

        float boxHalfBredth = 2f;

        if (Math.abs(line.x1 - line.x2) > Math.abs(line.y1 - line.y2)) {

            box[0] = new Vector2(line.x1, line.y1 - boxHalfBredth);
            box[1] = new Vector2(line.x1, line.y1 + boxHalfBredth);

            box[2] = new Vector2(line.x2, line.y2 + boxHalfBredth);
            box[3] = new Vector2(line.x2, line.y2 - boxHalfBredth);
        } else {
            box[0] = new Vector2(line.x1 - boxHalfBredth, line.y1);
            box[1] = new Vector2(line.x1 + boxHalfBredth, line.y1);

            box[2] = new Vector2(line.x2 + boxHalfBredth, line.y2);
            box[3] = new Vector2(line.x2 - boxHalfBredth, line.y2);
        }

        return box;
    }

    private Vector2[] sortByAngle(Vector2[] vertices) {

        float avgX = 0, avgY = 0;

        for (Vector2 vertice : vertices) {
            avgX = avgX + vertice.x;
            avgY = avgY + vertice.y;
        }

        avgX = avgX / vertices.length;
        avgY = avgY / vertices.length;

        boolean swapped = false;

        while (!swapped) {

            swapped = false;

            for (int i = 1; i < vertices.length; i++) {

                double angle = Math.atan2(vertices[i - 1].x - avgX, vertices[i - 1].y - avgY);
                double angle2 = Math.atan2(vertices[i].x - avgX, vertices[i].y - avgY);

                if (angle >= angle2) {
                    Vector2 tempVertices = vertices[i];
                    vertices[i] = vertices[i - 1];
                    vertices[i - 1] = tempVertices;

                    swapped = true;
                }

            }

        }
        return vertices;
    }*/
}
