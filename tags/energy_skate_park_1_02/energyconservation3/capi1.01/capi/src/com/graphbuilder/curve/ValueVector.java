package com.graphbuilder.curve;

/**
A value-vector is a sequence of values that some curves use to define themselves,
sometimes called a knot-vector or a weight-vector.  The values are stored using an
array.
*/
public class ValueVector {

	protected int size = 0;
	protected double[] value = null;

	/**
	Creates a ValueVector with initial capacity of 2.
	*/
	public ValueVector() {
		value = new double[2];
	}

	/**
	Creates a ValueVector using the specified array and initial size.

	@throws IllegalArgumentException If the value array is null or size < 0 or size > data.length.
	*/
	public ValueVector(double[] value, int size) {
		if (value == null)
			throw new IllegalArgumentException("value array cannot be null.");

		if (size < 0 || size > value.length)
			throw new IllegalArgumentException("size >= 0 && size <= value.length required");

		this.value = value;
		this.size = size;
	}

	/**
	Creates a ValueVector with the specified initial capacity.
	*/
	public ValueVector(int initialCapacity) {
		value = new double[initialCapacity];
	}

	/**
	Returns the number of values in the value array.
	*/
	public int size() {
		return size;
	}

	/**
	Returns the value at the specified index.

	@throws IllegalArgumentException If index < 0 or index >= size.
	*/
	public double get(int index) {
		if (index < 0 || index >= size)
			throw new IllegalArgumentException("required: (index >= 0 && index < size) but: (index = " + index + ", size = " + size + ")");

		return value[index];
	}

	/**
	Sets the value at the specified index.

	@throws IllegalArgumentException If index < 0 or index >= size.
	*/
	public void set(double d, int index) {
		if (index < 0 || index >= size)
			throw new IllegalArgumentException("required: (index >= 0 && index < size) but: (index = " + index + ", size = " + size + ")");

		value[index] = d;
	}

	/**
	Removes the value at the specified index. Values at a higher index are shifted to fill the space.

	@throws IllegalArgumentException If index < 0 or index >= size.
	*/
	public void remove(int index) {
		if (index < 0 || index >= size)
			throw new IllegalArgumentException("required: (index >= 0 && index < size) but: (index = " + index + ", size = " + size + ")");

		for (int i = index + 1; i < size; i++)
			value[i-1] = value[i];

		size--;
	}

	/**
	Adds a value to the value array at index location size.
	*/
	public void add(double d) {
		insert(d, size);
	}

	/**
	Inserts the value at the specified index location.  Values at an equal or higher index are shifted to make space.

	@throws IllegalArgumentException If index < 0 or index > size.
	*/
	public void insert(double d, int index) {
		if (index < 0 || index > size)
			throw new IllegalArgumentException("required: (index >= 0 && index <= size) but: (index = " + index + ", size = " + size + ")");

		ensureCapacity(size + 1);

		for (int i = size; i > index; i--)
			value[i] = value[i-1];

		value[index] = d;
		size++;
	}

	/**
	Checks that the value array has the specified capacity, otherwise the capacity of the
	value array is increased to be the maximum between twice the current capacity and the
	specified capacity.
	*/
	public void ensureCapacity(int capacity) {
		if (value.length < capacity) {
			int x = 2 * value.length;
			if (x < capacity) x = capacity;
			double[] arr = new double[x];
			for (int i = 0; i < size; i++)
				arr[i] = value[i];
			value = arr;
		}
	}

	/**
	Creates a new value array of exact size, copying the values from the old array into the
	new one.
	*/
	public void trimArray() {
		if (size < value.length) {
			double[] arr = new double[size];
			for (int i = 0; i < size; i++)
				arr[i] = value[i];
			value = arr;
		}
	}
}