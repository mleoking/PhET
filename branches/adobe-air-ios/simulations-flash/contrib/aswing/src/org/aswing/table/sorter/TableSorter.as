/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.event.TableModelEvent;
import org.aswing.event.TableModelListener;
import org.aswing.geom.Point;
import org.aswing.Icon;
import org.aswing.table.AbstractTableModel;
import org.aswing.table.JTableHeader;
import org.aswing.table.sorter.Arrow;
import org.aswing.table.sorter.Directive;
import org.aswing.table.sorter.Row;
import org.aswing.table.sorter.SortableHeaderRenderer;
import org.aswing.table.TableCellFactory;
import org.aswing.table.TableColumnModel;
import org.aswing.table.TableModel;
import org.aswing.util.ArrayUtils;
import org.aswing.util.Delegate;
import org.aswing.util.HashMap;

/**
 * A class that make your JTable sortable. Usage:
 * <pre>
 * var sorter:TableSorter = new TableSorter(yourTableModel);
 * sorter.setTableHeader(yourTable.getTableHeader());
 * yourTable.setModel(sorter);
 * </pre>
 * @author iiley
 */
class org.aswing.table.sorter.TableSorter extends AbstractTableModel implements TableModelListener{
	
    public static var DESCENDING:Number = -1;
    public static var NOT_SORTED:Number = 0;
    public static var ASCENDING:Number = 1;

    private static var EMPTY_DIRECTIVE:Directive;
    public static var NUMBER_COMAPRATOR:Function;
    public static var LEXICAL_COMPARATOR:Function;
    
    private static var inited:Boolean;

    private var tableModel:TableModel;
    private var viewToModel:Array; //Row[]
    private var modelToView:Array; //int[]
    private var columnSortables:Array;

    private var tableHeader:JTableHeader;
    private var mouseListener:Object;;
    private var tableModelListener:TableModelListener;
    private var columnComparators:HashMap;
    private var sortingColumns:Array;
	
	/**
	 * TableSorter(tableModel:TableModel, tableHeader:JTableHeader)<br>
	 * TableSorter(tableModel:TableModel)<br>
	 * TableSorter()<br>
	 */
    public function TableSorter(tableModel:TableModel, tableHeader:JTableHeader) {
        super();
        initStatics();
        columnComparators  = new HashMap();
        sortingColumns     = new Array();
        mouseListener      = new Object();
        columnSortables    = new Array();
        mouseListener[Component.ON_PRESS] = Delegate.create(this, __mousePress);
        mouseListener[Component.ON_RELEASE] = Delegate.create(this, __mouseRelease);
        tableModelListener = this;
        setTableHeader(tableHeader);
        setTableModel(tableModel);
    }
    
    private function initStatics():Void{
        if(inited == undefined){
			EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);

			NUMBER_COMAPRATOR = function(o1, o2):Number {
				o1 = Number(o1);
				o2 = Number(o2);
				return o1 == o2 ? 0 : (o1 > o2 ? 1 : -1);
		    };
    
			LEXICAL_COMPARATOR = function(o1, o2):Number {
		    	o1 = o1.toString();
		    	o2 = o2.toString();
				return o1 == o2 ? 0 : (o1 > o2 ? 1 : -1);
		    };
        	inited = true;
        }
    }

    private function clearSortingState():Void {
        viewToModel = null;
        modelToView = null;
    }

    public function getTableModel():TableModel {
        return tableModel;
    }
	
	/**
	 * Sets the tableModel
	 * @param tableModel the tableModel
	 */
    public function setTableModel(tableModel:TableModel):Void {
        if (this.tableModel != null) {
            this.tableModel.removeTableModelListener(tableModelListener);
        }

        this.tableModel = tableModel;
        if (this.tableModel != null) {
            this.tableModel.addTableModelListener(tableModelListener);
        }

        clearSortingState();
        fireTableStructureChanged();
    }

    public function getTableHeader():JTableHeader {
        return tableHeader;
    }
	
	/**
	 * Sets the table header
	 * @param tableHeader the table header
	 */
    public function setTableHeader(tableHeader:JTableHeader):Void {
        if (this.tableHeader != null) {
            this.tableHeader.removeEventListener(mouseListener);
            var defaultRenderer:TableCellFactory = this.tableHeader.getDefaultRenderer();
            if (defaultRenderer instanceof SortableHeaderRenderer) {
                this.tableHeader.setDefaultRenderer((SortableHeaderRenderer(defaultRenderer)).getTableCellFactory());
            }
        }
        this.tableHeader = tableHeader;
        if (this.tableHeader != null) {
            this.tableHeader.addEventListener(mouseListener);
            this.tableHeader.setDefaultRenderer(
                    new SortableHeaderRenderer(this.tableHeader.getDefaultRenderer(), this));
        }
    }

    public function isSorting():Boolean {
        return sortingColumns.length != 0;
    }
    
    public function getSortingColumns():Array{
    	return sortingColumns;
    }
    
    /**
     * Sets specified column sortable, default is true.
     * @param column   column
     * @param sortable true to set the column sortable, false to not
     */
    public function setColumnSortable(column:Number, sortable:Boolean):Void{
    	if(isColumnSortable(column) != sortable){
    		columnSortables[column] = sortable;
    		if(!sortable && getSortingStatus(column) != NOT_SORTED){
    			setSortingStatus(column, NOT_SORTED);
    		}
    	}
    }
    
    /**
     * Returns specified column sortable, default is true.
     * @return true if the column is sortable, false otherwish
     */    
    public function isColumnSortable(column:Number):Boolean{
    	return columnSortables[column] != false;
    }
    
    private function getDirective(column:Number):Directive {
        for (var i:Number = 0; i < sortingColumns.length; i++) {
            var directive:Directive = Directive(sortingColumns[i]);
            if (directive.column == column) {
                return directive;
            }
        }
        return EMPTY_DIRECTIVE;
    }

    public function getSortingStatus(column:Number):Number {
        return getDirective(column).direction;
    }

    private function sortingStatusChanged():Void {
        clearSortingState();
        fireTableDataChanged();
        if (tableHeader != null) {
            tableHeader.repaint();
        }
    }
	
	/**
	 * Sets specified column to be sort as specified direction.
	 * @param column the column to be sort
	 * @param status sort direction, should be one of these values:
	 * <ul>
	 * <li> DESCENDING : descending sort 
	 * <li> NOT_SORTED : not sort
	 * <li> ASCENDING  : ascending sort
	 * </ul>
	 */
    public function setSortingStatus(column:Number, status:Number):Void {
        var directive:Directive = getDirective(column);
        if (directive != EMPTY_DIRECTIVE) {
        	ArrayUtils.removeFromArray(sortingColumns, directive);
        }
        if (status != NOT_SORTED) {
        	sortingColumns.push(new Directive(column, status));
        }
        sortingStatusChanged();
    }

    public function getHeaderRendererIcon(column:Number, size:Number):Icon {
        var directive:Directive = getDirective(column);
        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }
        return new Arrow(directive.direction == DESCENDING, size);//, sortingColumns.indexOf(directive));
    }
	
	/**
	 * Cancels all sorting column to be NOT_SORTED.
	 */
    public function cancelSorting():Void {
        sortingColumns.splice(0);
        sortingStatusChanged();
    }
	
	/**
	 * Sets a comparator the specified columnClass. For example:
	 * <pre>
	 * setColumnComparator("Number", aNumberComparFunction);
	 * </pre>
	 * @param columnClass the column class name
	 * @param comparator the comparator function should be this spec:
	 * 			function(o1, o2):Number, it should return -1 or 0 or 1.
	 * @see org.aswing.table.TableModel#getColumnClass()
	 */
    public function setColumnComparator(columnClass:String, comparator:Function):Void {
        if (comparator == null) {
            columnComparators.remove(columnClass);
        } else {
            columnComparators.put(columnClass, comparator);
        }
    }
	
	/**
	 * Returns the comparator function for given column.
	 * @return the comparator function for given column.
	 * @see #setColumnComparator()
	 */
    public function getComparator(column:Number):Function {
        var columnType:String = tableModel.getColumnClass(column);
        var comparator:Function = Function(columnComparators.get(columnType));
        if (comparator != null) {
            return comparator;
        }
        if(columnType == "Number"){
			return NUMBER_COMAPRATOR;
        }else{
        	return LEXICAL_COMPARATOR;
        }
    }

    private function getViewToModel():Array {
        if (viewToModel == null) {
            var tableModelRowCount:Number = tableModel.getRowCount();
            viewToModel = new Array(tableModelRowCount);
            for (var row:Number = 0; row < tableModelRowCount; row++) {
                viewToModel[row] = new Row(this, row);
            }

            if (isSorting()) {
                viewToModel.sort(sortImp);
            }
        }
        return viewToModel;
    }
    
    private function sortImp(row1:Row, row2:Row):Number{
    	return row1.compareTo(row2);
    }
	
	/**
	 * Calculates the model index from the sorted index.
	 * @return the index in model from the sorter model index
	 */
    public function modelIndex(viewIndex:Number):Number {
        return getViewToModel()[viewIndex].modelIndex;
    }

    private function getModelToView():Array { //int[]
        if (modelToView == null) {
            var n:Number = getViewToModel().length;
            modelToView = new Array(n);
            for (var i:Number = 0; i < n; i++) {
                modelToView[modelIndex(i)] = i;
            }
        }
        return modelToView;
    }

    // TableModel interface methods 

    public function getRowCount():Number {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    public function getColumnCount():Number {
        return (tableModel == null) ? 0 : tableModel.getColumnCount();
    }

    public function getColumnName(column:Number):String {
        return tableModel.getColumnName(column);
    }

    public function getColumnClass(column:Number):String {
        return tableModel.getColumnClass(column);
    }

    public function isCellEditable(row:Number, column:Number):Boolean {
        return tableModel.isCellEditable(modelIndex(row), column);
    }

    public function getValueAt(row:Number, column:Number):Object {
        return tableModel.getValueAt(modelIndex(row), column);
    }

    public function setValueAt(aValue:Object, row:Number, column:Number):Void {
        tableModel.setValueAt(aValue, modelIndex(row), column);
    }

    public function tableChanged(e:TableModelEvent):Void {
        // If we're not sorting by anything, just pass the event along.             
        if (!isSorting()) {
            clearSortingState();
            fireTableChanged(e);
            return;
        }
            
        // If the table structure has changed, cancel the sorting; the             
        // sorting columns may have been either moved or deleted from             
        // the model. 
        if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
            cancelSorting();
            fireTableChanged(e);
            return;
        }

        // We can map a cell event through to the view without widening             
        // when the following conditions apply: 
        // 
        // a) all the changes are on one row (e.getFirstRow() == e.getLastRow()) and, 
        // b) all the changes are in one column (column != TableModelEvent.ALL_COLUMNS) and,
        // c) we are not sorting on that column (getSortingStatus(column) == NOT_SORTED) and, 
        // d) a reverse lookup will not trigger a sort (modelToView != null)
        //
        // Note: INSERT and DELETE events fail this test as they have column == ALL_COLUMNS.
        // 
        // The last check, for (modelToView != null) is to see if modelToView 
        // is already allocated. If we don't do this check; sorting can become 
        // a performance bottleneck for applications where cells  
        // change rapidly in different parts of the table. If cells 
        // change alternately in the sorting column and then outside of             
        // it this class can end up re-sorting on alternate cell updates - 
        // which can be a performance problem for large tables. The last 
        // clause avoids this problem. 
        var column:Number = e.getColumn();
        if (e.getFirstRow() == e.getLastRow()
                && column != TableModelEvent.ALL_COLUMNS
                && getSortingStatus(column) == NOT_SORTED
                && modelToView != null) {
            var viewIndex:Number = getModelToView()[e.getFirstRow()];
            fireTableChanged(new TableModelEvent(this, 
                                                 viewIndex, viewIndex, 
                                                 column, e.getType()));
            return;
        }

        // Something has happened to the data that may have invalidated the row order. 
        clearSortingState();
        fireTableDataChanged();
        return;
    }
    
    private var pressedPoint:Point;
    private function __mousePress(source:JTableHeader):Void {
    	pressedPoint = source.getMousePosition();
    }

    private function __mouseRelease(source:JTableHeader):Void {
        var h:JTableHeader = source;
        var point:Point = h.getMousePosition();
        //if user are dragging the header, not sort
        if(!point.equals(pressedPoint)){
        	return;
        }
        var columnModel:TableColumnModel = h.getColumnModel();
        var viewColumn:Number = columnModel.getColumnIndexAtX(h.getMousePosition().x);
        var column:Number = columnModel.getColumn(viewColumn).getModelIndex();
        if (column != -1 && isColumnSortable(column)) {
            var status:Number = getSortingStatus(column);
            if (!Key.isDown(Key.CONTROL)) {
                cancelSorting();
            }
            // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or 
            // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed. 
            status = status + (Key.isDown(Key.SHIFT) ? -1 : 1);
            status = (status + 4) % 3 - 1; // signed mod, returning {-1, 0, 1}
            setSortingStatus(column, status);
        }
    }
}