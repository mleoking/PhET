
/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.table.sorter.Directive;
import org.aswing.table.sorter.TableSorter;
import org.aswing.table.TableModel;
/**
 * @author iiley
 */
class org.aswing.table.sorter.Row {
	
    private var modelIndex:Number;
    private var tableSorter:TableSorter;

    public function Row(tableSorter:TableSorter, index:Number) {
    	this.tableSorter = tableSorter;
        this.modelIndex = index;
    }

    public function compareTo(o):Number {
        var row1:Number = modelIndex;
        var row2:Number = (Row(o)).modelIndex;
		var sortingColumns:Array = tableSorter.getSortingColumns();
		var tableModel:TableModel = tableSorter.getTableModel();
        for (var i:Number=0; i<sortingColumns.length; i++) {
            var directive:Directive = Directive(sortingColumns[i]);
            var column:Number = directive.column;
            var o1 = tableModel.getValueAt(row1, column);
            var o2 = tableModel.getValueAt(row2, column);

            var comparison:Number = 0;
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
}