/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{
	
import org.aswing.plaf.BaseComponentUI;
import org.aswing.*;
import org.aswing.event.*;

/**
 * ToolBar basic ui imp.
 * @author iiley
 * @private
 */
public class BasicToolBarUI extends BaseComponentUI{
	
	protected var bar:Container;
	
	public function BasicToolBarUI(){
		super();
	}
	
    protected function getPropertyPrefix():String {
        return "ToolBar.";
    }
    
	override public function installUI(c:Component):void{
		bar = Container(c);
		installDefaults();
		installComponents();
		installListeners();
	}
    
	override public function uninstallUI(c:Component):void{
		bar = Container(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
 	}
 	
 	protected function installDefaults():void{
        var pp:String = getPropertyPrefix();
        
        LookAndFeel.installColorsAndFont(bar, pp);
        LookAndFeel.installBorderAndBFDecorators(bar, pp);
        LookAndFeel.installBasicProperties(bar, pp);
 	}
	
 	protected function uninstallDefaults():void{
 		LookAndFeel.uninstallBorderAndBFDecorators(bar);
 	}
 	
 	protected function installComponents():void{
 		for(var i:int=0; i<bar.getComponentCount(); i++){
 			adaptChild(bar.getComponent(i));
 		}
 	}
	
 	protected function uninstallComponents():void{
 		for(var i:int=0; i<bar.getComponentCount(); i++){
 			unadaptChild(bar.getComponent(i));
 		}
 	}
 	
 	protected function installListeners():void{
 		bar.addEventListener(ContainerEvent.COM_ADDED, __onComAdded);
 		bar.addEventListener(ContainerEvent.COM_REMOVED, __onComRemoved);
 	}
	
 	protected function uninstallListeners():void{
 		bar.removeEventListener(ContainerEvent.COM_ADDED, __onComAdded);
 		bar.removeEventListener(ContainerEvent.COM_REMOVED, __onComRemoved);
 	}
 	
 	protected function adaptChild(c:Component):void{
    	var btn:AbstractButton = c as AbstractButton;
    	if(btn != null){
    		var bg:GroundDecorator = btn.getBackgroundDecorator();
    		if(bg != null){
    			var bgAdapter:ToolBarButtonBgAdapter = new ToolBarButtonBgAdapter(bg);
    			btn.setBackgroundDecorator(bgAdapter);
    		}
    		btn.addEventListener(PropertyChangeEvent.PROPERTY_CHANGE, __propertyChanged);
    	}
 	}
 	
 	protected function unadaptChild(c:Component):void{
    	var btn:AbstractButton = c as AbstractButton;
    	if(btn != null){
    		btn.removeEventListener(PropertyChangeEvent.PROPERTY_CHANGE, __propertyChanged);
    		var bg:ToolBarButtonBgAdapter = btn.getBackgroundDecorator() as ToolBarButtonBgAdapter;
    		if(bg != null){
    			btn.setBackgroundDecorator(bg.getOriginalBg());
    		}
    	}
 	}
    
    //------------------------------------------------
 	
 	private function __propertyChanged(e:PropertyChangeEvent):void{
 		if(e.getPropertyName() == "backgroundDecorator"){
 			var btn:AbstractButton = e.target as AbstractButton;
 			//var oldG:GroundDecorator = e.getOldValue();
 			var newG:GroundDecorator = e.getNewValue();
 			if(!(newG is ToolBarButtonBgAdapter)){
    			var bgAdapter:ToolBarButtonBgAdapter = new ToolBarButtonBgAdapter(newG);
    			btn.setBackgroundDecorator(bgAdapter);
 			}
 		}
 	}
 	
    private function __onComAdded(e:ContainerEvent):void{
    	adaptChild(e.getChild());
    }
    
    private function __onComRemoved(e:ContainerEvent):void{
    	unadaptChild(e.getChild());
    }
}
}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//                 BG Decorator Adapter
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

import org.aswing.graphics.Graphics2D;
import org.aswing.GroundDecorator;
import org.aswing.geom.IntRectangle;
import org.aswing.Component;
import flash.display.DisplayObject;
import org.aswing.AbstractButton;
import org.aswing.plaf.UIResource;

/**
 * This background adapter will invisible the original background, and visible it 
 * only when button be rollover.
 * @author iiley
 */
class ToolBarButtonBgAdapter implements GroundDecorator, UIResource{
	
	private var originalBg:GroundDecorator;
	
	public function ToolBarButtonBgAdapter(originalBg:GroundDecorator){
		this.originalBg = originalBg;
	}
	
	public function getOriginalBg():GroundDecorator{
		return originalBg;
	}
	
	public function updateDecorator(c:Component, g:Graphics2D, bounds:IntRectangle):void{
		if(originalBg == null){
			return;
		}
		var btn:AbstractButton = c as AbstractButton;
		var needPaint:Boolean = false;
		if(btn == null || btn.getModel().isArmed() || btn.isSelected() 
			|| (btn.getModel().isRollOver() && !btn.getModel().isPressed())){
			needPaint = true;
		}
		var dis:DisplayObject = getDisplay(c);
		if(dis != null) dis.visible = needPaint;
		if(needPaint){
			originalBg.updateDecorator(c, g, bounds);
		}
	}
	
	public function getDisplay(c:Component):DisplayObject{
		if(originalBg == null){
			return null;
		}
		return originalBg.getDisplay(c);
	}
	
}