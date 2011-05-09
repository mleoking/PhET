/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Event;

/**
 * @see org.aswing.overflow.JList
 * @see org.aswing.event.ListDataListener
 * @author iiley
 */
class org.aswing.event.ListDataEvent extends Event{

    /** Identifies one or more changes in the lists contents. */
    public static var CONTENTS_CHANGED:String = "CONTENTS_CHANGED";
    /** Identifies the addition of one or more contiguous items to the list */
    public static var INTERVAL_ADDED:String = "INTERVAL_ADDED";
    /** Identifies the removal of one or more contiguous items from the list */
    public static var INTERVAL_REMOVED:String = "INTERVAL_REMOVED";

    private var index0:Number;
    private var index1:Number;
    private var removedItems:Array;

    /**
     * Returns the event type. The possible values are:
     * <ul>
     * <li> {@link #CONTENTS_CHANGED}
     * <li> {@link #INTERVAL_ADDED}
     * <li> {@link #INTERVAL_REMOVED}
     * </ul>
     *
     * @return an number representing the type value
     */
    public function getType():String { return super.getType(); }

    /**
     * Returns the lower index of the range. For a single
     * element, this value is the same as that returned by {@link #getIndex1}.
     * @return an int representing the lower index value
     */
    public function getIndex0():Number { return index0; }
    /**
     * Returns the upper index of the range. For a single
     * element, this value is the same as that returned by {@link #getIndex0}.
     * @return an int representing the upper index value
     */
    public function getIndex1():Number { return index1; }
	/**
	 * Returns the removed items, it is null or empty array when this is not a removed event.
	 * @return a array that contains the removed items
	 */
	public function getRemovedItems():Array{ return removedItems.concat(); }
    /**
     * Constructs a ListDataEvent object.
     *
     * @param source  the source Object (typically <code>this</code>)
     * @param type    an int specifying {@link #CONTENTS_CHANGED},
     *                {@link #INTERVAL_ADDED}, or {@link #INTERVAL_REMOVED}
     * @param index0  an int specifying the bottom of a range
     * @param index1  an int specifying the top of a range
     * @param removedItems (optional) the items has been removed.
     */
    public function ListDataEvent(source:Object, type:String, index0:Number, index1:Number, removedItems:Array) {
        super(source, type);
		this.index0 = index0;
		this.index1 = index1;
		this.removedItems  = removedItems.concat();
    }

    public function toString():String {
        return "ListDataEvent[type=" + getType() +
        ",index0=" + index0 +
        ",index1=" + index1 + "]";
    }
}
