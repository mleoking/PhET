/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Event;
import org.aswing.table.TableColumnModel;

/**
 * @author iiley
 */
class org.aswing.table.TableColumnModelEvent extends Event {
	
    /** The index of the column from where it was moved or removed */
    private var	fromIndex:Number;

    /** The index of the column to where it was moved or added from */
    private var	toIndex:Number;


    /**
     * Constructs a TableColumnModelEvent object.
     *
     * @param source  the TableColumnModel that originated the event
     *                (typically <code>this</code>)
     * @param from    an int specifying the first row in a range of affected rows
     * @param to      an int specifying the last row in a range of affected rows
     */
    public function TableColumnModelEvent(source:TableColumnModel, from:Number, to:Number) {
		super(source, "onTableColumnModelEvent");
		fromIndex = from;
		toIndex = to;
    }

    /** Returns the fromIndex.  Valid for removed or moved events */
    public function getFromIndex():Number { return fromIndex; };

    /** Returns the toIndex.  Valid for add and moved events */
    public function getToIndex():Number { return toIndex; };
}