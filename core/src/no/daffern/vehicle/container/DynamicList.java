package no.daffern.vehicle.container;

import com.badlogic.gdx.math.Vector;

import java.util.List;

/**
 * Created by Daffern on 30.06.2017.
 */
public class DynamicList<T> {

	private Object[] objects;
	private int capacityIncrement;
	private int offset, size;
	private int head, tail;//first and last index
	private boolean firstObjectAdded = false;

	public DynamicList(){
		this(1,1,0);
	}

	public DynamicList(int initialCapacity, int capacityIncrement, int initialOffset) {
		this.objects = new Object[initialCapacity];
		this.capacityIncrement = capacityIncrement;
		this.offset = initialOffset;

		head = -offset;
		tail = -offset;
		size = 0;
	}

	public void addHead(T object) {
		if (firstObjectAdded){
			set(--head, object);
		}else{
			set(head, object);
			firstObjectAdded = true;
		}
	}

	public void addTail(T object) {
		if (firstObjectAdded){
			set(++tail, object);
		}else{
			set(tail, object);
			firstObjectAdded = true;
		}
	}

	public void set(int index, T object) {
		int i = index + offset;

		if (i < 0) {

			offset = -index;
			head = index;


			Object[] newObjects = new Object[objects.length + capacityIncrement];

			System.arraycopy(objects, 0, newObjects, capacityIncrement, objects.length);

			objects = newObjects;

			i = 0;

		}
		else if (i >= objects.length) {

			tail = index;


			Object[] newObjects = new Object[i + capacityIncrement];

			System.arraycopy(objects, 0, newObjects, 0, objects.length);

			objects = newObjects;
		}


		objects[i] = object;

		size++;

	}


	public T get(int index) {
		int i = index + offset;
		if (withinBounds(i))
			return (T) objects[i];
		else return null;
	}

	private boolean withinBounds(int i) {
		return (i >= 0 && i < size);
	}
	public int getSize(){
		return tail - head;
	}

	public int getHead() {
		return head;
	}

	public int getTail() {
		return tail;
	}


}
