/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table{

import org.aswing.DefaultListSelectionModel;
import org.aswing.ListSelectionModel;
import org.aswing.util.ArrayUtils;
import org.aswing.event.PropertyChangeEvent;
import org.aswing.event.SelectionEvent;

/**
 * The standard column-handler for a <code>JTable</code>.
 * <p>
 * @author iiley
 */
public class DefaultTableColumnModel implements TableColumnModel{
	
	/** Array of TableColumn objects in this model */
	private var tableColumns:Array;
	/** Model for keeping track of column selections */
	private var selectionModel:ListSelectionModel;
	/** Width margin between each column */
	private var columnMargin:int;
	/** List of TableColumnModelListener */
	private var listenerList:Array;
	/** Column selection allowed in this column model */
	private var columnSelectionAllowed:Boolean;
	/** A local cache of the combined width of all columns */
	private var totalColumnWidth:int;
		
	/**
	 * Creates a default table column model.
	 */	
	public function DefaultTableColumnModel(){
		tableColumns = new Array();
		listenerList = new Array();
		
		setSelectionModel(createSelectionModel());
		setColumnMargin(1);
		invalidateWidthCache();
		setColumnSelectionAllowed(false);
	}
	
	/**
	 *  Appends <code>aColumn</code> to the end of the
	 *  <code>tableColumns</code> array.
	 *  This method also posts the <code>columnAdded</code>
	 *  event to its listeners.
	 *
	 * @param aColumn the <code>TableColumn</code> to be added
	 * @see	#removeColumn()
	 */	
	public function addColumn(aColumn:TableColumn):void{
		if (aColumn == null){
			trace("Adding null column ignored");
			return;
		}
		tableColumns.push(aColumn);
		aColumn.addPropertyChangeListener(__propertyChanged);
		invalidateWidthCache();
		checkLeadAnchor();
		fireColumnAdded(new TableColumnModelEvent(this, 0, (getColumnCount() - 1)));
	}
	
	/**
	 *  Deletes the <code>column</code> from the
	 *  <code>tableColumns</code> array.  This method will do nothing if
	 *  <code>column</code> is not in the table's columns list.
	 *  <code>tile</code> is called
	 *  to resize both the header and table views.
	 *  This method also posts a <code>columnRemoved</code>
	 *  event to its listeners.
	 *
	 * @param column the <code>TableColumn</code> to be removed
	 * @see	#addColumn()
	 */	
	public function removeColumn(column:TableColumn):void{
		var columnIndex:int = ArrayUtils.indexInArray(tableColumns, column);
		if (columnIndex != (- 1)){
			if (selectionModel != null){
				selectionModel.removeIndexInterval(columnIndex, columnIndex);
			}
			checkLeadAnchor();
			column.removePropertyChangeListener(__propertyChanged);
			tableColumns.splice(columnIndex, 1);
			invalidateWidthCache();
			fireColumnRemoved(new TableColumnModelEvent(this, columnIndex, 0));
		}
	}
	
	/**
	 * Moves the column and heading at <code>columnIndex</code> to
	 * <code>newIndex</code>.  The old column at <code>columnIndex</code>
	 * will now be found at <code>newIndex</code>.  The column
	 * that used to be at <code>newIndex</code> is shifted
	 * left or right to make room.  This will not move any columns if
	 * <code>columnIndex</code> equals <code>newIndex</code>.  This method
	 * also posts a <code>columnMoved</code> event to its listeners.
	 *
	 * @param columnIndex the index of column to be moved
	 * @param newIndex	  new index to move the column
	 * @exception Error	if <code>column</code> or
	 * 						<code>newIndex</code>
	 *						are not in the valid range
	 */	
	public function moveColumn(columnIndex:int, newIndex:int):void{
		if ((((columnIndex < 0) || (columnIndex >= getColumnCount())) || (newIndex < 0)) || (newIndex >= getColumnCount())){
			trace("Error : moveColumn() - Index out of range");
			throw new Error("moveColumn() - Index out of range");
			return;
		}
		var aColumn:TableColumn;
		if (columnIndex == newIndex){
			fireColumnMoved(new TableColumnModelEvent(this, columnIndex, newIndex));
			return ;
		}
		aColumn = TableColumn(tableColumns[columnIndex]);
		tableColumns.splice(columnIndex, 1);
		var selected:Boolean = selectionModel.isSelectedIndex(columnIndex);
		selectionModel.removeIndexInterval(columnIndex, columnIndex);
		tableColumns.splice(newIndex, 0, aColumn);
		selectionModel.insertIndexInterval(newIndex, 1, true);
		if (selected){
			selectionModel.addSelectionInterval(newIndex, newIndex);
		}else{
			selectionModel.removeSelectionInterval(newIndex, newIndex);
		}
		fireColumnMoved(new TableColumnModelEvent(this, columnIndex, newIndex));
	}
	
	/**
	 * Sets the column margin to <code>newMargin</code>.  This method
	 * also posts a <code>columnMarginChanged</code> event to its
	 * listeners.
	 *
	 * @param newMargin the new margin width, in pixels
	 * @see	#getColumnMargin()
	 * @see	#getTotalColumnWidth()
	 */	
	public function setColumnMargin(newMargin:int):void{
		if (newMargin != columnMargin){
			columnMargin = newMargin;
			fireColumnMarginChanged();
		}
	}
	
	/**
	 * Returns the number of columns in the <code>tableColumns</code> array.
	 *
	 * @return	the number of columns in the <code>tableColumns</code> array
	 * @see	#getColumns()
	 */	
	public function getColumnCount():int{
		return tableColumns.length;
	}
	
	/**
	 * Returns an <code>Array</code> of all the columns in the model.
	 * @return an <code>Array</code> of the columns in the model
	 */	
	public function getColumns():Array{
		return tableColumns.concat();
	}
	
	/**
	 * Returns the index of the first column in the <code>tableColumns</code>
	 * array whose identifier is equal to <code>identifier</code>,
	 * when compared using <code>equals</code>.
	 *
	 * @param identifier the identifier object
	 * @return the index of the first column in the 
	 *			<code>tableColumns</code> array whose identifier
	 *			is equal to <code>identifier</code>
	 * @exception Error  if <code>identifier</code>
	 *				is <code>null</code>, or if no
	 *				<code>TableColumn</code> has this
	 *				<code>identifier</code>
	 * @see #getColumn()
	 */	
	public function getColumnIndex(identifier:Object):int{
		if (identifier == null){
			trace("Error : Identifier is null");
			throw new Error("Identifier is null");
		}
		var enumeration:Array = getColumns();
		var aColumn:TableColumn;
		var index:int = 0;
		for(var i:int=0; i<enumeration.length; i++){
			aColumn = TableColumn(enumeration[i]);
			if (identifier == aColumn.getIdentifier()){
				return index;
			}
			index++;
		}
		trace("Error : Identifier is null");
		throw new Error("Identifier not found");
	}
	
	/**
	 * Returns the <code>TableColumn</code> object for the column
	 * at <code>columnIndex</code>.
	 *
	 * @param	columnIndex	the index of the column desired
	 * @return	the <code>TableColumn</code> object for the column
	 *				at <code>columnIndex</code>
	 */	
	public function getColumn(columnIndex:int):TableColumn{
		return TableColumn(tableColumns[columnIndex]);
	}
	
	/**
	 * Returns the width margin for <code>TableColumn</code>.
	 * The default <code>columnMargin</code> is 1.
	 *
	 * @return the maximum width for the <code>TableColumn</code>
	 * @see	#setColumnMargin()
	 */	
	public function getColumnMargin():int{
		return columnMargin;
	}
	
	/**
	 * Returns the index of the column that lies at position <code>x</code>,
	 * or -1 if no column covers this point.
	 * <p>
	 * In keeping with Swing's separable model architecture, a
	 * TableColumnModel does not know how the table columns actually appear on
	 * screen.  The visual presentation of the columns is the responsibility
	 * of the view/controller object using this model (typically JTable).  The
	 * view/controller need not display the columns sequentially from left to
	 * right.  For example, columns could be displayed from right to left to
	 * accomodate a locale preference or some columns might be hidden at the
	 * request of the user.  Because the model does not know how the columns
	 * are laid out on screen, the given <code>xPosition</code> should not be
	 * considered to be a coordinate in 2D graphics space.  Instead, it should
	 * be considered to be a width from the start of the first column in the
	 * model.  If the column index for a given X coordinate in 2D space is
	 * required, <code>JTable.columnAtPoint</code> can be used instead.
	 *
	 * @param x  the horizontal location of interest
	 * @return the index of the column or -1 if no column is found
	 * @see org.aswing.JTable#columnAtPoint()
	 */	
	public function getColumnIndexAtX(x:int):int{
		if (x < 0){
			return -1;
		}
		var cc:int = getColumnCount();
		for (var column:int = 0; column < cc; column++){
			x = (x - getColumn(column).getWidth());
			if (x < 0){
				return column;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the total combined width of all columns.
	 * @return the <code>totalColumnWidth</code> property
	 */	
	public function getTotalColumnWidth():int{
		if (totalColumnWidth == (-1)){
			recalcWidthCache();
		}
		return totalColumnWidth;
	}
	
	/**
	 *  Sets the selection model for this <code>TableColumnModel</code>
	 *  to <code>newModel</code>
	 *  and registers for listener notifications from the new selection
	 *  model.  If <code>newModel</code> is <code>null</code>,
	 *  nothing will be changed. 
	 *
	 * @param newModel the new selection model
	 * @see	#getSelectionModel()
	 */	
	public function setSelectionModel(newModel:ListSelectionModel):void{
		if (newModel == null){
			trace("Setting null ListSelectionModel ignored");
			return;
		}
		var oldModel:ListSelectionModel = selectionModel;
		if (newModel != oldModel){
			if (oldModel != null){
				oldModel.removeListSelectionListener(__selectionChanged);
			}
			selectionModel = newModel;
			newModel.addListSelectionListener(__selectionChanged);
			checkLeadAnchor();
		}
	}
	
	/**
	 * Returns the <code>ListSelectionModel</code> that is used to
	 * maintain column selection state.
	 *
	 * @return	the object that provides column selection state.  Or
	 *		<code>null</code> if row selection is not allowed.
	 * @see	#setSelectionModel()
	 */	
	public function getSelectionModel():ListSelectionModel{
		return selectionModel;
	}
	
	/**
	 * Initialize the lead and anchor of the selection model
	 * based on what the column model contains.
	 */	
	private function checkLeadAnchor():void{
		var lead:int = selectionModel.getLeadSelectionIndex();
		var count:int = tableColumns.length;
		if (count == 0){
			if (lead != (- 1)){
				//TODO check if this is needed to add
				//selectionModel.setValueIsAdjusting(true);
				selectionModel.setAnchorSelectionIndex(- 1);
				selectionModel.setLeadSelectionIndex(- 1);
				//selectionModel.setValueIsAdjusting(false);
			}
		}else{
			if (lead == (- 1)){
				if (selectionModel.isSelectedIndex(0)){
					selectionModel.addSelectionInterval(0, 0);
				}else{
					selectionModel.removeSelectionInterval(0, 0);
				}
			}
		}
	}
	
	/**
	 * Sets whether column selection is allowed.  The default is false.
	 * @param  flag true if column selection will be allowed, false otherwise
	 */	
	public function setColumnSelectionAllowed(flag:Boolean):void{
		columnSelectionAllowed = flag;
	}
	/**
	 * Returns true if column selection is allowed, otherwise false.
	 * The default is false.
	 * @return the <code>columnSelectionAllowed</code> property
	 */	
	public function getColumnSelectionAllowed():Boolean{
		return columnSelectionAllowed;
	}
	/**
	 * Returns an array of selected columns.  If <code>selectionModel</code>
	 * is <code>null</code>, returns an empty array.
	 * @return an array of selected columns or an empty array if nothing
	 *			is selected or the <code>selectionModel</code> is
	 *			<code>null</code>
	 */	
	public function getSelectedColumns():Array{
		if (selectionModel != null){
			var iMin:int = selectionModel.getMinSelectionIndex();
			var iMax:int = selectionModel.getMaxSelectionIndex();
			if ((iMin == (- 1)) || (iMax == (- 1))){
				return new Array();
			}
			var rv:Array = new Array();
			for (var i:int = iMin; i <= iMax; i++){
				if (selectionModel.isSelectedIndex(i)){
					rv.push(i);
				}
			}
			return rv;
		}
		return new Array();
	}
	
	/**
	 * Returns the number of columns selected.
	 * @return the number of columns selected
	 */	
	public function getSelectedColumnCount():int{
		if (selectionModel != null){
			var iMin:int = selectionModel.getMinSelectionIndex();
			var iMax:int = selectionModel.getMaxSelectionIndex();
			var count:int = 0;
			for (var i:int = iMin; i <= iMax; i++){
				if (selectionModel.isSelectedIndex(i)){
					count++;
				}
			}
			return count;
		}
		return 0;
	}
	
	public function addColumnModelListener(x:TableColumnModelListener):void{
		listenerList.push(x);
	}
	
	public function removeColumnModelListener(x:TableColumnModelListener):void{
		ArrayUtils.removeFromArray(listenerList, x);
	}
	
	public function getColumnModelListeners():Array{
		return listenerList.concat();
	}
	
	private function fireColumnAdded(e:TableColumnModelEvent):void{
		var listeners:Array = listenerList;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (var i:int = (listeners.length - 1); i >= 0; i --){
			var lis:TableColumnModelListener = TableColumnModelListener(listeners[i]);
			lis.columnAdded(e);
		}
	}
	
	private function fireColumnRemoved(e:TableColumnModelEvent):void{
		var listeners:Array = listenerList;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (var i:int = (listeners.length - 1); i >= 0; i --){
			var lis:TableColumnModelListener = TableColumnModelListener(listeners[i]);
			lis.columnRemoved(e);
		}
	}
	
	private function fireColumnMoved(e:TableColumnModelEvent):void{
		var listeners:Array = listenerList;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (var i:int = (listeners.length - 1); i >= 0; i --){
			var lis:TableColumnModelListener = TableColumnModelListener(listeners[i]);
			lis.columnMoved(e);
		}
	}
	
	private function fireColumnSelectionChanged(firstIndex:int, lastIndex:int, programmatic:Boolean):void{
		var listeners:Array = listenerList;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (var i:int = (listeners.length - 1); i >= 0; i --){
			var lis:TableColumnModelListener = TableColumnModelListener(listeners[i]);
			lis.columnSelectionChanged(this, firstIndex, lastIndex, programmatic);
		}
	}
	
	private function fireColumnMarginChanged():void{
		var listeners:Array = listenerList;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (var i:int = (listeners.length - 1); i >= 0; i --){
			var lis:TableColumnModelListener = TableColumnModelListener(listeners[i]);
			lis.columnMarginChanged(this);
		}
	}
	
	public function getListeners():Array{
		return listenerList.concat();
	}
	
	private function __propertyChanged(e:PropertyChangeEvent):void{
		if ((e.getPropertyName() == "width") || (e.getPropertyName() == "preferredWidth")){
			invalidateWidthCache();
			// This is a misnomer, we're using this method 
			// simply to cause a relayout. 
			fireColumnMarginChanged();
		}
	}
	
	private function __selectionChanged(e:SelectionEvent):void{
		fireColumnSelectionChanged(e.getFirstIndex(), e.getLastIndex(), e.isProgrammatic());
	}
	
	private function createSelectionModel():ListSelectionModel{
		return new DefaultListSelectionModel();
	}
	
	/**
	 * Recalculates the total combined width of all columns.  Updates the
	 * <code>totalColumnWidth</code> property.
	 */	
	private function recalcWidthCache():void{
		var enumeration:Array = tableColumns;
		totalColumnWidth = 0;
		for(var i:int=0; i<enumeration.length; i++){
			var c:TableColumn = enumeration[i];
			totalColumnWidth += c.getWidth();
		}
	}
	
	private function invalidateWidthCache():void{
		totalColumnWidth = -1;
	}
	
	public function toString():String{
		return "DefaultTableColumnModel[]";
	}
}
}