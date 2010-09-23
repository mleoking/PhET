/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic { 

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.MouseEvent;

import org.aswing.*;
import org.aswing.event.ReleaseEvent;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.*;
import org.aswing.table.*;

/**
 * @author iiley
 * @private
 */
public class BasicTableHeaderUI extends BaseComponentUI{
	
	private var header:JTableHeader;
	private var cells:Array;
	private var mouseXOffset:int;
	private var resizeCursor:DisplayObject;
	private var resizing:Boolean;
	
	public function BasicTableHeaderUI() {
		super();
		mouseXOffset = 0;
		resizing = false;
		resizeCursor = Cursor.createCursor(Cursor.H_MOVE_CURSOR);
	}
	
	protected function getPropertyPrefix():String {
		return "TableHeader.";
	}
	
	override public function installUI(c:Component):void {
		header = JTableHeader(c);
		installDefaults();
		installComponents();
		installListeners();
	}
	
	protected function installDefaults():void {
		var pp:String = getPropertyPrefix();
		LookAndFeel.installColorsAndFont(header, pp);
		LookAndFeel.installBorderAndBFDecorators(header, pp);
		LookAndFeel.installBasicProperties(header, pp);
		header.setOpaque(true);
	}
	
	protected function installComponents():void{
		cells = new Array();
	}
	
	protected function installListeners():void{
		header.addEventListener(MouseEvent.ROLL_OVER, __onHeaderRollover);
		header.addEventListener(MouseEvent.ROLL_OUT, __onHeaderRollout);
		header.addEventListener(MouseEvent.MOUSE_DOWN, __onHeaderPressed);
		header.addEventListener(ReleaseEvent.RELEASE, __onHeaderReleased);
		header.addEventListener(Event.REMOVED_FROM_STAGE, __headerRemovedFromStage);
	}
	
	override public function uninstallUI(c:Component):void {
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
	}
	
	protected function uninstallDefaults():void {
		LookAndFeel.uninstallBorderAndBFDecorators(header);
	}
	
	protected function uninstallComponents():void{
		removeAllCells();
		cells = null;
	}
	
	protected function uninstallListeners():void{
		header.removeEventListener(MouseEvent.ROLL_OVER, __onHeaderRollover);
		header.removeEventListener(MouseEvent.ROLL_OUT, __onHeaderRollout);
		header.removeEventListener(MouseEvent.MOUSE_DOWN, __onHeaderPressed);
		header.removeEventListener(ReleaseEvent.RELEASE, __onHeaderReleased);
		header.removeEventListener(Event.REMOVED_FROM_STAGE, __headerRemovedFromStage);
	}
	
	//*************************************************
	//			 Event Handlers
	//*************************************************
	
	private function __headerRemovedFromStage(e:Event):void{
		header.stage.removeEventListener(MouseEvent.MOUSE_MOVE, 
			__onRollOverMouseMoving);
		header.stage.removeEventListener(MouseEvent.MOUSE_MOVE, 
			__onMouseMoving);
	}
	
	private function __onHeaderRollover(e:MouseEvent):void{
		if(!e.buttonDown){
			if(header.stage){
				header.stage.addEventListener(MouseEvent.MOUSE_MOVE, 
					__onRollOverMouseMoving, false, 0, true);
			}
		}
	}
	
	private function __onHeaderRollout(e:MouseEvent):void{
		if(e == null || !e.buttonDown){
			CursorManager.getManager(header.stage).hideCustomCursor(resizeCursor);
			if(header.stage){
				header.stage.removeEventListener(MouseEvent.MOUSE_MOVE, 
					__onRollOverMouseMoving);
			}
		}
	}
	
	private function __onRollOverMouseMoving(e:Event):void{
		if(resizing){
			return;
		}
		var p:IntPoint = header.getMousePosition();
		if(header.getTable().hitTestMouse() && 
			canResize(getResizingColumn(p, header.columnAtPoint(p)))){
			CursorManager.getManager(header.stage).showCustomCursor(resizeCursor, true);
		}else{
			CursorManager.getManager(header.stage).hideCustomCursor(resizeCursor);
		}
	}
	
	private function __onHeaderPressed(e:Event):void{
		header.setResizingColumn(null);
		if(header.getTable().getCellEditor() != null){
			header.getTable().getCellEditor().cancelCellEditing();
		}
		
		var p:IntPoint = header.getMousePosition();
		//First find which header cell was hit
		var index:int = header.columnAtPoint(p);
		if(index >= 0){
			//The last 3 pixels + 3 pixels of next column are for resizing
			var resizingColumn:TableColumn = getResizingColumn(p, index);
			if (canResize(resizingColumn)) {
				header.setResizingColumn(resizingColumn);
				mouseXOffset = p.x - resizingColumn.getWidth();
				if(header.stage){
					header.stage.addEventListener(MouseEvent.MOUSE_MOVE, 
						__onMouseMoving, false, 0, true);
				}
				resizing = true;
			}
		}
	}
	
	private function __onHeaderReleased(e:Event):void{
		if(header.stage){
			header.stage.removeEventListener(MouseEvent.MOUSE_MOVE, 
				__onMouseMoving);
		}
		header.setResizingColumn(null);
		resizing = false;
		__onRollOverMouseMoving(null);
	}
		
	private function __onMouseMoving(e:MouseEvent):void{
		var mouseX:int = header.getMousePosition().x;
		var resizingColumn:TableColumn = header.getResizingColumn();
		if (resizingColumn != null) {
			var newWidth:int;
			newWidth = mouseX - mouseXOffset;
			resizingColumn.setWidth(newWidth);
			e.updateAfterEvent();
		}
	}
	
	private function canResize(column:TableColumn):Boolean {
		return (column != null) && header.getResizingAllowed()
			&& column.getResizable();
	}
	
	private function getResizingColumn(p:IntPoint, column:int):TableColumn {
		if (column < 0) {
			return null;
		}
		var r:IntRectangle = header.getHeaderRect(column);
		r.grow(-3, 0);
		//if r contains p
		if ((p.x > r.x && p.x < r.x+r.width)) {
			return null;
		}
		var midPoint:int = r.x + r.width / 2;
		var columnIndex:int;
		columnIndex = (p.x < midPoint) ? column - 1 : column;
		if (columnIndex == -1) {
			return null;
		}
		return header.getColumnModel().getColumn(columnIndex);
	}
	
	private function getHeaderRenderer(columnIndex:int):TableCellFactory {
		var aColumn:TableColumn = header.getColumnModel().getColumn(columnIndex);
		var renderer:TableCellFactory = aColumn.getHeaderCellFactory();
		if (renderer == null) {
			renderer = header.getDefaultRenderer();
		}
		return renderer;
	}	
	
	override protected function paintBackGround(c:Component, g:Graphics2D, b:IntRectangle):void{
		if(c.isOpaque()){
	 		var bgColor:ASColor = (c.getBackground() == null ? ASColor.WHITE : c.getBackground());
	    	BasicGraphicsUtils.drawControlBackground(g, b, bgColor, Math.PI/2);
		}
	}
	
	override public function paint(c:Component, g:Graphics2D, b:IntRectangle):void{
		super.paint(c, g, b);
		if (header.getColumnModel().getColumnCount() <= 0) {
			return;
		}
		synCreateCellInstances();
		
		var cm:TableColumnModel = header.getColumnModel();
		var cMin:Number = 0;
		var cMax:Number = cm.getColumnCount() - 1;
		var columnWidth:Number;
		var cellRect:IntRectangle = header.getHeaderRect(cMin);
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
	private function synCreateCellInstances():void{
		var columnCount:int = header.getColumnModel().getColumnCount();
		var i:int;
		if(lastColumnCellFactories==null || lastColumnCellFactories.length != columnCount){
			removeAllCells();
		}else{
			for(i=0; i<columnCount; i++){
				if(lastColumnCellFactories[i] != getHeaderRenderer(i)){
					removeAllCells();
					break;
				}
			}
		}
		if(cells.length == 0){
			lastColumnCellFactories = new Array(columnCount);
			for(i=0; i<columnCount; i++){
				var factory:TableCellFactory = getHeaderRenderer(i);
				lastColumnCellFactories[i] = factory;
				var cell:TableCell = factory.createNewCell(false);
				header.append(cell.getCellComponent());
				setCellComponentProperties(cell.getCellComponent());
				cells.push(cell);
			}
		}
	}
	
	private static function setCellComponentProperties(com:Component):void{
		com.setFocusable(false);
		if(com is Container){
			var con:Container = Container(com);
			for(var i:int=0; i<con.getComponentCount(); i++){
				setCellComponentProperties(con.getComponent(i));
			}
		}
	}	
	
	private function removeAllCells():void{
		for(var i:int=0; i<cells.length; i++){
			var cell:TableCell = TableCell(cells[i]);
			cell.getCellComponent().removeFromContainer();
		}
		cells = new Array();
	}
	//******************************************************************
	//							 Size Methods
	//******************************************************************
	private function createHeaderSize(width:int):IntDimension {
		return header.getInsets().getOutsideSize(new IntDimension(width, header.getRowHeight()));
	}

	/**
	 * Return the minimum size of the table. The minimum height is the
	 * row height times the number of rows.
	 * The minimum width is the sum of the minimum widths of each column.
	 */
	override public function getMinimumSize(c:Component):IntDimension {
		var width:int = 0;
//		var enumeration:Array = header.getColumnModel().getColumns();
//		for(var i:int=0; i<enumeration.length; i++){
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
	override public function getPreferredSize(c:Component):IntDimension {
		var width:int = 0;
		var enumeration:Array = header.getColumnModel().getColumns();
		for(var i:int=0; i<enumeration.length; i++){
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
	override public function getMaximumSize(c:Component):IntDimension {
		var width:int = 100000;
//		var enumeration:Array = header.getColumnModel().getColumns();
//		for(var i:int=0; i<enumeration.length; i++){
//			var aColumn:TableColumn = TableColumn(enumeration[i]);
//			width = width + aColumn.getMaxWidth();
//		}
		return createHeaderSize(width);
	}	
	
	public function toString():String{
		return "BasicTableHeaderUI[]";
	}
}
}