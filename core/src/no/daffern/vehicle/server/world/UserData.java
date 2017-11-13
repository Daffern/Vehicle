package no.daffern.vehicle.server.world;

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
		public Chunk chunk;
		public UserDataDestructible(Chunk chunk){
			this.chunk = chunk;
			this.type = UserDataType.DESTRUCTIBLE;
		}
	}

	public static class UserDataDrill extends UserData{
		public List<Chunk> recentChunks = new ArrayList<>(2);
		public float[] drillVertices;
		public UserDataDrill(float[] drillVertices){
			this.drillVertices = drillVertices;
			this.type = UserDataType.DRILL;
		}
	}
}
