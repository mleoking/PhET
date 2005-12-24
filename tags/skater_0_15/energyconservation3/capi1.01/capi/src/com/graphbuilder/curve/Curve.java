package com.graphbuilder.curve;

/**
<p>A Curve is an object that defines itself using mathematical equations and points of a
control-path in a given dimension.  Classes that extend Curve must define the appendTo method.

<p>A Curve object can be appended to a MultiPath object which creates a series of points that
represent the curve.  Most curves in the com.graphbuilder.curve package extend the
ParametricCurve class, with one exception being the Polyline class.  The ParametricCurve
class allows curves to use the BinaryCurveApproximationAlgorithm class to generate
the points.  The number of points created depends on the curve properties, the control points,
the sample limit and the flatness.  The flatness is the only parameter that is controlled
by the multi-path.  How the flatness and sample limit affect the number of points created
is described in the BinaryCurveApproximationAlgorithm class.

<p>The data representation of a point is a double array.  The double array was chosen because
it offers a high degree of precision.  An array based representation supports n-dimensional
curves, with index locations acting as dimensions.

<p>The ControlPath stores com.graphbuilder.curve.Point objects.  The reason the ControlPath
stores Point objects and not double arrays is to provide one level of indirection.  For example,
suppose two different objects had a reference to the same double array.  To change the dimension
of the array, a new array has to be created.  This would require updating the references in each
of the objects to the new array.  However, if a Point object stores the array, and the objects
refer to the Point object, then only the reference to the array in the Point object needs to be
updated.  However, multi-paths do not store Point objects, but instead references to double arrays.
There is no need for MultiPaths to reference Point objects because the points inside a multi-path
are generated.

<p>In many of the implementations, curve instances share static memory blocks which are used to
store the results of computations.  The reason this is done is to avoid using excessive memory.
However, none of the methods in the curve package are synchronized.  Thus, multiple threads should
not be appending curves to multi-paths, otherwise the results of computations will be overwritten.

@see com.graphbuilder.curve.MultiPath
@see com.graphbuilder.curve.ControlPath
@see com.graphbuilder.curve.GroupIterator
@see com.graphbuilder.curve.BinaryCurveApproximationAlgorithm
@see com.graphbuilder.curve.ParametricCurve
@see com.graphbuilder.curve.Point
@see #appendTo(MultiPath)
*/
public abstract class Curve {

	/**
	@see #getControlPath()
	*/
	protected ControlPath cp;

	/**
	@see #getGroupIterator()
	*/
	protected GroupIterator gi;

	/**
	@see #getConnect()
	*/
	protected boolean connect = false;

	/**
	Constructs a curve with the specified control-path and group-iterator.

	@throws IllegalArgumentException If the control-path or group-iterator is null.
	*/
	public Curve(ControlPath cp, GroupIterator gi) {
		setControlPath(cp);
		setGroupIterator(gi);
	}

	/**
	Returns the control-path this curve uses to define itself.

	@see #setControlPath(ControlPath)
	@see com.graphbuilder.curve.ControlPath
	*/	
	public ControlPath getControlPath() {
		return cp;
	}

	/**
	Sets the control-path this curve uses to define itself.

	@throws IllegalArgumentException If the control-path is null.
	@see #getControlPath()
	*/
	public void setControlPath(ControlPath cp) {
		if (cp == null)
			throw new IllegalArgumentException("ControlPath cannot be null.");

		this.cp = cp;
	}

	/**
	Returns the group-iterator associated with this curve.  The group-iterator determines
	which points from the control-path, and the order of the points this curve uses to define
	itself.

	@see #setGroupIterator(GroupIterator)
	@see #getControlPath()
	*/
	public GroupIterator getGroupIterator() {
		return gi;
	}

	/**
	Sets the group-iterator this curve uses to define itself.

	@see #getGroupIterator()
	*/
	public void setGroupIterator(GroupIterator gi) {
		if (gi == null)
			throw new IllegalArgumentException("GroupIterator cannot be null.");

		this.gi = gi;
	}

	/**
	Connect is used in the appendTo method to determine if the first point appended should be of type
	MOVE_TO or LINE_TO.  If connect is true then LINE_TO is used.  If connect is false then MOVE_TO is
	used.

	@see #setConnect(boolean)
	@see #appendTo(MultiPath)
	@see com.graphbuilder.curve.MultiPath#MOVE_TO
	@see com.graphbuilder.curve.MultiPath#LINE_TO
	*/
	public boolean getConnect() {
		return connect;
	}

	/**
	Sets the value of the boolean connect flag.

	@see #getConnect()
	*/
	public void setConnect(boolean b) {
		connect = b;
	}

	/**
	Appends a sequence of points defined by this curve to the multi-path.  If the internal state of
	the curve is such that it cannot be evaluated, then the specification is for this method to
	return quietly.

	@see com.graphbuilder.curve.MultiPath
	@see com.graphbuilder.curve.BinaryCurveApproximationAlgorithm
	*/
	public abstract void appendTo(MultiPath mp);

	/**
	Resets the shared memory to the initial state.
	*/
	public void resetMemory() {}
}