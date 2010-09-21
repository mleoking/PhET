/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.event.TableModelEvent;

/**
 * TableModelListener defines the interface for an object that listens
 * to changes in a TableModel.
 * @author iiley
 */
interface org.aswing.event.TableModelListener {
    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    public function tableChanged(e:TableModelEvent):Void;
}