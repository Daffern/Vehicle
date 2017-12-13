package no.daffern.vehicle.utils;

import com.badlogic.gdx.math.MathUtils;
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

		if (v.length <= 6){
			Tools.log(Box2dUtils.class, "Cant remove from triangle");
			return v;
		}
	    for (int i = 3 ; i < v.length ; i+=2){
			float dx = v[i-1] - v[i-3];
			float dy = v[i] - v[i-2];

			if ( (dx*dx + dy*dy) <= minDistance){
				float[] newV = new float[v.length - 2];

				System.arraycopy(v,0,newV,0,i-1);

				if (v.length != i+1)
					System.arraycopy(v,i+1,newV,i-1,newV.length-i+1);

				v = newV;
				i-=2;
			}
		}

		//check first and last
	    float dx = v[0] - v[v.length-2];
	    float dy = v[1] - v[v.length-1];
		if (dx*dx + dy*dy <= minDistance){
			float[] newV = new float[v.length -2];
			System.arraycopy(v,0,newV,0,newV.length -1);
			v = newV;
		}

	    return v;
    }

    public static float[] snapVertices(float[] v, float snap){
    	for (int i = 1 ; i < v.length ; i+=2){
    		float x = v[i-1];
    		float y = v[i];

		    v[i-1] = MathUtils.round(x/snap) * snap;
		    v[i] = MathUtils.round(y/snap) * snap;


	    }
	    return v;
    }

	public static float[] approxCircle(float x, float y, float r, int num_segments){
		float angle = 2 * MathUtils.PI / num_segments;
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float cx = r, cy = 0;
		float[] verts = new float[num_segments*2];
		int ii = 0;
		for (int i = 0; i < num_segments; i++) {
			float temp = cx;
			cx = cos * cx - sin * cy;
			cy = sin * temp + cos * cy;

			verts[ii] = x + cx;
			ii++;
			verts[ii] = y + cy;
			ii++;

		}
		return verts;
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

/*
	b2Assert(b2DistanceSquared(v1, v2) > b2_linearSlop * b2_linearSlop);

	float32 b2DistanceSquared(const b2Vec2& a, const b2Vec2& b)
	{
		b2Vec2 c = a - b;
		return b2Dot(c, c);
	}

	float32 b2Dot(const b2Vec2& a, const b2Vec2& b)
	{
		return a.x * b.x + a.y * b.y;
	}*/
}
