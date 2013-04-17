/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic
{
	
import flash.text.*;
import flash.ui.Keyboard;

import org.aswing.*;
import org.aswing.event.AWEvent;
import org.aswing.event.FocusKeyEvent;
import org.aswing.geom.IntDimension;
import org.aswing.geom.IntRectangle;
import org.aswing.graphics.Graphics2D;
import org.aswing.plaf.*;

/**
 * Basic Button implementation.
 * @author iiley
 * @private
 */
public class BasicButtonUI extends BaseComponentUI{
	
	protected var button:AbstractButton;
	protected var textField:TextField;
	
	public function BasicButtonUI(){
		super();
	}

    protected function getPropertyPrefix():String {
        return "Button.";
    }
    
	override public function installUI(c:Component):void{
		button = AbstractButton(c);
		installDefaults(button);
		installComponents(button);
		installListeners(button);
	}
    
	override public function uninstallUI(c:Component):void{
		button = AbstractButton(c);
		uninstallDefaults(button);
		uninstallComponents(button);
		uninstallListeners(button);
 	}
 	
 	protected function installDefaults(b:AbstractButton):void{
        // load shared instance defaults
        var pp:String = getPropertyPrefix();
        if(!b.isShiftOffsetSet()){
        	b.setShiftOffset(getInt(pp + "textShiftOffset"));
        	b.setShiftOffsetSet(false);
        }
        
        if(b.getMargin() is UIResource) {
            b.setMargin(getInsets(pp + "margin"));
        }
        
        LookAndFeel.installColorsAndFont(b, pp);
        LookAndFeel.installBorderAndBFDecorators(b, pp);
        LookAndFeel.installBasicProperties(b, pp);
        button.mouseChildren = false;
 	}
	
 	protected function uninstallDefaults(b:AbstractButton):void{
 		LookAndFeel.uninstallBorderAndBFDecorators(b);
 	}
 	
 	protected function installComponents(b:AbstractButton):void{
 		textField = AsWingUtils.createLabel(b, "label");
 		b.setFontValidated(false);
 	}
	
 	protected function uninstallComponents(b:AbstractButton):void{
 		b.removeChild(textField);
 	}
 	
 	protected function installListeners(b:AbstractButton):void{
 		b.addStateListener(__stateListener);
 		b.addEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onKeyDown);
 		b.addEventListener(FocusKeyEvent.FOCUS_KEY_UP, __onKeyUp);
 	}
	
 	protected function uninstallListeners(b:AbstractButton):void{
 		b.removeStateListener(__stateListener);
 		b.removeEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onKeyDown);
 		b.removeEventListener(FocusKeyEvent.FOCUS_KEY_UP, __onKeyUp);
 	}
 	
 	//-----------------------------------------------------
 	        
    protected function getTextShiftOffset():int{
    	return button.getShiftOffset();
    }
    
    //--------------------------------------------------------
    
    private function __stateListener(e:AWEvent):void{
    	button.repaint();
    }
    
    private function __onKeyDown(e:FocusKeyEvent):void{
		if(!(button.isShowing() && button.isEnabled())){
			return;
		}
		var model:ButtonModel = button.getModel();
		if(e.keyCode == Keyboard.SPACE && !(model.isRollOver() && model.isPressed())){
	    	setTraversingTrue();
			model.setRollOver(true);
			model.setArmed(true);
			model.setPressed(true);
		}
    }
    
    private function __onKeyUp(e:FocusKeyEvent):void{
		if(!(button.isShowing() && button.isEnabled())){
			return;
		}
		if(e.keyCode == Keyboard.SPACE){
			var model:ButtonModel = button.getModel();
	    	setTraversingTrue();
			model.setPressed(false);
			model.setArmed(false);
			//b.fireActionEvent();
			model.setRollOver(false);
		}
    }
    
    protected function setTraversingTrue():void{
    	var fm:FocusManager = FocusManager.getManager(button.stage);
    	if(fm){
    		fm.setTraversing(true);
    	}
    }
    
    //--------------------------------------------------
    
    /* These rectangles/insets are allocated once for all 
     * ButtonUI.paint() calls.  Re-using rectangles rather than 
     * allocating them in each paint call substantially reduced the time
     * it took paint to run.  Obviously, this method can't be re-entered.
     */
	private static var viewRect:IntRectangle = new IntRectangle();
    private static var textRect:IntRectangle = new IntRectangle();
    private static var iconRect:IntRectangle = new IntRectangle();    

    override public function paint(c:Component, g:Graphics2D, r:IntRectangle):void{
    	super.paint(c, g, r);
    	var b:AbstractButton = AbstractButton(c);
    	
    	var insets:Insets = b.getMargin();
    	if(insets != null){
    		r = insets.getInsideBounds(r);
    	}
    	viewRect.setRect(r);
    	
    	textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

        // layout the text and icon
        var text:String = AsWingUtils.layoutCompoundLabel(c, 
            c.getFont(), b.getDisplayText(), getIconToLayout(), 
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    	b.getDisplayText() == null ? 0 : b.getIconTextGap());
	   	
    	
    	paintIcon(b, g, iconRect);
    	
        if (text != null && text != ""){
        	textField.visible = true;
        	if(b.getModel().isArmed()){
        		textRect.x += getTextShiftOffset();
        		textRect.y += getTextShiftOffset();
        	}
			paintText(b, textRect, text);
        }else{
        	textField.text = "";
        	textField.visible = false;
        }
    }
    
    protected function getIconToLayout():Icon{
    	return button.getIcon();
    }
    
    /**
     * do nothing here, let background decorator to paint the background
     */
	override protected function paintBackGround(c:Component, g:Graphics2D, b:IntRectangle):void{
		//do nothing here, let background decorator to paint the background
	}    
    
    /**
     * paint the text to specified button with specified bounds
     */
    protected function paintText(b:AbstractButton, textRect:IntRectangle, text:String):void{
    	b.bringToTop(textField);
    	var font:ASFont = b.getFont();
    	
		if(textField.text != text){
			textField.text = text;
		}
		if(!b.isFontValidated()){
			AsWingUtils.applyTextFont(textField, font);
			b.setFontValidated(true);
		}
    	AsWingUtils.applyTextColor(textField, getTextPaintColor(b));
		textField.x = textRect.x;
		textField.y = textRect.y;
		if(b.getMnemonicIndex() >= 0){
			textField.setTextFormat(
				new TextFormat(null, null, null, null, null, true), 
				b.getMnemonicIndex());
		}
    	textField.filters = b.getTextFilters();
    }
    
    protected function getTextPaintColor(b:AbstractButton):ASColor{
    	if(b.isEnabled()){
    		return b.getForeground();
    	}else{
    		return BasicGraphicsUtils.getDisabledColor(b);
    	}
    }
    
    /**
     * paint the icon to specified button's mc with specified bounds
     */
    protected function paintIcon(b:AbstractButton, g:Graphics2D, iconRect:IntRectangle):void{
        var model:ButtonModel = b.getModel();
        var icon:Icon = b.getIcon();
        var tmpIcon:Icon = null;
        
        var icons:Array = getIcons();
        for(var i:int=0; i<icons.length; i++){
        	var ico:Icon = icons[i];
			setIconVisible(ico, false);
        }
        
	    if(icon == null) {
	    	return;
	    }

		if(!model.isEnabled()) {
			if(model.isSelected()) {
				tmpIcon = b.getDisabledSelectedIcon();
			} else {
				tmpIcon = b.getDisabledIcon();
			}
		} else if(model.isPressed() && model.isArmed()) {
			tmpIcon = b.getPressedIcon();
		} else if(b.isRollOverEnabled() && model.isRollOver()) {
			if(model.isSelected()) {
				tmpIcon = b.getRollOverSelectedIcon();
			} else {
				tmpIcon = b.getRollOverIcon();
			}
		} else if(model.isSelected()) {
			tmpIcon = b.getSelectedIcon();
		}
              
		if(tmpIcon != null) {
			icon = tmpIcon;
		}
		setIconVisible(icon, true);
		if(model.isPressed() && model.isArmed()) {
			icon.updateIcon(b, g, iconRect.x + getTextShiftOffset(),
                        iconRect.y + getTextShiftOffset());
		}else{
			icon.updateIcon(b, g, iconRect.x, iconRect.y);
		}
    }
    
    protected function setIconVisible(icon:Icon, visible:Boolean):void{
    	if(icon.getDisplay(button) != null){
    		icon.getDisplay(button).visible = visible;
    	}
    }
    
    protected function getIcons():Array{
    	var arr:Array = new Array();
    	if(button.getIcon() != null){
    		arr.push(button.getIcon());
    	}
    	if(button.getDisabledIcon() != null){
    		arr.push(button.getDisabledIcon());
    	}
    	if(button.getSelectedIcon() != null){
    		arr.push(button.getSelectedIcon());
    	}
    	if(button.getDisabledSelectedIcon() != null){
    		arr.push(button.getDisabledSelectedIcon());
    	}
    	if(button.getRollOverIcon() != null){
    		arr.push(button.getRollOverIcon());
    	}
    	if(button.getRollOverSelectedIcon() != null){
    		arr.push(button.getRollOverSelectedIcon());
    	}
    	if(button.getPressedIcon() != null){
    		arr.push(button.getPressedIcon());
    	}
    	return arr;
    }
    
      
    /**
     * Returns the a button's preferred size with specified icon and text.
     */
    protected function getButtonPreferredSize(b:AbstractButton, icon:Icon, text:String):IntDimension{
    	viewRect.setRectXYWH(0, 0, 100000, 100000);
    	textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        
        AsWingUtils.layoutCompoundLabel(b, 
            b.getFont(), text, icon, 
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    	b.getDisplayText() == null ? 0 : b.getIconTextGap()
        );
        /* The preferred size of the button is the size of 
         * the text and icon rectangles plus the buttons insets.
         */
        var size:IntDimension;
        if(icon == null){
        	size = textRect.getSize();
        }else if(b.getDisplayText()==null || b.getDisplayText()==""){
        	size = iconRect.getSize();
        }else{
        	var r:IntRectangle = iconRect.union(textRect);
        	size = r.getSize();
        }
        size = b.getInsets().getOutsideSize(size);
		if(b.getMargin() != null)
        	size = b.getMargin().getOutsideSize(size);
        return size;
    }
    
    /**
     * Returns the a button's minimum size with specified icon and text.
     */    
    protected function getButtonMinimumSize(b:AbstractButton, icon:Icon, text:String):IntDimension{
        var size:IntDimension = b.getInsets().getOutsideSize();
		if(b.getMargin() != null)
        	size = b.getMargin().getOutsideSize(size);
		return size;
    }    
    
    override public function getPreferredSize(c:Component):IntDimension{
    	var b:AbstractButton = AbstractButton(c);
    	return getButtonPreferredSize(b, getIconToLayout(), b.getDisplayText());
    }

    override public function getMinimumSize(c:Component):IntDimension{
    	var b:AbstractButton = AbstractButton(c);
    	return getButtonMinimumSize(b, getIconToLayout(), b.getDisplayText());
    }

    override public function getMaximumSize(c:Component):IntDimension{
		return IntDimension.createBigDimension();
    }
}
}