package com.graphbuilder.curve;

/**
Listener interface to listen for changes in a ValueVector.  The ValueVector class
does not support listeners because it is designed to be lightweight.  This class is
intended to be used as a listener to a GUI component that is used to manipulate
a value-vector.
*/
public interface ValueVectorListener {

	public void valueChanged(ValueVector v, int index, double oldValue);
	public void valueInserted(ValueVector v, int index, double value);
	public void valueRemoved(ValueVector v, int index, double oldValue);

}