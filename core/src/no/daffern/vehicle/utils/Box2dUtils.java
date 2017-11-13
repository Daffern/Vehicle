package no.daffern.vehicle.utils;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Transform;

/**
 * Created by Daffern on 14.05.2017.
 */
public class Box2dUtils {

    public static boolean shouldCollide(Fixture fixture1, Fixture fixture2) {
        return (fixture1.getFilterData().maskBits & fixture2.getFilterData().categoryBits) != 0 &&
                (fixture1.getFilterData().categoryBits & fixture2.getFilterData().maskBits) != 0;
    }

	/**
	 * Neat for transforming the vertices of a fixture by the transform of its body
	 * @param transform
	 * @param vertices
	 * @return
	 */

	public static float[] transformVertices(Transform transform, float[] vertices){

		float[] v = new float[vertices.length];
		System.arraycopy(vertices,0,v,0,vertices.length);

    	float tx = transform.vals[Transform.POS_X];
		float ty = transform.vals[Transform.POS_Y];
		float tcos = transform.vals[Transform.COS];
		float tsin = transform.vals[Transform.SIN];

    	for (int i = 0 ; i < v.length; i+=2){

    		float x = v[i];
		    float y = v[i+1];

		    v[i]    = tcos * x - tsin * y + tx;
		    v[i+1]  = tsin * x + tcos * y + ty;

	    }

	    return v;
    }

    public static float[] removeCloseVertices(float[] v, float minDistance){

		for (int i = 3 ; i < v.length ; i+=2){
			float x1 = v[i-3];
			float y1 = v[i-2];
			float x2 = v[i-1];
			float y2 = v[i];

			float dx = x2 - x1;
			float dy = y2 - y1;

			if ( (dx*dx + dy*dy) <= minDistance){
				float[] newV = new float[v.length - 2];

				System.arraycopy(v,0,newV,0,i-1);
				if (v.length != i+1) System.arraycopy(v,i+1,newV,i-1,newV.length-i+1);

				v = newV;
				i-=2;
			}


		}
		return v;
    }

	/**
	 * returns 8 vertices (4 points)
	 * @param v
	 * @return
	 */
	public static AABB findAABB(float[] v){

	    //find aabb
		AABB aabb = new AABB();
	    aabb.minX = Float.MAX_VALUE;
		aabb.maxX = -Float.MAX_VALUE;
		aabb.minY = Float.MAX_VALUE;
		aabb.maxY = -Float.MAX_VALUE;
	    for (int i = 0 ; i < v.length ;){
		    float x = v[i++];
		    float y = v[i++];

		    if (x > aabb.maxX)
			    aabb.maxX = x;
		    if (x < aabb.minX)
			    aabb.minX = x;
		    if (y > aabb.maxY)
			    aabb.maxY = y;
		    if (y < aabb.minY)
			    aabb.minY = y;
	    }

	    return aabb;
    }
    public static class AABB{
		public float minX, minY, maxX, maxY;
    }
}
