package no.daffern.vehicle.server.world.terrainGenerator;

/**
 * Created by Daffern on 03.07.2017.
 */
public class MidpointDisplacementTerrainGenerator implements TerrainGenerator {

	private final float heightScale, h;

	public MidpointDisplacementTerrainGenerator(){
		this(1,0.7f);
	}

	public MidpointDisplacementTerrainGenerator(float heightScale, float h){
		this.heightScale = heightScale;
		this.h = h;
	}

	/**
	 *
	 * @param startX
	 * @param startY
	 * @param segmentLength
	 * @param verticeCount gotta be a multiple of 2
	 * @return
	 */
	@Override
	public float[] generateLines(float startX, float startY, float segmentLength, int verticeCount) {

		//create heightpoints
		int subSize = verticeCount;
		int size = verticeCount+1;
		int stride = subSize/2;

		float ratio = (float)Math.pow(2.,-h);
		float scale = heightScale * ratio;

		//generate the y coordinates first
		float[] points = new float[size];
		points[0] = startY;
		points[subSize] = startY + ((float)(Math.random()*10)-5) *scale;


		while (stride != 0){
			for (int i=stride; i<subSize; i+=stride) {
				points[i] = scale * ((float)Math.random()-0.5f) + avgEndpoints (i, stride, points);

				scale *= ratio;

				i+=stride;
			}
			//dafuk
			stride >>= 1;
		}

		//create vertices
		float x = startX;
		float[] vertices = new float[size*2];

		for (int i = 0 ; i < points.length; i++){
			vertices[i*2] = x;
			vertices[i*2+1] = points[i];

			x = x + segmentLength;
		}

		return vertices;
	}

	float avgEndpoints (int i, int stride, float[] vertices)
	{
		return ((vertices[i-stride] + vertices[i+stride]) * .5f);
	}

}
