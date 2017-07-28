package no.daffern.vehicle.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Daffern on 30.06.2017.
 */
public class OffsetList<T> {

	private Object[] objects;
	private int capacityIncrement;
	private int offset;
	private int head, tail;//first and last index
	private boolean firstAdded = false;

	public OffsetList(){
		this(1,1,0);
	}

	public OffsetList(int initialCapacity, int capacityIncrement, int initialOffset) {
		this.objects = new Object[initialCapacity];
		this.capacityIncrement = capacityIncrement;
		this.offset = initialOffset;

		head = -offset;
		tail = -offset-1;
	}

	/*

	[head] ------ [tail]



	 */

	public int addHead(T object) {
		head--;

		int i = head + offset;

		if (i < 0){

			offset = offset - capacityIncrement;
			i = head + offset;

			Object[] newObjects = new Object[objects.length + capacityIncrement];

			System.arraycopy(objects, 0, newObjects, capacityIncrement, objects.length);

			objects = newObjects;
		}


		objects[i] = object;

		return head;
	}

	public int addTail(T object) {
		tail++;

		int i = tail + offset;

		if (i >= objects.length) {

			Object[] newObjects = new Object[objects.length + capacityIncrement];

			System.arraycopy(objects, 0, newObjects, 0, objects.length);

			objects = newObjects;
		}

		objects[i] = object;

		return tail;
	}

	public void set(int index, T object) {
		int i = index + offset;

		if (!withinBounds(i)){
			throw new ArrayIndexOutOfBoundsException();
		}

		objects[i] = object;

	}

	public T remove(int index){

		int i = index + offset;

		if (!withinBounds(i))
			return null;

		if (index == tail){

			tail--;

		}
		else if (index == head){

			head++;

		}

		T object = (T) objects[i];
		objects[i] = null;
		return object;
	}

	public T removeTail(){
		return remove(tail);
	}
	public T removeHead(){
		return remove(head);
	}


	public T get(int index) {
		int i = index + offset;
		if (withinBounds(i))
			return (T) objects[i];
		else return null;
	}

	public T getNoOffset(int i){
		return (T)objects[i];
	}

	private boolean withinBounds(int i) {
		return (i >= 0 && i < getSize());
	}
	public int getSize(){
		return tail - head;
	}

	public int start() {
		return head;
	}

	public int end() {
		return tail+1;
	}

}
