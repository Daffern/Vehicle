package no.daffern.vehicle.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.FloatArray;
import no.daffern.vehicle.client.ResourceManager;
import no.daffern.vehicle.container.DynamicList;
import no.daffern.vehicle.utils.Tools;

/**
 * Created by Daffern on 07.05.2017.
 */
public class TerrainDrawer {

	TextureRegion surfaceRegion;
	TextureRegion groundRegion;

	DynamicList<Section> sections;

	FloatArray queuedLines;

	float surfaceAspectRatio, groundAspectRatio;

	Color color;

	float surfaceHeight = 20f;
	float groundHeight = 500f;

	boolean initialized = false;

	public TerrainDrawer() {
		sections = new DynamicList(10,5,0);
		queuedLines = new FloatArray();
		color = new Color(1,1,1,1);
	}

	public void initialize(String packName, final String surfaceName, final String groundName) {

		ResourceManager.loadAsset(packName, TextureAtlas.class, new ResourceManager.AssetListener<TextureAtlas>() {
			@Override
			public void onAssetLoaded(TextureAtlas asset) {
				surfaceRegion = asset.findRegion(surfaceName);
				groundRegion = asset.findRegion(groundName);

				surfaceAspectRatio = surfaceRegion.getRegionWidth() / surfaceRegion.getRegionHeight();
				groundAspectRatio = groundRegion.getRegionWidth() / groundRegion.getRegionHeight();

				//release queue
				while (queuedLines.size > 0) {
					addLine(queuedLines.removeIndex(0),
							queuedLines.removeIndex(0),
							queuedLines.removeIndex(0),
							queuedLines.removeIndex(0));
				}

			}
		});


		clear();
		initialized = true;
	}


	public void addLine(float x, float y, float x2, float y2) {
		if (surfaceRegion == null) {//queues up lines if the texture is not yet loaded
			queuedLines.addAll(x, y, x2, y2);
			return;
		}

		float color = this.color.toFloatBits();

		float su = surfaceRegion.getU();
		float sv = surfaceRegion.getV();
		float su2 = surfaceRegion.getU2();
		float sv2 = surfaceRegion.getV2();

		float gu = groundRegion.getU();
		float gv = groundRegion.getV();
		float gu2 = groundRegion.getU2();
		float gv2 = groundRegion.getV2();

		FloatArray surfaceVertices = new FloatArray(20);
		FloatArray groundVertices = new FloatArray(20);//fix capacity


        /*
        1----4 = xy -- x2y2
        |    |
        |    |
        2----3
         */

		//#1
		surfaceVertices.addAll(x, y, color, su, sv); //x

		//#2
		surfaceVertices.addAll(x, y - surfaceHeight, color, su, sv2); //x

		//#3
		surfaceVertices.addAll(x2, y2 - surfaceHeight, color, su2, sv2); //x

		//#4
		surfaceVertices.addAll(x2, y2, color, su2, sv); //x


		y = y - surfaceHeight;
		y2 = y2 - surfaceHeight;

		Color groundColor = new Color(this.color);
		float lastColor = color;

		for (int i = 0; i < groundHeight / 50; i++) {

			groundColor.mul(0.6f,0.6f,0.6f,1f);
			color = groundColor.toFloatBits();

			float height = 50 * i;
			float height2 = 50 * (i + 1);

			groundVertices.addAll(x, y - height, lastColor, gu, gv);//1
			groundVertices.addAll(x, y - height2, color, gu, gv2);//2
			groundVertices.addAll(x2, y2 - height2, color, gu2, gv2);//3
			groundVertices.addAll(x2, y2 - height, lastColor, gu2, gv);//4

			lastColor = color;
		}
		//int sectionIndex = MathUtils.floor(x) ;

		sections.addTail(new Section(surfaceVertices.toArray(), groundVertices.toArray()));

	}

	public void clear() {
		//sections.clear();
	}


	public void draw(Batch batch) {
		if (groundRegion != null && surfaceRegion != null){

			for (int i = sections.getHead(); i <= sections.getTail() ; i++){

				Section section = sections.get(i);

				if (section != null) {

					batch.draw(groundRegion.getTexture(), section.groundVertices, 0, section.groundVertices.length);
					batch.draw(surfaceRegion.getTexture(), section.surfaceVertices, 0, section.surfaceVertices.length);
				}
				else{
					Tools.log(this,"sectio nnull?=?");
				}
			}

		}


	}


	public boolean isInitialized() {
		return initialized;
	}


	private class Section {
		float[] surfaceVertices;
		float[] groundVertices;

		public Section(float[] surfaceVertices, float[] groundVertices) {
			this.surfaceVertices = surfaceVertices;
			this.groundVertices = groundVertices;
		}

		public float getMinX() {
			return surfaceVertices[0];
		}

		public float getMaxX() {
			return surfaceVertices[surfaceVertices.length - 5];
		}

	}
}
