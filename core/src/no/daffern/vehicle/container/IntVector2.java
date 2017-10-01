package no.daffern.vehicle.container;

/**
 * Created by Daffern on 19.12.2016.
 */
public class IntVector2 {
    public int x, y;
    public IntVector2(){

    }
    public IntVector2(int x, int y){
        this.x = x;
        this.y = y;
    }
    public String toString(){
        return "X: " + x + ", Y: " + y;
    }


    public IntVector2 left(){
    	return new IntVector2(x-1,y);
    }
	public IntVector2 right(){
		return new IntVector2(x+1,y);
	}
	public IntVector2 down(){
		return new IntVector2(x,y-1);
	}
	public IntVector2 up(){
		return new IntVector2(x,y+1);
	}


    @Override
    public boolean equals(Object object){
	    if (this == object)
		    return true;

	    if (object instanceof IntVector2){
		    IntVector2 other = (IntVector2)object;

		    if (x == other.x && y == other.y)
			    return true;

	    }
	    return false;
    }
	@Override
	public int hashCode() {
		return x * 31 + y;
	}





}
