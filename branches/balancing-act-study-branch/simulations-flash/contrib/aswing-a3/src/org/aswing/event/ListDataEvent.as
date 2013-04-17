/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.event{

/**
 * The event for list model.
 * @see org.aswing.JList
 * @see org.aswing.ListModel
 * @see org.aswing.event.ListDataListener
 * @author iiley
 */
public class ListDataEvent extends ModelEvent{

    private var index0:int;
    private var index1:int;
    private var removedItems:Array;

    /**
     * Returns the lower index of the range. For a single
     * element, this value is the same as that returned by {@link #getIndex1}.
     * @return an int representing the lower index value
     */
    public function getIndex0():int { return index0; }
    
    /**
     * Returns the upper index of the range. For a single
     * element, this value is the same as that returned by {@link #getIndex0}.
     * @return an int representing the upper index value
     */
    public function getIndex1():int { return index1; }
    
	/**
	 * Returns the removed items, it is null or empty array when this is not a removed event.
	 * @return a array that contains the removed items
	 */
	public function getRemovedItems():Array{ return removedItems.concat(); }
	
    /**
     * Constructs a ListDataEvent object.
     *
     * @param source  the source Object (typically <code>this</code>)
     * @param index0  an int specifying the bottom of a range
     * @param index1  an int specifying the top of a range
     * @param removedItems (optional) the items has been removed.
     */
	public function ListDataEvent(source:Object, index0:int, index1:int, removedItems:Array){
		super(source);
		this.index0 = index0;
		this.index1 = index1;
		this.removedItems  = removedItems.concat();
	}
	
}
}