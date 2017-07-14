package no.daffern.vehicle.server.world.terrainGenerator;

public class SimplexNoiseTerrainGenerator implements TerrainGenerator {
	/**
	 * Constructor of to initialize a fractal noise summation
	 *
	 * @param[in] frequency    Frequency ("width") of the first octave of noise (default to 1.0)
	 * @param[in] amplitude    Amplitude ("height") of the first octave of noise (default to 1.0)
	 * @param[in] lacunarity   Lacunarity specifies the frequency multiplier between successive octaves (default to 2.0).
	 * @param[in] persistence  Persistence is the loss of amplitude between successive octaves (usually 1/lacunarity)
	 */
	public SimplexNoiseTerrainGenerator(float frequency, float amplitude, float lacunarity) {
		this(frequency, amplitude, lacunarity, 0.5f);
	}

	public SimplexNoiseTerrainGenerator(float frequency, float amplitude) {
		this(frequency, amplitude, 2.0f, 0.5f);
	}

	public SimplexNoiseTerrainGenerator(float frequency) {
		this(frequency, 1.0f, 2.0f, 0.5f);
	}

	public SimplexNoiseTerrainGenerator() {
		this(1.0f, 1.0f, 2.0f, 0.5f);
	}

	//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above:
	//ORIGINAL LINE: explicit SimplexNoise(float frequency = 1.0f, float amplitude = 1.0f, float lacunarity = 2.0f, float persistence = 0.5f) : mFrequency(frequency), mAmplitude(amplitude), mLacunarity(lacunarity), mPersistence(persistence)
	public SimplexNoiseTerrainGenerator(float frequency, float amplitude, float lacunarity, float persistence) {
		this.mFrequency = frequency;
		this.mAmplitude = amplitude;
		this.mLacunarity = lacunarity;
		this.mPersistence = persistence;
	}

	// Parameters of Fractional Brownian Motion (fBm) : sum of N "octaves" of noise
	private float mFrequency; ///< Frequency ("width") of the first octave of noise (default to 1.0)
	private float mAmplitude; ///< Amplitude ("height") of the first octave of noise (default to 1.0)
	private float mLacunarity; ///< Lacunarity specifies the frequency multiplier between successive octaves (default to 2.0).
	private float mPersistence; ///< Persistence is the loss of amplitude between successive octaves (usually 1/lacunarity)

	@Override
	public float[] generateLines(float startX, float startY, float segmentLength, int segmentsNum) {

		float[] vertices = new float[segmentsNum];

		float x = startX;
		float y = startY;

		for (int i = 0; i < segmentsNum; i++) {
			vertices[i] = x;
			i++;
			vertices[i] = y;

			x = x + segmentLength;
			y = y + noise(x);

		}

		return vertices;
	}

	public static float noise(float x) {
		float n0; // Noise contributions from the two "corners"
		float n1;

		// No need to skew the input space in 1D

		// Corners coordinates (nearest integer values):
		int i0 = fastfloor(x);
		int i1 = i0 + 1;
		// Distances to corners (between 0 and 1):
		float x0 = x - i0;
		float x1 = x0 - 1.0f;

		// Calculate the contribution from the first corner
		float t0 = 1.0f - x0 * x0;
		//  if(t0 < 0.0f) t0 = 0.0f; // not possible
		t0 *= t0;
		n0 = t0 * t0 * grad(hash(i0), x0);

		// Calculate the contribution from the second corner
		float t1 = 1.0f - x1 * x1;
		//  if(t1 < 0.0f) t1 = 0.0f; // not possible
		t1 *= t1;
		n1 = t1 * t1 * grad(hash(i1), x1);

		// The maximum value of this noise is 8*(3/4)^4 = 2.53125
		// A factor of 0.395 scales to fit exactly within [-1,1]
		return 0.395f * (n0 + n1);
	}
	// 2D Perlin simplex noise

	/**
	 * 2D Perlin simplex noise
	 * <p>
	 * Takes around 150ns on an AMD APU.
	 *
	 * @return Noise value in the range[-1; 1], value of 0 on all integer coordinates.
	 * @param[in] x float coordinate
	 * @param[in] y float coordinate
	 */
	public static float noise(float x, float y) {
		float n0; // Noise contributions from the three corners
		float n1;
		float n2;

		// Skewing/Unskewing factors for 2D
		final float F2 = 0.366025403f; // F2 = (sqrt(3) - 1) / 2
		final float G2 = 0.211324865f; // G2 = (3 - sqrt(3)) / 6   = F2 / (1 + 2 * K)

		// Skew the input space to determine which simplex cell we're in
		float s = (x + y) * F2; // Hairy factor for 2D
		float xs = x + s;
		float ys = y + s;
		int i = fastfloor(xs);
		int j = fastfloor(ys);

		// Unskew the cell origin back to (x,y) space
		float t = (float) (i + j) * G2;
		float X0 = i - t;
		float Y0 = j - t;
		float x0 = x - X0; // The x,y distances from the cell origin
		float y0 = y - Y0;

		// For the 2D case, the simplex shape is an equilateral triangle.
		// Determine which simplex we are in.
		int i1; // Offsets for second (middle) corner of simplex in (i,j) coords
		int j1;
		if (x0 > y0) { // lower triangle, XY order: (0,0)->(1,0)->(1,1)
			i1 = 1;
			j1 = 0;
		}
		else { // upper triangle, YX order: (0,0)->(0,1)->(1,1)
			i1 = 0;
			j1 = 1;
		}

		// A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
		// a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
		// c = (3-sqrt(3))/6

		float x1 = x0 - i1 + G2; // Offsets for middle corner in (x,y) unskewed coords
		float y1 = y0 - j1 + G2;
		float x2 = x0 - 1.0f + 2.0f * G2; // Offsets for last corner in (x,y) unskewed coords
		float y2 = y0 - 1.0f + 2.0f * G2;

		// Calculate the contribution from the first corner
		float t0 = 0.5f - x0 * x0 - y0 * y0;
		if (t0 < 0.0f) {
			n0 = 0.0f;
		}
		else {
			t0 *= t0;
			n0 = t0 * t0 * grad(hash(i + hash(j)), x0, y0);
		}

		// Calculate the contribution from the second corner
		float t1 = 0.5f - x1 * x1 - y1 * y1;
		if (t1 < 0.0f) {
			n1 = 0.0f;
		}
		else {
			t1 *= t1;
			n1 = t1 * t1 * grad(hash(i + i1 + hash(j + j1)), x1, y1);
		}

		// Calculate the contribution from the third corner
		float t2 = 0.5f - x2 * x2 - y2 * y2;
		if (t2 < 0.0f) {
			n2 = 0.0f;
		}
		else {
			t2 *= t2;
			n2 = t2 * t2 * grad(hash(i + 1 + hash(j + 1)), x2, y2);
		}

		// Add contributions from each corner to get the final noise value.
		// The result is scaled to return values in the interval [-1,1].
		return 45.23065f * (n0 + n1 + n2);
	}


	public final float fractal(int octaves, float x) {
		float output = 0.0f;
		float denom = 0.0f;
		float frequency = mFrequency;
		float amplitude = mAmplitude;

		for (int i = 0; i < octaves; i++) {
			output += (amplitude * noise(x * frequency));
			denom += amplitude;

			frequency *= mLacunarity;
			amplitude *= mPersistence;
		}

		return (output / denom);
	}

	/**
	 * Fractal/Fractional Brownian Motion (fBm) summation of 2D Perlin Simplex noise
	 *
	 * @return Noise value in the range[-1; 1], value of 0 on all integer coordinates.
	 * @param[in] octaves   number of fraction of noise to sum
	 * @param[in] x         x float coordinate
	 * @param[in] y         y float coordinate
	 */
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: float fractal(int octaves, float x, float y) const
	public final float fractal(int octaves, float x, float y) {
		float output = 0.0f;
		float denom = 0.0f;
		float frequency = mFrequency;
		float amplitude = mAmplitude;

		for (int i = 0; i < octaves; i++) {
			output += (amplitude * noise(x * frequency, y * frequency));
			denom += amplitude;

			frequency *= mLacunarity;
			amplitude *= mPersistence;
		}

		return (output / denom);
	}


	/**
	 * Computes the largest integer value not greater than the float one
	 * <p>
	 * This method is faster than using (int32_t)std::floor(fp).
	 * <p>
	 * I measured it to be approximately twice as fast:
	 * float:  ~18.4ns instead of ~39.6ns on an AMD APU),
	 * double: ~20.6ns instead of ~36.6ns on an AMD APU),
	 * Reference: http://www.codeproject.com/Tips/700780/Fast-floor-ceiling-functions
	 *
	 * @return largest integer value not greater than fp
	 * @param[in] fp    float input value
	 */
	public static int fastfloor(float fp) {
		int i = (int) fp;
		return (fp < i) ? (i - 1) : (i);
	}

	/**
	 * Permutation table. This is just a random jumble of all numbers 0-255.
	 * <p>
	 * This produce a repeatable pattern of 256, but Ken Perlin stated
	 * that it is not a problem for graphic texture as the noise features disappear
	 * at a distance far enough to be able to see a repeatable pattern of 256.
	 * <p>
	 * This needs to be exactly the same for all instances on all platforms,
	 * so it's easiest to just keep it as static explicit data.
	 * This also removes the need for any initialisation of this class.
	 * <p>
	 * Note that making this an uint32_t[] instead of a uint8_t[] might make the
	 * code run faster on platforms with a high penalty for unaligned single
	 * byte addressing. Intel x86 is generally single-byte-friendly, but
	 * some other CPUs are faster with 4-aligned reads.
	 * However, a char[] is smaller, which avoids cache trashing, and that
	 * is probably the most important aspect on most architectures.
	 * This array is accessed a *lot* by the noise functions.
	 * A vector-valued noise over 3D accesses it 96 times, and a
	 * float-valued 4D noise 64 times. We want this to fit in the cache!
	 */
	public static final byte[] perm = {(byte) 151, (byte) 160, (byte) 137, 91, 90, 15, (byte) 131, 13, (byte) 201, 95, 96, 53, (byte) 194, (byte) 233, 7, (byte) 225, (byte) 140, 36, 103, 30, 69, (byte) 142, 8, 99, 37, (byte) 240, 21, 10, 23, (byte) 190, 6, (byte) 148, (byte) 247, 120, (byte) 234, 75, 0, 26, (byte) 197, 62, 94, (byte) 252, (byte) 219, (byte) 203, 117, 35, 11, 32, 57, (byte) 177, 33, 88, (byte) 237, (byte) 149, 56, 87, (byte) 174, 20, 125, (byte) 136, (byte) 171, (byte) 168, 68, (byte) 175, 74, (byte) 165, 71, (byte) 134, (byte) 139, 48, 27, (byte) 166, 77, (byte) 146, (byte) 158, (byte) 231, 83, 111, (byte) 229, 122, 60, (byte) 211, (byte) 133, (byte) 230, (byte) 220, 105, 92, 41, 55, 46, (byte) 245, 40, (byte) 244, 102, (byte) 143, 54, 65, 25, 63, (byte) 161, 1, (byte) 216, 80, 73, (byte) 209, 76, (byte) 132, (byte) 187, (byte) 208, 89, 18, (byte) 169, (byte) 200, (byte) 196, (byte) 135, (byte) 130, 116, (byte) 188, (byte) 159, 86, (byte) 164, 100, 109, (byte) 198, (byte) 173, (byte) 186, 3, 64, 52, (byte) 217, (byte) 226, (byte) 250, 124, 123, 5, (byte) 202, 38, (byte) 147, 118, 126, (byte) 255, 82, 85, (byte) 212, (byte) 207, (byte) 206, 59, (byte) 227, 47, 16, 58, 17, (byte) 182, (byte) 189, 28, 42, (byte) 223, (byte) 183, (byte) 170, (byte) 213, 119, (byte) 248, (byte) 152, 2, 44, (byte) 154, (byte) 163, 70, (byte) 221, (byte) 153, 101, (byte) 155, (byte) 167, 43, (byte) 172, 9, (byte) 129, 22, 39, (byte) 253, 19, 98, 108, 110, 79, 113, (byte) 224, (byte) 232, (byte) 178, (byte) 185, 112, 104, (byte) 218, (byte) 246, 97, (byte) 228, (byte) 251, 34, (byte) 242, (byte) 193, (byte) 238, (byte) 210, (byte) 144, 12, (byte) 191, (byte) 179, (byte) 162, (byte) 241, 81, 51, (byte) 145, (byte) 235, (byte) 249, 14, (byte) 239, 107, 49, (byte) 192, (byte) 214, 31, (byte) 181, (byte) 199, 106, (byte) 157, (byte) 184, 84, (byte) 204, (byte) 176, 115, 121, 50, 45, 127, 4, (byte) 150, (byte) 254, (byte) 138, (byte) 236, (byte) 205, 93, (byte) 222, 114, 67, 29, 24, 72, (byte) 243, (byte) 141, (byte) 128, (byte) 195, 78, 66, (byte) 215, 61, (byte) 156, (byte) 180};

	/**
	 * Helper function to hash an integer using the above permutation table
	 * <p>
	 * This inline function costs around 1ns, and is called N+1 times for a noise of N dimension.
	 * <p>
	 * Using a real hash function would be better to improve the "repeatability of 256" of the above permutation table,
	 * but fast integer Hash functions uses more time and have bad random properties.
	 *
	 * @return 8-bits hashed value
	 * @param[in] i Integer value to hash
	 */
	public static byte hash(int i) {
		return perm[(byte) i];
	}

	/* NOTE Gradient table to test if lookup-table are more efficient than calculs
	static const float gradients1D[16] = {
	        -8.f, -7.f, -6.f, -5.f, -4.f, -3.f, -2.f, -1.f,
	         1.f,  2.f,  3.f,  4.f,  5.f,  6.f,  7.f,  8.f
	};
	*/

	/**
	 * Helper function to compute gradients-dot-residual vectors (1D)
	 *
	 * @return gradient value
	 * @note that these generate gradients of more than unit length. To make
	 * a close match with the value range of classic Perlin noise, the final
	 * noise values need to be rescaled to fit nicely within [-1,1].
	 * (The simplex noise functions as such also have different scaling.)
	 * Note also that these noise functions are the most practical and useful
	 * signed version of Perlin noise.
	 * @param[in] hash  hash value
	 * @param[in] x     distance to the corner
	 */
	public static float grad(int hash, float x) {
		int h = hash & 0x0F; // Convert low 4 bits of hash code
		float grad = 1.0f + (h & 7); // Gradient value 1.0, 2.0, ..., 8.0
		if ((h & 8) != 0) {
			grad = -grad; // Set a random sign for the gradient
		}
		//  float grad = gradients1D[h];    // NOTE : Test of Gradient look-up table instead of the above
		return (grad * x); // Multiply the gradient with the distance
	}

	/**
	 * Helper functions to compute gradients-dot-residual vectors (2D)
	 *
	 * @return gradient value
	 * @param[in] hash  hash value
	 * @param[in] x     x coord of the distance to the corner
	 * @param[in] y     y coord of the distance to the corner
	 */
	public static float grad(int hash, float x, float y) {
		int h = hash & 0x3F; // Convert low 3 bits of hash code
		float u = h < 4 ? x : y; // into 8 simple gradient directions,
		float v = h < 4 ? y : x; // and compute the dot product with (x,y).
		return (((h & 1) != 0) ? -u : u) + (((h & 2) != 0) ? -2.0f * v : 2.0f * v);
	}

}