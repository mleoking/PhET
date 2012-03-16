package org.aswing.plaf.basic{

import org.aswing.*;
import org.aswing.plaf.UIResource;
import org.aswing.graphics.*;
import org.aswing.geom.*;

/**
 * @private
 */
public class BasicLabelButtonUI extends BasicButtonUI{
	
	public function BasicLabelButtonUI(){
		super();
	}
	
    override protected function getPropertyPrefix():String {
        return "LabelButton.";
    }
	
	override protected function installDefaults(bb:AbstractButton):void{
		super.installDefaults(bb);
		var pp:String = getPropertyPrefix();
    	var b:JLabelButton = bb as JLabelButton;
    	if(b.getRollOverColor() == null || b.getRollOverColor() is UIResource){
    		b.setRollOverColor(getColor(pp+"rollOver"));
    	}
    	if(b.getPressedColor() == null || b.getPressedColor() is UIResource){
    		b.setPressedColor(getColor(pp+"pressed"));
    	}
    	b.buttonMode = true;
	}
	
    override protected function getTextPaintColor(bb:AbstractButton):ASColor{
    	var b:JLabelButton = bb as JLabelButton;
    	if(b.isEnabled()){
    		var model:ButtonModel = b.getModel();
    		if(model.isSelected() || (model.isPressed() && model.isArmed())){
    			return b.getPressedColor();
    		}else if(b.isRollOverEnabled() && model.isRollOver()){
    			return b.getRollOverColor();
    		}
    		return b.getForeground();
    	}else{
    		return BasicGraphicsUtils.getDisabledColor(b);
    	}
    }
    
    /**
     * paint normal bg
     */
	override protected function paintBackGround(c:Component, g:Graphics2D, b:IntRectangle):void{
		if(c.isOpaque()){
			g.fillRectangle(new SolidBrush(c.getBackground()), b.x, b.y, b.width, b.height);
		}		
	}
}
}