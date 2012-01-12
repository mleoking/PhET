/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table.sorter{

import org.aswing.table.TableModel;

/**
 * @author iiley
 */
public class Row{
	
	public var modelIndex:int;
	public var tableSorter:TableSorter;

    public function Row(tableSorter:TableSorter, index:int) {
    	this.tableSorter = tableSorter;
        this.modelIndex = index;
    }

    public function compareTo(o:*):int {
        var row1:int = modelIndex;
        var row2:int = (Row(o)).modelIndex;
		var sortingColumns:Array = tableSorter.getSortingColumns();
		var tableModel:TableModel = tableSorter.getTableModel();
        for (var i:int=0; i<sortingColumns.length; i++) {
            var directive:Directive = Directive(sortingColumns[i]);
            var column:int = directive.column;
            var o1:* = tableModel.getValueAt(row1, column);
            var o2:* = tableModel.getValueAt(row2, column);

            var comparison:int = 0;
            // Define null less than everything, except null.
            if (o1 == null && o2 == null) {
                comparison = 0;
            } else if (o1 == null) {
                comparison = -1;
            } else if (o2 == null) {
                comparison = 1;
            } else {
            	var comparator:Function = tableSorter.getComparator(column);
                comparison = comparator(o1, o2);
            }
            if (comparison != 0) {
                return directive.direction == TableSorter.DESCENDING ? -comparison : comparison;
            }
        }
        return 0;
    }
    
    public function getModelIndex():int{
    	return modelIndex;
    }
}
}