/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{

import org.aswing.plaf.*;
import org.aswing.*;
import org.aswing.event.ListItemEvent;
import org.aswing.event.FocusKeyEvent;
import org.aswing.event.AWEvent;
import org.aswing.event.SelectionEvent;
import flash.events.MouseEvent;
import org.aswing.graphics.*;
import org.aswing.geom.*;
import flash.display.Graphics;
import flash.ui.Keyboard;

/**
 * List UI basic imp.
 * @author  iiley
 * @private
 */
public class BasicListUI extends BaseComponentUI{
	
	protected var list:JList;
	
	public function BasicListUI(){
		super();
	}
	
    override public function installUI(c:Component):void {
        list = JList(c);
        installDefaults();
        installListeners();
    }
    
    protected function getPropertyPrefix():String {
        return "List.";
    }
    
    protected function installDefaults():void {
        var pp:String = getPropertyPrefix();
        
        LookAndFeel.installColorsAndFont(list, pp);
        LookAndFeel.installBorderAndBFDecorators(list, pp);
        LookAndFeel.installBasicProperties(list, pp);
        
		var sbg:ASColor = list.getSelectionBackground();
		if (sbg == null || sbg is UIResource) {
			list.setSelectionBackground(getColor(pp+"selectionBackground"));
		}

		var sfg:ASColor = list.getSelectionForeground();
		if (sfg == null || sfg is UIResource) {
			list.setSelectionForeground(getColor(pp+"selectionForeground"));
		}
    }
    
    protected function installListeners():void{
    	list.addEventListener(ListItemEvent.ITEM_CLICK, __onItemClick);
    	list.addEventListener(ListItemEvent.ITEM_MOUSE_DOWN, __onItemMouseDown);
    	list.addEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onKeyDown);
    	list.addEventListener(AWEvent.FOCUS_LOST, __onFocusLost);
    	list.addEventListener(SelectionEvent.LIST_SELECTION_CHANGED, __onSelectionChanged);
    	list.addEventListener(MouseEvent.MOUSE_WHEEL, __onMouseWheel);
    }
	
	override public function uninstallUI(c:Component):void {
        uninstallDefaults();
        uninstallListeners();
    }
    
    protected function uninstallDefaults():void {
        LookAndFeel.uninstallBorderAndBFDecorators(list);
    }
    
    protected function uninstallListeners():void{
    	list.removeEventListener(ListItemEvent.ITEM_CLICK, __onItemClick);
    	list.removeEventListener(ListItemEvent.ITEM_MOUSE_DOWN, __onItemMouseDown);
    	list.removeEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onKeyDown);
    	list.removeEventListener(AWEvent.FOCUS_LOST, __onFocusLost);
    	list.removeEventListener(SelectionEvent.LIST_SELECTION_CHANGED, __onSelectionChanged);
    	list.removeEventListener(MouseEvent.MOUSE_WHEEL, __onMouseWheel);
    }
    
    protected var paintFocusedIndex:int=-1;
    protected var paintFocusedCell:ListCell;
    protected var focusGraphics:Graphics2D;
    protected var focusRectangle:IntRectangle;
    protected var focusGraphicsOwner:Graphics;
    
	override public function paintFocus(c:Component, g:Graphics2D, b:IntRectangle):void{
    	var fm:FocusManager = FocusManager.getManager(list.stage);
    	if(fm){
	    	focusGraphics = g;
	    	focusRectangle = b;
	    	focusGraphicsOwner = fm.moveFocusRectUpperTo(list).graphics;
	    	paintCurrentCellFocus();
    	}
    }
        
    private function paintCurrentCellFocus():void{
    	if(paintFocusedCell != null){
    		paintCellFocus(paintFocusedCell.getCellComponent());
    	}else{
    		super.paintFocus(list, focusGraphics, focusRectangle);
    	}
    }
    
    private function paintCellFocusWithIndex(index:int):void{
    	if(index < 0 || index >= list.getModel().getSize()){
    		return;
    	}
		paintFocusedCell = list.getCellByIndex(index);
		paintFocusedIndex = index;
		if(paintFocusedCell){
			paintCellFocus(paintFocusedCell.getCellComponent());
		}
    }
    
    protected function paintCellFocus(cellComponent:Component):void{
    	if(focusGraphicsOwner)
    		focusGraphicsOwner.clear();
    	super.paintFocus(list, focusGraphics, focusRectangle);
    	super.paintFocus(list, focusGraphics, paintFocusedCell.getCellComponent().getComBounds());
    }
    
    //----------
    private function __onMouseWheel(e:MouseEvent):void{
		if(!list.isEnabled()){
			return;
		}
    	var viewPos:IntPoint = list.getViewPosition();
    	if(e.shiftKey){
    		viewPos.x -= e.delta*list.getHorizontalUnitIncrement();
    	}else{
    		viewPos.y -= e.delta*list.getVerticalUnitIncrement();
    	}
    	list.setViewPosition(viewPos);
    }
    
    private function __onFocusLost(e:AWEvent):void{
    	if(focusGraphicsOwner)
    		focusGraphicsOwner.clear();
    }
    
    private function __onKeyDown(e:FocusKeyEvent):void{
		if(!list.isEnabled()){
			return;
		}
    	var code:uint = e.keyCode;
    	var dir:Number = 0;
    	if(code == Keyboard.UP || code == Keyboard.DOWN || code == Keyboard.SPACE){
    		var fm:FocusManager = FocusManager.getManager(list.stage);
	    	if(fm) fm.setTraversing(true);
    	}
    	if(code == Keyboard.UP){
    		dir = -1;
    	}else if(code == Keyboard.DOWN){
    		dir = 1;
    	}
    	
    	if(paintFocusedIndex == -1){
    		paintFocusedIndex = list.getSelectedIndex();
    	}
    	if(paintFocusedIndex < -1){
    		paintFocusedIndex = -1;
    	}else if(paintFocusedIndex > list.getModel().getSize()){
    		paintFocusedIndex = list.getModel().getSize();
    	}
    	var index:int = paintFocusedIndex + dir;    	
    	if(code == Keyboard.HOME){
    		index = 0;
    	}else if(code == Keyboard.END){
    		index = list.getModel().getSize() - 1;
    	}
    	if(index < 0 || index >= list.getModel().getSize()){
    		return;
    	}
    	if(dir != 0 || (code == Keyboard.HOME || code == Keyboard.END)){
		    list.ensureIndexIsVisible(index);
		    list.validate();
    		if(e.shiftKey){
				var archor:int = list.getAnchorSelectionIndex();
				if(archor < 0){
					archor = index;
				}
				list.setSelectionInterval(archor, index, false);
    		}else if(e.ctrlKey){
    		}else{
		    	list.setSelectionInterval(index, index, false);
    		}
    		//this make sure paintFocusedCell rememberd
    		paintCellFocusWithIndex(index);
    	}else{
    		if(code == Keyboard.SPACE){
		    	list.addSelectionInterval(index, index, false);
    			//this make sure paintFocusedCell rememberd
    			paintCellFocusWithIndex(index);
		    	list.ensureIndexIsVisible(index);
    		}
    	}
    }
    private function __onSelectionChanged(e:SelectionEvent):void{
    	var fm:FocusManager = FocusManager.getManager(list.stage);
    	if(fm != null && fm.isTraversing() && list.isFocusOwner()){
    		if(focusGraphics == null){
    			list.paintFocusRect(true);
    		}
    		paintCellFocusWithIndex(list.getLeadSelectionIndex());
    	}
    }
    
	//------------------------------------------------------------------------
	//                 ---------  Selection ---------
	//------------------------------------------------------------------------
    
    private var pressedIndex:Number;
    private var pressedCtrl:Boolean;
    private var pressedShift:Boolean;
    private var doSelectionWhenRelease:Boolean;    
    
    private function __onItemMouseDown(e:ListItemEvent):void{
		var index:int = list.getItemIndexByCell(e.getCell());
		pressedIndex = index;
		pressedCtrl = e.ctrlKey;
		pressedShift = e.shiftKey;
		doSelectionWhenRelease = false;
		
		if(list.getSelectionMode() == JList.MULTIPLE_SELECTION){
			if(list.isSelectedIndex(index)){
				doSelectionWhenRelease = true;
			}else{
				doSelection();
			}
		}else{
			list.setSelectionInterval(index, index, false);
		}
    }
    
    private function doSelection():void{
    	var index:int = pressedIndex;
		if(pressedShift){
			var archor:int = list.getAnchorSelectionIndex();
			if(archor < 0){
				archor = index;
			}
			list.setSelectionInterval(archor, index, false);
		}else if(pressedCtrl){
			if(!list.isSelectedIndex(index)){
				list.addSelectionInterval(index, index, false);
			}else{
				list.removeSelectionInterval(index, index, false);
			}
		}else{
			list.setSelectionInterval(index, index, false);
		}
    }
    
    private function __onItemClick(e:ListItemEvent):void{
    	if(doSelectionWhenRelease){
    		doSelection();
    		doSelectionWhenRelease = false;
    	}
    }
    	
	
}
}