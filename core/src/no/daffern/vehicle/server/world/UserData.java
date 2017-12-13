package no.daffern.vehicle.server.world;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.seisw.util.geom.Poly;
import no.daffern.vehicle.server.world.destructible.Chunk;

import java.util.ArrayList;
import java.util.List;

public class UserData {
	public enum UserDataType{
		DESTRUCTIBLE,
		DRILL
	}
	public UserDataType type;

	public static class UserDataDestructible extends UserData{
		public Chunk chunk;//chunk this fixture belongs to
		public Poly poly;//poly corresponding to the fixture
		public UserDataDestructible(Chunk chunk, Poly poly){
			this.chunk = chunk;
			this.poly = poly;
			this.type = UserDataType.DESTRUCTIBLE;
		}
	}

	public static class UserDataDrill extends UserData{
		public List<Fixture> fixtures = new ArrayList<>(5);
		public UserDataDrill(){
			this.type = UserDataType.DRILL;
		}
	}
}
