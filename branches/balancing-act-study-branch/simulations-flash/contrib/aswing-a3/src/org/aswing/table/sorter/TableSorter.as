/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table.sorter{

import flash.events.MouseEvent;

import org.aswing.*;
import org.aswing.event.*;
import org.aswing.geom.*;
import org.aswing.table.AbstractTableModel;
import org.aswing.table.JTableHeader;
import org.aswing.table.TableCellFactory;
import org.aswing.table.TableColumnModel;
import org.aswing.table.TableModel;
import org.aswing.util.ArrayUtils;
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
public class TableSorter extends AbstractTableModel implements TableModelListener{
	
    public static var DESCENDING:int = -1;
    public static var NOT_SORTED:int = 0;
    public static var ASCENDING:int = 1;

    private static var EMPTY_DIRECTIVE:Directive;
    public static var NUMBER_COMAPRATOR:Function;
    public static var LEXICAL_COMPARATOR:Function;
    
    private static var inited:Boolean = false;

    private var tableModel:TableModel;
    private var viewToModel:Array; //Row[]
    private var modelToView:Array; //int[]
    private var columnSortables:Array;

    private var tableHeader:JTableHeader;
    private var tableModelListener:TableModelListener;
    private var columnComparators:HashMap;
    private var sortingColumns:Array;
	
	/**
	 * TableSorter(tableModel:TableModel, tableHeader:JTableHeader)<br>
	 * TableSorter(tableModel:TableModel)<br>
	 * TableSorter()<br>
	 */
    public function TableSorter(tableModel:TableModel, tableHeader:JTableHeader=null) {
        super();
        initStatics();
        columnComparators  = new HashMap();
        sortingColumns     = new Array();
        columnSortables    = new Array();
        tableModelListener = this;
        setTableHeader(tableHeader);
        setTableModel(tableModel);
    }
    
    private function initStatics():void{
        if(!inited){
			EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);

			NUMBER_COMAPRATOR = function(o1:*, o2:*):int {
				o1 = Number(o1);
				o2 = Number(o2);
				return o1 == o2 ? 0 : (o1 > o2 ? 1 : -1);
		    };
    
			LEXICAL_COMPARATOR = function(o1:*, o2:*):int {
		    	o1 = o1.toString();
		    	o2 = o2.toString();
				return o1 == o2 ? 0 : (o1 > o2 ? 1 : -1);
		    };
        	inited = true;
        }
    }

    private function clearSortingState():void {
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
    public function setTableModel(tableModel:TableModel):void {
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
    public function setTableHeader(tableHeader:JTableHeader):void {
        if (this.tableHeader != null) {
            this.tableHeader.removeEventListener(MouseEvent.MOUSE_DOWN, __mousePress);
            this.tableHeader.removeEventListener(ReleaseEvent.RELEASE, __mouseRelease);
            var defaultRenderer:TableCellFactory = this.tableHeader.getDefaultRenderer();
            if (defaultRenderer is SortableHeaderRenderer) {
                this.tableHeader.setDefaultRenderer((SortableHeaderRenderer(defaultRenderer)).getTableCellFactory());
            }
        }
        this.tableHeader = tableHeader;
        if (this.tableHeader != null) {
            this.tableHeader.addEventListener(MouseEvent.MOUSE_DOWN, __mousePress);
            this.tableHeader.addEventListener(ReleaseEvent.RELEASE, __mouseRelease);
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
    public function setColumnSortable(column:int, sortable:Boolean):void{
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
    public function isColumnSortable(column:int):Boolean{
    	return columnSortables[column] != false;
    }
    
    private function getDirective(column:int):Directive {
        for (var i:int = 0; i < sortingColumns.length; i++) {
            var directive:Directive = Directive(sortingColumns[i]);
            if (directive.column == column) {
                return directive;
            }
        }
        return EMPTY_DIRECTIVE;
    }

    public function getSortingStatus(column:int):int {
        return getDirective(column).direction;
    }

    private function sortingStatusChanged():void {
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
    public function setSortingStatus(column:int, status:int):void {
        var directive:Directive = getDirective(column);
        if (directive != EMPTY_DIRECTIVE) {
        	ArrayUtils.removeFromArray(sortingColumns, directive);
        }
        if (status != NOT_SORTED) {
        	sortingColumns.push(new Directive(column, status));
        }
        sortingStatusChanged();
    }

    public function getHeaderRendererIcon(column:int, size:int):Icon {
        var directive:Directive = getDirective(column);
        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }
        return new Arrow(directive.direction == DESCENDING, size);//, sortingColumns.indexOf(directive));
    }
	
	/**
	 * Cancels all sorting column to be NOT_SORTED.
	 */
    public function cancelSorting():void {
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
	 * 			function(o1, o2):int, it should return -1 or 0 or 1.
	 * @see org.aswing.table.TableModel#getColumnClass()
	 */
    public function setColumnComparator(columnClass:String, comparator:Function):void {
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
    public function getComparator(column:int):Function {
        var columnType:String = tableModel.getColumnClass(column);
        var comparator:Function = columnComparators.get(columnType) as Function;
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
            var tableModelRowCount:int = tableModel.getRowCount();
            viewToModel = new Array(tableModelRowCount);
            for (var row:int = 0; row < tableModelRowCount; row++) {
                viewToModel[row] = new Row(this, row);
            }

            if (isSorting()) {
                viewToModel.sort(sortImp);
            }
        }
        return viewToModel;
    }
    
    private function sortImp(row1:Row, row2:Row):int{
    	return row1.compareTo(row2);
    }
	
	/**
	 * Calculates the model index from the sorted index.
	 * @return the index in model from the sorter model index
	 */
    public function modelIndex(viewIndex:int):int {
        return getViewToModel()[viewIndex].getModelIndex();
    }

    private function getModelToView():Array { //int[]
        if (modelToView == null) {
            var n:int = getViewToModel().length;
            modelToView = new Array(n);
            for (var i:int = 0; i < n; i++) {
                modelToView[modelIndex(i)] = i;
            }
        }
        return modelToView;
    }

    // TableModel interface methods 

    override public function getRowCount():int {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    override public function getColumnCount():int {
        return (tableModel == null) ? 0 : tableModel.getColumnCount();
    }

    override public function getColumnName(column:int):String {
        return tableModel.getColumnName(column);
    }

    override public function getColumnClass(column:int):String {
        return tableModel.getColumnClass(column);
    }

    override public function isCellEditable(row:int, column:int):Boolean {
        return tableModel.isCellEditable(modelIndex(row), column);
    }

    override public function getValueAt(row:int, column:int):* {
        return tableModel.getValueAt(modelIndex(row), column);
    }

    override public function setValueAt(aValue:*, row:int, column:int):void {
        tableModel.setValueAt(aValue, modelIndex(row), column);
    }

    public function tableChanged(e:TableModelEvent):void {
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
        var column:int = e.getColumn();
        if (e.getFirstRow() == e.getLastRow()
                && column != TableModelEvent.ALL_COLUMNS
                && getSortingStatus(column) == NOT_SORTED
                && modelToView != null) {
            var viewIndex:int = getModelToView()[e.getFirstRow()];
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
    
    private var pressedPoint:IntPoint;
    private function __mousePress(e:MouseEvent):void {
    	var header:JTableHeader = e.currentTarget as JTableHeader;
    	pressedPoint = header.getMousePosition();
    }

    private function __mouseRelease(e:ReleaseEvent):void {
    	if(e.isReleasedOutSide()){
    		return;
    	}
        var h:JTableHeader = e.currentTarget as JTableHeader;
        var point:IntPoint = h.getMousePosition();
        //if user are dragging the header, not sort
        if(!point.equals(pressedPoint)){
        	return;
        }
        var columnModel:TableColumnModel = h.getColumnModel();
        var viewColumn:int = columnModel.getColumnIndexAtX(h.getMousePosition().x);
        if(viewColumn == -1){
        	return;
        }
        var column:int = columnModel.getColumn(viewColumn).getModelIndex();
        if (column != -1 && isColumnSortable(column)) {
            var status:int = getSortingStatus(column);
            if (!e.ctrlKey) {
                cancelSorting();
            }
            status = nextSortingStatus(status, e.shiftKey);
            setSortingStatus(column, status);
        }
    }
    
    // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or 
    // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed.     
    // You can override this method to change the arithmetic
    protected function nextSortingStatus(curStatus:int, shiftKey:Boolean):int{
    	var status:int = curStatus;
	    status = status + (shiftKey ? -1 : 1);
        status = (status + 4) % 3 - 1; // signed mod, returning {-1, 0, 1}
        return status;
    }
}
}