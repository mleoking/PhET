/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table{
	
import org.aswing.event.ModelEvent;

/**
 * @author iiley
 */
public class TableColumnModelEvent extends ModelEvent{
	
    /** The index of the column from where it was moved or removed */
    private var	fromIndex:int;

    /** The index of the column to where it was moved or added from */
    private var	toIndex:int;

    /**
     * Constructs a TableColumnModelEvent object.
     *
     * @param source  the TableColumnModel that originated the event
     *                (typically <code>this</code>)
     * @param from    an int specifying the first row in a range of affected rows
     * @param to      an int specifying the last row in a range of affected rows
     */
    public function TableColumnModelEvent(source:TableColumnModel, from:int, _to:int) {
		super(source);
		fromIndex = from;
		toIndex = _to;
    }

    /** Returns the fromIndex.  Valid for removed or moved events */
    public function getFromIndex():int { return fromIndex; };

    /** Returns the toIndex.  Valid for add and moved events */
    public function getToIndex():int { return toIndex; };
}
}