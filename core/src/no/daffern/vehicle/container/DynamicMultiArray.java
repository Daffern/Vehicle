package no.daffern.vehicle.container;


import java.util.Vector;

/**
 * Created by Daffern on 06.06.2017.
 *
 * Two dimensional array which can be indexed by positive and negative values
 */
public class DynamicMultiArray<T> {


    private Object[][] array;

    private int sizeX, sizeY;
    private int offsetX = 0, offsetY = 0;
    private int startX=0, startY=0, endX=0, endY=0;

    public DynamicMultiArray(){
        this(1,1);
    }
    public DynamicMultiArray(int initialCapacityX, int initialCapacityY) {
        this(initialCapacityX,initialCapacityY,0,0);
    }

    public DynamicMultiArray(int initialCapacityX, int initialCapacityY, int offsetX, int offsetY){
        this.array = new Object[initialCapacityX][initialCapacityY];
        this.sizeX = initialCapacityX;
        this.sizeY = initialCapacityY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public T set(int x, int y, T object){
        int i = x + offsetX;
        int j = y + offsetY;

        //resize arrays
        if (i < 0){

            sizeX -= i;
            offsetX -= i;

            Object[][] newArray = new Object[sizeX][sizeY];

            System.arraycopy(array,0,newArray,-i,array.length);

            array = newArray;

            i = 0;

        }
        else if (i >= sizeX){

            sizeX = i + 1;

            Object[][] newArray = new Object[sizeX][sizeY];

            System.arraycopy(array,0,newArray,0,array.length);

            array = newArray;

        }
        if (j < 0){

            sizeY -= j;
            offsetY -= j;

            Object[][] newArray = new Object[sizeX][sizeY];

            for (int k = 0 ; k < sizeX ; k++){
                System.arraycopy(array[k],0,newArray[k],-j,array[k].length);
            }

            array = newArray;

            j = 0;
        }
        else if (j >= sizeY){

            sizeY = j + 1;

            Object[][] newArray = new Object[sizeX][sizeY];

            for (int k = 0 ; k < sizeX ; k++){
                System.arraycopy(array[k],0,newArray[k],0,array[k].length);
            }

            array = newArray;
        }

        //update start/end
        if (x < startX)
        	startX = x;
        else if (x > endX)
        	endX = x;

        if (y < startY)
        	startY = y;
        else if (y > endY)
	        endY = y;

        array[i][j] = object;

        return object;
    }

    public T get(int x, int y) {
        int i = x + offsetX;
        int j = y + offsetY;
        if (withinBounds(i, j))
            return (T) array[i][j];
        else
            return null;
    }

    public T remove(int x, int y){
        int i = x + offsetX;
        int j = y + offsetY;

        if (withinBounds(i,j)){
            T object = (T)array[i][j];
            array[i][j] = null;
            return object;
        }
        else return null;
    }

    public String toString(int x, int y){
        int i = x + offsetX;
        int j = y + offsetY;
        if (withinBounds(i,j)) {
            return "X: " + x + ", Y: " + y + " is: " + ((array[i][j] == null) ? "null" : array[i][j].toString());
        }
        else return "X: " + x + " Y: " + y + " is out of bounds";
    }

    private boolean withinBounds(int i, int j) {
        if (i < 0 || j < 0 || i >= sizeX || j >= sizeY)
            return false;
        return true;
    }

    public int getSizeX() {
        return sizeX;
    }
    public int getSizeY() {
        return sizeY;
    }
    public int getOffsetX() {
        return offsetX;
    }
    public int getOffsetY() {
        return offsetY;
    }
    public int startX(){
        return startX;
    }
    public int startY(){
        return startY;
    }
    public int endX(){
        return endX;
    }
    public int endY(){
        return endY;
    }
}
