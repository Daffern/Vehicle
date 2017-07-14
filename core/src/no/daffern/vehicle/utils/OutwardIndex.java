package no.daffern.vehicle.utils;

/**
 * Created by Daffern on 06.06.2017.
 *
 * Generates indexes from start to end
 */

public class OutwardIndex {

    int i;
    int length;
    int middle;

    public OutwardIndex(int startIndex, int endIndex){
        this.i = -1;
        this.length = endIndex - startIndex;
        this.middle = (startIndex + endIndex)/2;
    }

    public boolean hasNext(){
        if (i == length)
            return false;
        else
            return true;
    }

    public int getNext(){
        i++;
        if (i % 2 == 0){
            return middle + (int)Math.ceil(i / 2d);
        }
        else {
            return middle - ( i / 2 );
        }
    }


}
