/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{
	
import org.aswing.*;
import org.aswing.geom.*;
import org.aswing.graphics.*;
	
/**
 * Basic RadioButton implementation.
 * @author iiley
 * @private
 */	
public class BasicRadioButtonUI extends BasicToggleButtonUI{
	
	protected var defaultIcon:Icon;
	
	public function BasicRadioButtonUI(){
		super();
	}
	
	override protected function installDefaults(b:AbstractButton):void{
		super.installDefaults(b);
		defaultIcon = getIcon(getPropertyPrefix() + "icon");
	}
	
	override protected function uninstallDefaults(b:AbstractButton):void{
		super.uninstallDefaults(b);
		if(defaultIcon.getDisplay(b)){
    		if(button.contains(defaultIcon.getDisplay(b))){
    			button.removeChild(defaultIcon.getDisplay(b));
    		}
		}
	}
	
    override protected function getPropertyPrefix():String {
        return "RadioButton.";
    }
    	
    public function getDefaultIcon():Icon {
        return defaultIcon;
    }
    
    override protected function getIconToLayout():Icon{
    	if(button.getIcon() == null){
    		if(defaultIcon.getDisplay(button)){
	    		if(!button.contains(defaultIcon.getDisplay(button))){
	    			button.addChild(defaultIcon.getDisplay(button));
	    		}
    		}
    		return defaultIcon;
    	}else{
    		return button.getIcon();
    	}
    }
    
	override protected function paintBackGround(c:Component, g:Graphics2D, b:IntRectangle):void{
		if(c.isOpaque()){
			g.fillRectangle(new SolidBrush(c.getBackground()), b.x, b.y, b.width, b.height);
		}
	}
}
}