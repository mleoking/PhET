/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Brush;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.overflow.JTable;
import org.aswing.ListSelectionModel;
import org.aswing.LookAndFeel;
import org.aswing.plaf.TableUI;
import org.aswing.plaf.UIResource;
import org.aswing.table.TableColumn;
import org.aswing.table.TableColumnModel;
import org.aswing.UIManager;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicTableUI extends TableUI {
	
	private var table:JTable;
	private var tableListener:Object;
	private var mouseListener:Object;
	
	public function BasicTableUI() {
		super();
		focusRow = 0;
		focusColumn = 0;
	}
	
    public function installUI(c:Component):Void {
        table = JTable(c);
        installDefaults();
        installListeners();
    }
    
    private function installDefaults():Void {
    	var pp:String = "Table.";
        LookAndFeel.installColorsAndFont(table, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(table, pp+"border");
        LookAndFeel.installBasicProperties(table, pp);
        
		var sbg:ASColor = table.getSelectionBackground();
		if (sbg === undefined || sbg instanceof UIResource) {
			table.setSelectionBackground(UIManager.getColor("Table.selectionBackground"));
		}

		var sfg:ASColor = table.getSelectionForeground();
		if (sfg === undefined || sfg instanceof UIResource) {
			table.setSelectionForeground(UIManager.getColor("Table.selectionForeground"));
		}

		var gridColor:ASColor = table.getGridColor();
		if (gridColor === undefined || gridColor instanceof UIResource) {
			table.setGridColor(UIManager.getColor("Table.gridColor"));
		}
    }
    private function installListeners():Void{
    	tableListener = new Object();
    	tableListener[JTable.ON_PRESS] = Delegate.create(this, __onTablePress);
    	tableListener[JTable.ON_RELEASE] = Delegate.create(this, __onTableRelease);
    	tableListener[JTable.ON_RELEASEOUTSIDE] = Delegate.create(this, __onTableReleasedOutside);
    	tableListener[JTable.ON_CLICKED] = Delegate.create(this, __onTableClicked);
    	tableListener[JTable.ON_KEY_DOWN] = Delegate.create(this, __onTableKeyDown);
    	tableListener[JTable.ON_MOUSE_WHEEL] = Delegate.create(this, __onTableMouseWheel);
    	table.addEventListener(tableListener);
    	
    	mouseListener = {onMouseMove:Delegate.create(this, __onTableMouseMove)};
    }
	
	public function uninstallUI(c:Component):Void {
        uninstallDefaults();
        uninstallListeners();
    }
    
    private function uninstallDefaults():Void {
        LookAndFeel.uninstallBorder(table);
    }
    private function uninstallListeners():Void{
    	table.removeEventListener(tableListener);
    }
    
    private function __onTablePress():Void{
    	selectMousePointed();
		Mouse.addListener(mouseListener);
		table.getCellEditor().stopCellEditing();
    }
    
    private function __onTableClicked(source:JTable, clickCount:Number):Void{
    	var p:Point = getMousePosOnTable();
    	var row:Number = table.rowAtPoint(p);
    	var column:Number = table.columnAtPoint(p);
    	if(table.editCellAt(row, column, clickCount)){
    	}
    }
    
    private function __onTableRelease():Void{
    	Mouse.removeListener(mouseListener);
    }
    
    private function __onTableReleasedOutside():Void{
    	Mouse.removeListener(mouseListener);
    }
	
	private function __onTableMouseMove():Void{
		addSelectMousePointed();
	}
	
	private function __onTableMouseWheel(source:JTable, delta:Number):Void{
		if(!table.isEnabled()){
			return;
		}
    	var viewPos:Point = table.getViewPosition();
    	viewPos.y -= delta*table.getVerticalUnitIncrement();
    	table.setViewPosition(viewPos);
    }
	
	private function selectMousePointed():Void{
    	var p:Point = getMousePosOnTable();
    	var row:Number = table.rowAtPoint(p);
    	var column:Number = table.columnAtPoint(p);
		if ((column == -1) || (row == -1)) {
			return;
		}
		makeSelectionChange(row, column);
	}
	
	private function addSelectMousePointed():Void{
    	var p:Point = getMousePosOnTable();
    	var row:Number = table.rowAtPoint(p);
    	var column:Number = table.columnAtPoint(p);
		if ((column == -1) || (row == -1)) {
			return;
		}
		table.changeSelection(row, column, false, true);
	}
	
	private function makeSelectionChange(row:Number, column:Number):Void {
		recordFocusIndecis(row, column);
		var ctrl:Boolean = Key.isDown(getAdditionSelectionKey());
		var shift:Boolean = Key.isDown(getIntervalSelectionKey());

		// Apply the selection state of the anchor to all cells between it and the
		// current cell, and then select the current cell.
		// For mustang, where API changes are allowed, this logic will moved to
		// JTable.changeSelection()
		if (ctrl && shift) {
			var rm:ListSelectionModel = table.getSelectionModel();
			var cm:ListSelectionModel = table.getColumnModel().getSelectionModel();
			var anchorRow:Number = rm.getAnchorSelectionIndex();
			var anchorCol:Number = cm.getAnchorSelectionIndex();

			if (table.isCellSelected(anchorRow, anchorCol)) {
				rm.addSelectionInterval(anchorRow, row);
				cm.addSelectionInterval(anchorCol, column);
			} else {
				rm.removeSelectionInterval(anchorRow, row);
				rm.addSelectionInterval(row, row);
				rm.setAnchorSelectionIndex(anchorRow);
				cm.removeSelectionInterval(anchorCol, column);
				cm.addSelectionInterval(column, column);
				cm.setAnchorSelectionIndex(anchorCol);
			}
		} else {
			table.changeSelection(row, column, ctrl, !ctrl && shift);
		}
	}	
	
	private function changeSelection(rowIndex:Number, columnIndex:Number, toggle:Boolean, extend:Boolean):Void{
		recordFocusIndecis(rowIndex, columnIndex);
		table.changeSelection(rowIndex, columnIndex, toggle, extend);
	}
	
	private function getMousePosOnTable():Point{
		var p:Point = table.getMousePosition();
		return table.getLogicLocationFromPixelLocation(p);
	}
	
    private function getIntervalSelectionKey():Number{
    	return Key.SHIFT;
    }
    private function getAdditionSelectionKey():Number{
    	return Key.CONTROL;
    }	
    private function getEditionKey():Number{
    	return Key.ENTER;
    }
    private function getSelectionKey():Number{
    	return Key.SPACE;
    }
		
	public function paint(c:Component, g:Graphics, b:Rectangle):Void{
		super.paint(c, g, b);
		table.clearChildrenGraphics();
		g = table.getChildrenGraphics();
		var rowCount:Number = table.getRowCount();
		var columnCount:Number = table.getColumnCount();
		if (rowCount <= 0 || columnCount <= 0) {
			return;
		}
		var extentSize:Dimension = table.getExtentSize();
		var viewPos:Point = table.getViewPosition();
		viewPos.x = Math.round(viewPos.x);
		viewPos.y = Math.round(viewPos.y);
		var startX:Number = Math.round(b.x - viewPos.x);
		var startY:Number = Math.round(b.y - viewPos.y + table.getHeaderHeight());
				
		var vb:Rectangle = new Rectangle();
		vb.setSize(extentSize);
		vb.setLocation(viewPos);
		var upperLeft:Point = vb.getLocation();
		var lowerRight:Point = vb.rightBottom();
		var rMin:Number = table.rowAtPoint(upperLeft);
		var rMax:Number = table.rowAtPoint(lowerRight);
		if (rMin == -1) {
			rMin = 0;
		}
		if (rMax == -1) {
			rMax = rowCount - 1;
		}
		var cMin:Number = table.columnAtPoint(upperLeft);
		var cMax:Number = table.columnAtPoint(lowerRight);
		if (cMin == -1) {
			cMin = 0;
		}
		if (cMax == -1) {
			cMax = columnCount - 1;
		}
		
		var minCell:Rectangle = table.getCellRect(rMin, cMin, true);
		var maxCell:Rectangle = table.getCellRect(rMax, cMax, true);
		var damagedArea:Rectangle = minCell.union(maxCell);
		damagedArea.setLocation(damagedArea.getLocation().move(startX, startY));
		
		var pen:Pen = new Pen(table.getGridColor(), 1);
		var brush:Brush = new SolidBrush(table.getSelectionBackground());
		if (table.getShowHorizontalLines()) {
			var x1:Number = damagedArea.x + 0.5;
			var x2:Number = damagedArea.x + damagedArea.width - 1;
			var y:Number = damagedArea.y + 0.5;
			
			g.drawLine(pen, x1, y, x2, y);
			var rh:Number = table.getRowHeight();
			for (var row:Number = rMin; row <= rMax; row++) {
				y += rh;
				if(row == rowCount - 1){
					y -= 1;
				}
				g.drawLine(pen, x1, y, x2, y);
			}
		}
		if (table.getShowVerticalLines()) {
			var cm:TableColumnModel = table.getColumnModel();
			var x:Number = damagedArea.x + 0.5;
			var y1:Number = damagedArea.y + 0.5;
			var y2:Number = y1 + damagedArea.height -1;
			g.drawLine(pen, x, y1, x, y2);
			for (var column:Number = cMin; column <= cMax; column++) {
				var w:Number = cm.getColumn(column).getWidth();
				x += w;
				if(column == columnCount - 1){
					x -= 1;
				}
				g.drawLine(pen, x, y1, x, y2);
			}
		}		
	}	
	//******************************************************************
	//	                    Focus and Key control
	//******************************************************************
	private function __onTableKeyDown():Void{
		if(!table.isEnabled()){
			return;
		}
		var rDir:Number = 0;
		var cDir:Number = 0;
		var code:Number = Key.getCode();
		if(code == Key.LEFT){
			cDir = -1;
		}else if(code == Key.RIGHT){
			cDir = 1;
		}else if(code == Key.UP){
			rDir = -1;
		}else if(code == Key.DOWN){
			rDir = 1;
		}
		if(cDir != 0 || rDir != 0){
			moveFocus(rDir, cDir);
			paintCurrentCellFocus();
			return;
		}
		if(code == getSelectionKey()){
			table.changeSelection(focusRow, focusColumn, true, false);
		}else if(code == getEditionKey()){
			table.editCellAt(focusRow, focusColumn);
		}
	}
	
	private function recordFocusIndecis(row:Number, column:Number):Void{
		focusRow = row;
		focusColumn = column;
	}
	private function restrictRow(row:Number):Number{
		return Math.max(0, Math.min(table.getRowCount()-1, row));;
	}
	private function restrictColumn(column:Number):Number{
		return Math.max(0, Math.min(table.getColumnCount()-1, column));
	}
	
	private function moveFocus(rDir:Number, cDir:Number):Void{
		var ctrl:Boolean = Key.isDown(getAdditionSelectionKey());
		var shift:Boolean = Key.isDown(getIntervalSelectionKey());
		var rm:ListSelectionModel = table.getSelectionModel();
		var cm:ListSelectionModel = table.getColumnModel().getSelectionModel();
		var anchorRow:Number = rm.getAnchorSelectionIndex();
		var anchorCol:Number = cm.getAnchorSelectionIndex();
		
		var oldRow:Number = restrictRow(focusRow);
		var oldColumn:Number = restrictColumn(focusColumn);
		focusRow += rDir;
		focusRow = restrictRow(focusRow);
		focusColumn += cDir;
		focusColumn = restrictColumn(focusColumn);
		
		if(!ctrl){
			changeSelection(focusRow, focusColumn, ctrl, !ctrl && shift);
		}
		table.ensureCellIsVisible(focusRow, focusColumn);
	}
	
	private var focusRow:Number;
	private var focusColumn:Number;

    public function paintFocus(c:Component, g:Graphics):Void{
    	paintCurrentCellFocus();
    }

    private function paintCurrentCellFocus():Void{
    	paintCellFocusWithRowColumn(focusRow, focusColumn);
    }
    
    private function paintCellFocusWithRowColumn(row:Number, column:Number):Void{
       	var g:Graphics = table.getFocusGraphics();
    	super.paintFocus(table, g);
		var rect:Rectangle = table.getCellRect(row, column, true);
		rect.setLocation(table.getPixelLocationFromLogicLocation(rect.getLocation()));
		g.drawRectangle(new Pen(getDefaultFocusColorOutter(), 2), rect.x, rect.y, rect.width, rect.height);
    }

	//******************************************************************
	//	                         Size Methods
	//******************************************************************

	private function createTableSize(width:Number):Dimension {
		var height:Number = 0;
		var rowCount:Number = table.getRowCount();
		if (rowCount > 0 && table.getColumnCount() > 0) {
			var r:Rectangle = table.getCellRect(rowCount - 1, 0, true);
			height = r.y + r.height;
		}
		height += table.getTableHeader().getPreferredHeight();
		return new Dimension(width, height);
	}
		
    /**
     * Returns the view size.
     */    
	public function getViewSize(table:JTable):Dimension{
		var width:Number = 0;
		var enumeration:Array = table.getColumnModel().getColumns();
		for(var i:Number=0; i<enumeration.length; i++){
			var aColumn:TableColumn = TableColumn(enumeration[i]);
			width = width + aColumn.getPreferredWidth();
		}
		
    	var d:Dimension = createTableSize(width);
    	if(table.getAutoResizeMode() != JTable.AUTO_RESIZE_OFF){
    		d.width = table.getExtentSize().width;
    	}else{
    		d.width = table.getColumnModel().getTotalColumnWidth();
    	}
    	d.height -= table.getTableHeader().getHeight();
    			
		return d;
	}

	/**
	 * Return the minimum size of the table. The minimum height is the
	 * row height times the number of rows.
	 * The minimum width is the sum of the minimum widths of each column.
	 */
	public function getMinimumSize(c:Component):Dimension {
		var width:Number = 0;
		var enumeration:Array = table.getColumnModel().getColumns();
		for(var i:Number=0; i<enumeration.length; i++){
			var aColumn:TableColumn = TableColumn(enumeration[i]);
			width = width + aColumn.getMinWidth();
		}
		return table.getInsets().getOutsideSize(new Dimension(width, 0));
	}

	/**
	 * Return the preferred size of the table. The preferred height is the
	 * row height times the number of rows.
	 * The preferred width is the sum of the preferred widths of each column.
	 */
	public function getPreferredSize(c:Component):Dimension {
		return table.getInsets().getOutsideSize(getViewSize(JTable(c)));
	}

	/**
	 * Return the maximum size of the table. The maximum height is the
	 * row heighttimes the number of rows.
	 * The maximum width is the sum of the maximum widths of each column.
	 */
	public function getMaximumSize(c:Component):Dimension {
		var width:Number = 0;
		var enumeration:Array = table.getColumnModel().getColumns();
		for(var i:Number=0; i<enumeration.length; i++){
			var aColumn:TableColumn = TableColumn(enumeration[i]);
			width = width + aColumn.getMaxWidth();
		}
		return table.getInsets().getOutsideSize(createTableSize(width));
	}	
	
	public function toString():String{
		return "BasicTableUI[]";
	}

}