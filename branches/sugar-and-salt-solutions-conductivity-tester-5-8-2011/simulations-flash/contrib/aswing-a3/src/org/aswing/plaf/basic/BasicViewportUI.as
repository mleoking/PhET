/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{

import org.aswing.*;
import org.aswing.event.*;
import org.aswing.plaf.BaseComponentUI;
import org.aswing.geom.IntPoint;
import flash.ui.Keyboard;
import flash.events.MouseEvent;

/**
 * @private
 */
public class BasicViewportUI extends BaseComponentUI{
	
	protected var viewport:JViewport;
	
	public function BasicViewportUI(){
		super();
	}
	
	override public function installUI(c:Component):void {
		viewport = JViewport(c);
		installDefaults();
		installListeners();
	}

	override public function uninstallUI(c:Component):void {
		viewport = JViewport(c);
		uninstallDefaults();
		uninstallListeners();
	}

    protected function getPropertyPrefix():String {
        return "Viewport.";
    }

	protected function installDefaults():void {
		var pp:String = getPropertyPrefix();
		LookAndFeel.installColorsAndFont(viewport, pp);
		LookAndFeel.installBorderAndBFDecorators(viewport, pp);
		LookAndFeel.installBasicProperties(viewport, pp);
	}

	protected function uninstallDefaults():void {
		LookAndFeel.uninstallBorderAndBFDecorators(viewport);
	}
	
	protected function installListeners():void{
		viewport.addEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onKeyDown);
		viewport.addEventListener(MouseEvent.MOUSE_WHEEL, __onMouseWheel);
	}
	
	protected function uninstallListeners():void{
		viewport.removeEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onKeyDown);
		viewport.removeEventListener(MouseEvent.MOUSE_WHEEL, __onMouseWheel);
	}
	
	private function __onMouseWheel(e:MouseEvent):void{
		if(!(viewport.isEnabled() && viewport.isShowing())){
			return;
		}
    	var viewPos:IntPoint = viewport.getViewPosition();
    	if(e.shiftKey){
    		viewPos.x -= e.delta*viewport.getHorizontalUnitIncrement();
    	}else{
    		viewPos.y -= e.delta*viewport.getVerticalUnitIncrement();
    	}
    	viewport.setViewPosition(viewPos);
	}
	
	private function __onKeyDown(e:FocusKeyEvent):void{
		if(!(viewport.isEnabled() && viewport.isShowing())){
			return;
		}
    	var code:uint = e.keyCode;
    	var viewpos:IntPoint = viewport.getViewPosition();
    	switch(code){
    		case Keyboard.UP:
    			viewpos.move(0, -viewport.getVerticalUnitIncrement());
    			break;
    		case Keyboard.DOWN:
    			viewpos.move(0, viewport.getVerticalUnitIncrement());
    			break;
    		case Keyboard.LEFT:
    			viewpos.move(-viewport.getHorizontalUnitIncrement(), 0);
    			break;
    		case Keyboard.RIGHT:
    			viewpos.move(viewport.getHorizontalUnitIncrement(), 0);
    			break;
    		case Keyboard.PAGE_UP:
    			if(e.shiftKey){
    				viewpos.move(-viewport.getHorizontalBlockIncrement(), 0);
    			}else{
    				viewpos.move(0, -viewport.getVerticalBlockIncrement());
    			}
    			break;
    		case Keyboard.PAGE_DOWN:
    			if(e.shiftKey){
    				viewpos.move(viewport.getHorizontalBlockIncrement(), 0);
    			}else{
    				viewpos.move(0, viewport.getVerticalBlockIncrement());
    			}
    			break;
    		case Keyboard.HOME:
    			viewpos.setLocationXY(0, 0);
    			break;
    		case Keyboard.END:
    			viewpos.setLocationXY(int.MAX_VALUE, int.MAX_VALUE);
    			break;
    		default:
    			return;
    	}
    	viewport.setViewPosition(viewpos);
	}
}
}