/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.FocusManager;
import org.aswing.geom.Point;
import org.aswing.graphics.Graphics;
import org.aswing.overflow.JList;
import org.aswing.ListCell;
import org.aswing.LookAndFeel;
import org.aswing.plaf.ListUI;
import org.aswing.plaf.UIResource;
import org.aswing.UIManager;
import org.aswing.util.Delegate;

/**
 * @author firdosh, iiley
 */
class org.aswing.plaf.basic.BasicListUI extends ListUI{
	
	private var list:JList;
	private var listListener:Object;
	
	public function BasicListUI(){
		super();
	}
	
    public function installUI(c:Component):Void {
        list = JList(c);
        installDefaults(list);
        installListeners(list);
    }
    
    private function installDefaults(c:JList):Void {
    	var pp:String = "List.";
        LookAndFeel.installColorsAndFont(c, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(c, "List.border");
        LookAndFeel.installBasicProperties(c, pp);
        
		var sbg:ASColor = list.getSelectionBackground();
		if (sbg === undefined || sbg instanceof UIResource) {
			list.setSelectionBackground(UIManager.getColor("List.selectionBackground"));
		}

		var sfg:ASColor = list.getSelectionForeground();
		if (sfg === undefined || sfg instanceof UIResource) {
			list.setSelectionForeground(UIManager.getColor("List.selectionForeground"));
		}
    }
    private function installListeners(c:JList):Void{
    	listListener = new Object();
    	listListener[JList.ON_ITEM_PRESS] = Delegate.create(this, __onItemPress);
    	listListener[JList.ON_ITEM_RELEASE] = Delegate.create(this, __onItemRelease);
    	listListener[JList.ON_KEY_DOWN]   = Delegate.create(this, __onKeyDown);
    	listListener[JList.ON_FOCUS_LOST] = Delegate.create(this, __onFocusLost);
    	listListener[JList.ON_SELECTION_CHANGED] = Delegate.create(this, __onSelectionChanged);
    	listListener[JList.ON_MOUSE_WHEEL] = Delegate.create(this, __onMouseWheel);
    	list.addEventListener(listListener);
    }
	
	public function uninstallUI(c:Component):Void {
        var p:JList = JList(c);
        uninstallDefaults(p);
        uninstallListeners(p);
    }
    
    private function uninstallDefaults(p:JList):Void {
        LookAndFeel.uninstallBorder(p);
    }
    private function uninstallListeners(p:JList):Void{
    	list.removeEventListener(listListener);
    }
    
    private var paintFocusedIndex:Number;
    private var paintFocusedCell:ListCell;
    public function paintFocus(c:Component, g:Graphics):Void{
    	super.paintFocus(c, g);
    	paintCurrentCellFocus();
    }
	public function clearFocus(c:Component):Void{
		super.clearFocus(c);
		clearCellFocusGraphics();
	}
    
    private function paintCurrentCellFocus():Void{
    	paintCellFocus(paintFocusedCell.getCellComponent());
    }
    
    private function paintCellFocusWithIndex(index:Number):Void{
    	clearCellFocusGraphics();
    	if(index < 0 || index >= list.getModel().getSize()){
    		return;
    	}
		paintFocusedCell = list.getCellByIndex(index);
		paintFocusedIndex = index;
		if(list.isFocusOwner() && FocusManager.getCurrentManager().isTraversing()){
			paintCellFocus(paintFocusedCell.getCellComponent());
		}
    }
    
    private function paintCellFocus(cellComponent:Component):Void{
    	super.paintFocus(cellComponent, cellComponent.getFocusGraphics());
    }
    
    private function clearCellFocusGraphics():Void{
    	paintFocusedCell.getCellComponent().clearFocusGraphics();
    }
    
    private function getIntervalSelectionKey():Number{
    	return Key.SHIFT;
    }
    private function getAdditionSelectionKey():Number{
    	return Key.CONTROL;
    }
    //----------
    private function __onMouseWheel(source:JList, delta:Number):Void{
		if(!list.isEnabled()){
			return;
		}
    	var viewPos:Point = list.getViewPosition();
    	viewPos.y -= delta*list.getVerticalUnitIncrement();
    	list.setViewPosition(viewPos);
    }
    private function __onFocusLost():Void{
    	clearCellFocusGraphics();
    }
    private function __onKeyDown():Void{
		if(!list.isEnabled()){
			return;
		}
    	var code:Number = Key.getCode();
    	var dir:Number = 0;
    	if(code == Key.UP || code == Key.DOWN || code == Key.SPACE){
	    	FocusManager.getCurrentManager().setTraversing(true);
    	}
    	if(code == Key.UP){
    		dir = -1;
    	}else if(code == Key.DOWN){
    		dir = 1;
    	}
    	
    	if(paintFocusedIndex == undefined){
    		paintFocusedIndex = list.getSelectedIndex();
    	}
    	if(paintFocusedIndex < -1){
    		paintFocusedIndex = -1;
    	}else if(paintFocusedIndex > list.getModel().getSize()){
    		paintFocusedIndex = list.getModel().getSize();
    	}
    	var index:Number = paintFocusedIndex + dir;    	
    	if(code == Key.HOME){
    		index = 0;
    	}else if(code == Key.END){
    		index = list.getModel().getSize() - 1;
    	}
    	if(index < 0 || index >= list.getModel().getSize()){
    		return;
    	}
    	if(dir != 0 || (code == Key.HOME || code == Key.END)){
    		if(Key.isDown(getIntervalSelectionKey())){
				var archor:Number = list.getAnchorSelectionIndex();
				if(archor < 0){
					archor = index;
				}
				list.setSelectionInterval(archor, index);
    		}else if(Key.isDown(getAdditionSelectionKey())){
    		}else{
		    	list.setSelectionInterval(index, index);
    		}
    		//this make sure paintFocusedCell rememberd
    		paintCellFocusWithIndex(index);
		    list.ensureIndexIsVisible(index);
    	}else{
    		if(code == Key.SPACE){
		    	list.addSelectionInterval(index, index);
    			//this make sure paintFocusedCell rememberd
    			paintCellFocusWithIndex(index);
		    	list.ensureIndexIsVisible(index);
    		}
    	}
    }
    private function __onSelectionChanged():Void{
    	paintCellFocusWithIndex(list.getLeadSelectionIndex());
    }
    
	//------------------------------------------------------------------------
	//                 ---------  Selection ---------
	//------------------------------------------------------------------------
    
    private var pressedIndex:Number;
    private var pressedCtrl:Boolean;
    private var pressedShift:Boolean;
    private var doSelectionWhenRelease:Boolean;    
    
    private function __onItemPress(source:JList, value:Object, cell:ListCell):Void{
		var index:Number = list.getItemIndexByCell(cell);
		pressedIndex = index;
		pressedCtrl = Key.isDown(getAdditionSelectionKey());
		pressedShift = Key.isDown(getIntervalSelectionKey());
		doSelectionWhenRelease = false;
		
		if(list.getSelectionMode() == JList.MULTIPLE_SELECTION){
			if(list.isSelectedIndex(index)){
				doSelectionWhenRelease = true;
			}else{
				doSelection();
			}
		}else{
			list.setSelectionInterval(index, index);
		}
    }
    
    private function doSelection():Void{
    	var index:Number = pressedIndex;
		if(pressedShift){
			var archor:Number = list.getAnchorSelectionIndex();
			if(archor < 0){
				archor = index;
			}
			list.setSelectionInterval(archor, index);
		}else if(pressedCtrl){
			if(!list.isSelectedIndex(index)){
				list.addSelectionInterval(index, index);
			}else{
				list.removeSelectionInterval(index, index);
			}
		}else{
			list.setSelectionInterval(index, index);
		}    	
    }
    
    private function __onItemRelease(source:JList, value:Object, cell:ListCell):Void{
    	if(doSelectionWhenRelease){
    		doSelection();
    		doSelectionWhenRelease = false;
    	}
    }
    	
}