/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.Container;
import org.aswing.Cursor;
import org.aswing.CursorManager;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.LookAndFeel;
import org.aswing.plaf.TableHeaderUI;
import org.aswing.table.JTableHeader;
import org.aswing.table.TableCell;
import org.aswing.table.TableCellFactory;
import org.aswing.table.TableColumn;
import org.aswing.table.TableColumnModel;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicTableHeaderUI extends TableHeaderUI {
	
	private var header:JTableHeader;
	private var cells:Array;
	private var headerListener:Object;
	private var mouseMoveListener:Object;
	private var cursorTriggerListener:Object;
	private var mouseXOffset:Number;
	private var resizeCursor:Cursor;
	private var resizing:Boolean;
	
	public function BasicTableHeaderUI() {
		super();
		mouseXOffset = 0;
		resizing = false;
		resizeCursor = new Cursor(Cursor.H_RESIZE_CURSOR);
	}
	
    public function installUI(c:Component):Void {
        header = JTableHeader(c);
        installDefaults();
        installComponents();
        installListeners();
    }
    
    private function installDefaults():Void {
    	var pp:String = "TableHeader.";
        LookAndFeel.installColorsAndFont(header, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(header, pp+"border");
        LookAndFeel.installBasicProperties(header, pp);
        header.setOpaque(true);
    }
    private function installComponents():Void{
    	cells = new Array();
    }
    private function installListeners():Void{
    	mouseMoveListener = {onMouseMove:Delegate.create(this, __onMouseMoving)};
    	cursorTriggerListener = {onMouseMove:Delegate.create(this, __onRollOverMouseMoving)};
    	headerListener = new Object();
    	headerListener[Component.ON_ROLLOVER] = Delegate.create(this, __onHeaderRollover);
    	headerListener[Component.ON_ROLLOUT] = Delegate.create(this, __onHeaderRollout);
    	headerListener[Component.ON_PRESS] = Delegate.create(this, __onHeaderPressed);
    	headerListener[Component.ON_RELEASE] = Delegate.create(this, __onHeaderReleased);
    	headerListener[Component.ON_RELEASEOUTSIDE] = Delegate.create(this, __onHeaderReleasedOutSide);
    	header.addEventListener(headerListener);
    }
	
	public function uninstallUI(c:Component):Void {
        uninstallDefaults();
        uninstallComponents();
        uninstallListeners();
    }
    
    private function uninstallDefaults():Void {
        LookAndFeel.uninstallBorder(header);
    }
    private function uninstallComponents():Void{
    	removeAllCells();
    	cells = null;
    }
    private function uninstallListeners():Void{
    	header.removeEventListener(headerListener);
		Mouse.removeListener(mouseMoveListener);
    }		
    
    //*************************************************
    //             Event Handlers
    //*************************************************
    private function __onHeaderRollover():Void{
    	Mouse.removeListener(cursorTriggerListener);
    	Mouse.addListener(cursorTriggerListener);
    }
    
    private function __onHeaderRollout():Void{
    	CursorManager.hideCustomCursor();
    	Mouse.removeListener(cursorTriggerListener);
    }
    
    private function __onRollOverMouseMoving():Void{
    	if(resizing){
    		return;
    	}
    	var p:Point = header.getMousePosition();
    	if(canResize(getResizingColumn(p, header.columnAtPoint(p)))){
    		if(!CursorManager.isCustomCursorShowing()){
    			CursorManager.showCustomCursor(resizeCursor, header);
    		}
    	}else{
    		if(CursorManager.isCustomCursorShowing()){
    			CursorManager.hideCustomCursor();
    		}
    	}
    }
    
    private function __onHeaderPressed():Void{
    	header.setResizingColumn(null);
    	if(header.getTable().getCellEditor() != null){
    		header.getTable().getCellEditor().cancelCellEditing();
    	}
    	
    	var p:Point = header.getMousePosition();
		//First find which header cell was hit
		var columnModel:TableColumnModel = header.getColumnModel();
		var index:Number = header.columnAtPoint(p);
		if(index >= 0){
			//The last 3 pixels + 3 pixels of next column are for resizing
			var resizingColumn:TableColumn = getResizingColumn(p, index);
			if (canResize(resizingColumn)) {
				header.setResizingColumn(resizingColumn);
				mouseXOffset = p.x - resizingColumn.getWidth();
				Mouse.removeListener(mouseMoveListener);
				Mouse.addListener(mouseMoveListener);
				resizing = true;
			}
		}
    }
    
    private function __onHeaderReleased():Void{
		Mouse.removeListener(mouseMoveListener);
    	header.setResizingColumn(null);
    	resizing = false;
    }
    
    private function __onHeaderReleasedOutSide():Void{
    	__onHeaderReleased();
    	__onHeaderRollout();
    }
    
    private function __onMouseMoving():Void{
    	var mouseX:Number = header.getMousePosition().x;
    	var resizingColumn:TableColumn = header.getResizingColumn();
		if (resizingColumn != null) {
			var oldWidth:Number = resizingColumn.getWidth();
			var newWidth:Number;
			newWidth = mouseX - mouseXOffset;
			resizingColumn.setWidth(newWidth);
			updateAfterEvent();
		}
    }
    
	private function canResize(column:TableColumn):Boolean {
		return (column != null) && header.getResizingAllowed()
			&& column.getResizable();
	}
	private function getResizingColumn(p:Point, column:Number):TableColumn {
		if (column < 0) {
			return null;
		}
		var r:Rectangle = header.getHeaderRect(column);
		r.grow(-3, 0);
		//if r contains p
		if ((p.x > r.x && p.x < r.x+r.width) && (p.y > r.y && p.y < r.y+r.height)) {
			return null;
		}
		var midPoint:Number = r.x + r.width / 2;
		var columnIndex:Number;
		columnIndex = (p.x < midPoint) ? column - 1 : column;
		if (columnIndex == -1) {
			return null;
		}
		return header.getColumnModel().getColumn(columnIndex);
	}
    
	private function getHeaderRenderer(columnIndex:Number):TableCellFactory {
		var aColumn:TableColumn = header.getColumnModel().getColumn(columnIndex);
		var renderer:TableCellFactory = aColumn.getHeaderCellFactory();
		if (renderer == null) {
			renderer = header.getDefaultRenderer();
		}
		return renderer;;
	}    
	
    public function paint(c:Component, g:Graphics, b:Rectangle):Void{
    	super.paint(c, g, b);
		if (header.getColumnModel().getColumnCount() <= 0) {
			return;
		}
		synCreateCellInstances();
		
		var cm:TableColumnModel = header.getColumnModel();
		var cMin:Number = 0;
		var cMax:Number = cm.getColumnCount() - 1;
		var columnWidth:Number;
		var cellRect:Rectangle = header.getHeaderRect(cMin);
		cellRect.x += header.getTable().getColumnModel().getColumnMargin()/2;
		var aColumn:TableColumn;
		for (var column:Number = cMin; column <= cMax; column++) {
			aColumn = cm.getColumn(column);
			columnWidth = aColumn.getWidth();
			cellRect.width = columnWidth;
			var cell:TableCell = cells[column];
			cell.setCellValue(aColumn.getHeaderValue());
			cell.setTableCellStatus(header.getTable(), false, -1, column);
			cell.getCellComponent().setBounds(cellRect);
			cell.getCellComponent().setVisible(true);
			cell.getCellComponent().validate();
			cellRect.x += columnWidth;
		}
    }
    
    private var lastColumnCellFactories:Array;
    private function synCreateCellInstances():Void{
    	var columnCount:Number = header.getColumnModel().getColumnCount();
    	if(lastColumnCellFactories.length != columnCount){
    		removeAllCells();
    	}else{
    		for(var i:Number=0; i<columnCount; i++){
    			if(lastColumnCellFactories[i] != getHeaderRenderer(i)){
    				removeAllCells();
    				break;
    			}
    		}
    	}
    	if(cells.length == 0){
    		lastColumnCellFactories = new Array(columnCount);
    		for(var i:Number=0; i<columnCount; i++){
    			var factory:TableCellFactory = getHeaderRenderer(i);
    			lastColumnCellFactories[i] = factory;
    			var cell:TableCell = factory.createNewCell();
    			header.append(cell.getCellComponent());
    			setCellComponentProperties(cell.getCellComponent());
    			cells.push(cell);
    		}
    	}
    }
    
	private static function setCellComponentProperties(com:Component):Void{
		com.setFocusable(false);
		com.setTriggerEnabled(false);
		if(com instanceof Container){
			var con:Container = Container(com);
			for(var i:Number=0; i<con.getComponentCount(); i++){
				setCellComponentProperties(con.getComponent(i));
			}
		}
	}    
    
    private function removeAllCells():Void{
    	for(var i:Number=0; i<cells.length; i++){
    		var cell:TableCell = TableCell(cells[i]);
    		cell.getCellComponent().removeFromContainer();
    	}
    	cells = new Array();
    }
    //******************************************************************
	//	                         Size Methods
	//******************************************************************
	private function createHeaderSize(width:Number):Dimension {
		return header.getInsets().getOutsideSize(new Dimension(width, header.getRowHeight()));
	}

	/**
	 * Return the minimum size of the table. The minimum height is the
	 * row height times the number of rows.
	 * The minimum width is the sum of the minimum widths of each column.
	 */
	public function getMinimumSize(c:Component):Dimension {
		var width:Number = 0;
//		var enumeration:Array = header.getColumnModel().getColumns();
//		for(var i:Number=0; i<enumeration.length; i++){
//			var aColumn:TableColumn = TableColumn(enumeration[i]);
//			width = width + aColumn.getMinWidth();
//		}
		return createHeaderSize(width);
	}

	/**
	 * Return the preferred size of the table. The preferred height is the
	 * row height times the number of rows.
	 * The preferred width is the sum of the preferred widths of each column.
	 */
	public function getPreferredSize(c:Component):Dimension {
		var width:Number = 0;
		var enumeration:Array = header.getColumnModel().getColumns();
		for(var i:Number=0; i<enumeration.length; i++){
			var aColumn:TableColumn = TableColumn(enumeration[i]);
			width = width + aColumn.getPreferredWidth();
		}
		return createHeaderSize(width);
	}

	/**
	 * Return the maximum size of the table. The maximum height is the
	 * row heighttimes the number of rows.
	 * The maximum width is the sum of the maximum widths of each column.
	 */
	public function getMaximumSize(c:Component):Dimension {
		var width:Number = Number.MAX_VALUE;
//		var enumeration:Array = header.getColumnModel().getColumns();
//		for(var i:Number=0; i<enumeration.length; i++){
//			var aColumn:TableColumn = TableColumn(enumeration[i]);
//			width = width + aColumn.getMaxWidth();
//		}
		return createHeaderSize(width);
	}	
	
	public function toString():String{
		return "BasicTableHeaderUI[]";
	}
}