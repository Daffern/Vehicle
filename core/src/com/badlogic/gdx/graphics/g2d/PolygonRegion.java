/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.graphics.g2d;

/** Defines a polygon shape on top of a texture region to avoid drawing transparent pixels.
 * @see PolygonRegionLoader
 * @author Stefan Bachmann
 * @author Nathan Sweet */
public class PolygonRegion {
	final float[] textureCoords; // texture coordinates in atlas coordinates
	final float[] vertices; // pixel coordinates relative to source image.
	final short[] triangles;
	final TextureRegion region;

	/**
	 * Modified for the ChunkDrawer, precalculates uvs (Use with SpriteBatch.draw(PolygonRegion, float, float))
	 * @param region
	 * @param vertices
	 * @param triangles
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public PolygonRegion (TextureRegion region, float[] vertices, short[] triangles, float x, float y, float width, float height) {
		this.region = region;
		this.vertices = vertices;
		this.triangles = triangles;

		float[] textureCoords = this.textureCoords = new float[vertices.length];
		float u = region.u, v = region.v;
		float uvWidth = region.u2 - u;
		float uvHeight = region.v2 - v;



		for (int i = 0, n = vertices.length; i < n; i++) {
			textureCoords[i] = u + uvWidth * ((vertices[i]-x) / width);
			i++;
			textureCoords[i] = v + uvHeight * (1 - ((vertices[i]-y) / height));
		}
	}

	/** Returns the vertices in local space. */
	public float[] getVertices () {
		return vertices;
	}

	public short[] getTriangles () {
		return triangles;
	}

	public float[] getTextureCoords () {
		return textureCoords;
	}

	public TextureRegion getRegion () {
		return region;
	}
}
