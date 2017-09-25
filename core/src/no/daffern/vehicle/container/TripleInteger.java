package no.daffern.vehicle.container;

/**
 * Created by Daffern on 26.04.2017.
 */
public class TripleInteger {
    int int1, int2, int3;
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TripleInteger other = (TripleInteger) o;

        if (int1 != other.int1) return false;
        if (int2 != other.int2) return false;
	    return int3 == other.int3;
    }

    @Override
    public int hashCode()
    {
        int result = (int1 ^ (int1 >>> 32));
        result = 31 * result + (int2 ^ (int2 >>> 32));
        result = 31 * result + (int3 ^ (int3 >>> 32));
        return result;
    }
    public static int getHashCode(int int1, int int2, int int3){
        int result = (int1 ^ (int1 >>> 32));
        result = 31 * result + (int2 ^ (int2 >>> 32));
        result = 31 * result + (int3 ^ (int3 >>> 32));
        return result;
    }
}
