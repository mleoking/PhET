/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractButton;
import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.ASWingUtils;
import org.aswing.border.Border;
import org.aswing.ButtonModel;
import org.aswing.Component;
import org.aswing.FocusManager;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.Icon;
import org.aswing.Insets;
import org.aswing.LookAndFeel;
import org.aswing.plaf.ButtonUI;
import org.aswing.plaf.ComponentUI;
import org.aswing.plaf.UIResource;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.HashMap;
 
/**
 * Basic Button implementation.
 * To implement a Diff button UI, generally you should:
 * <ul>
 * 	<li>override <code>paintBackGround</code> to paint different bg for you button
 * 	<li>implement a diff Border for it, and put it into UI defaults with "Button.border" as key.
 * 	<li>initialize differnt UI defaults for buttons in your LAF class, for example:  
 * 	Button.font, ToggleButton.backgound XxxButton.foreground...
 * </ul>
 * @author iiley
 */
class org.aswing.plaf.basic.BasicButtonUI extends ButtonUI{

    // Shared UI object
    private static var buttonUI:BasicButtonUI;
    
    // Offset controlled by set method 
    private var shiftOffset:Number;
    private var defaultTextShiftOffset:Number;

    // Has the shared instance defaults been initialized?
    private var defaults_initialized:Boolean;
    
    //map to recored the buttons -> listeners
   	private var listenerMap:HashMap;
   	//map to recored the buttons -> last painted icon
   	private var lastPaintedIconMap:HashMap;
   	//map to recored the buttons -> topTextField
   	private var topTextFieldMap:HashMap;
   	//map to recored the buttons -> bottomTextField
   	private var bottomTextFieldMap:HashMap;

    private static var propertyPrefix:String = "Button" + ".";

    // ********************************
    //          Create PLAF
    // ********************************
    public static function createInstance(c:Component):ComponentUI {
    	if(buttonUI == null){
    		buttonUI = new BasicButtonUI();
    	}
        return buttonUI;
    }

    private function getPropertyPrefix():String {
        return propertyPrefix;
    }
    
    public function BasicButtonUI() {
    	shiftOffset = 0;
    	defaultTextShiftOffset = 0;
    	defaults_initialized = false;
    	listenerMap = new HashMap();
    	lastPaintedIconMap = new HashMap();
    	topTextFieldMap = new HashMap();
    	bottomTextFieldMap = new HashMap();
    	checkRectsForCountLayout();
    }

    public function installUI(c:Component):Void{
    	var b:AbstractButton = AbstractButton(c);
        installDefaults(b);
        installListeners(b);
    }
    
    private var defaultForeground:ASColor;
    private var defaultBackground:ASColor;
    private var defaultTextFormat:TextFormat;
    private var defaultBorder:Border;    

	private function installDefaults(b:AbstractButton):Void{
        // load shared instance defaults
        var pp:String = getPropertyPrefix();
        if(!defaults_initialized) {
            defaultTextShiftOffset = UIManager.getNumber(pp + "textShiftOffset");
        	defaults_initialized = true;
        }
        
        if(b.getMargin() === undefined || (b.getMargin() instanceof UIResource)) {
            b.setMargin(UIManager.getInsets(pp + "margin"));
        }
        
        LookAndFeel.installColorsAndFont(b, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(b, pp + "border");
        LookAndFeel.installBasicProperties(b, pp);
        setTextShiftOffset();
	}
	
	private function installListeners(b:AbstractButton):Void{
		var l:Object = createButtonListener(b);
		if(l != null){
			listenerMap.put(b.getID(), l);
			b.addEventListener(l);
		}
	}
	
	private function createButtonListener(b:AbstractButton):Object{
		var l:Object = new Object();
		l[AbstractButton.ON_STATE_CHANGED] = Delegate.create(this, __onStateChanged);
		l[AbstractButton.ON_KEY_DOWN] = Delegate.create(this, __onKeyDown);
		l[AbstractButton.ON_KEY_UP] = Delegate.create(this, __onKeyUp);
		return l;
	}
		
	private function __onStateChanged(source:AbstractButton):Void{
		source.paintImmediately();
	}
	private function __onKeyDown(source:AbstractButton):Void{
		if(!source.isEnabled()){
			return;
		}
		var b:AbstractButton = source;
		var model:ButtonModel = b.getModel();
		if(Key.getCode() == Key.SPACE && !(model.isRollOver() && model.isPressed())){
	    	FocusManager.getCurrentManager().setTraversing(true);
			b.getModel().setRollOver(true);
			b.getModel().setPressed(true);
		}
	}
	private function __onKeyUp(source:AbstractButton):Void{
		if(!source.isEnabled()){
			return;
		}
		var b:AbstractButton = source;
		if(Key.getCode() == Key.SPACE){
	    	FocusManager.getCurrentManager().setTraversing(true);
			b.getModel().setReleased(true);
			b.fireActionEvent();
			b.getModel().setRollOver(false);
		}
	}
	
	
    public function uninstallUI(c:Component):Void{
    	var b:AbstractButton = AbstractButton(c);
        uninstallDefaults(b);
        uninstallListeners(b);
        removeMCs(b);
    	listenerMap.remove(c.getID());
    	lastPaintedIconMap.remove(c.getID());
    	topTextFieldMap.remove(c.getID());
    	bottomTextFieldMap.remove(c.getID());
    }
    
    private function uninstallDefaults(b:AbstractButton):Void{
    	LookAndFeel.uninstallBorder(b);
    	defaults_initialized = false;
    }
    
    private function uninstallListeners(b:AbstractButton):Void{
    	b.removeEventListener(listenerMap.remove(b.getID()));
    }
        
    private function removeMCs(c:Component):Void{
    	getTopTextField(c).removeTextField();
    	getBottomTextField(c).removeTextField();
    }
    
    public function create(c:Component):Void{
    	var topText:TextField = c.createTextField("t_text");
    	var bottomText:TextField = c.createTextField("b_text");
    	topTextFieldMap.put(c.getID(), topText);
    	bottomTextFieldMap.put(c.getID(), bottomText);
    	c.setFontValidated(false);
    }
    
    private function getTopTextField(c:Component):TextField{
    	return TextField(topTextFieldMap.get(c.getID()));
    }
    
    private function getBottomTextField(c:Component):TextField{
    	return TextField(bottomTextFieldMap.get(c.getID()));
    }
    
    /* These rectangles/insets are allocated once for all 
     * ButtonUI.paint() calls.  Re-using rectangles rather than 
     * allocating them in each paint call substantially reduced the time
     * it took paint to run.  Obviously, this method can't be re-entered.
     */
	private static var viewRect:Rectangle;
    private static var textRect:Rectangle;
    private static var iconRect:Rectangle;
       
    private static function checkRectsForCountLayout():Void{
    	if(viewRect == null){
			viewRect = new Rectangle();
    		textRect = new Rectangle();
    		iconRect = new Rectangle();
    	}
    }
       
    public function paint(c:Component, g:Graphics, r:Rectangle):Void{
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
        var text:String = ASWingUtils.layoutCompoundLabel(
            c.getFont(), b.getText(), b.getIcon(), 
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    	b.getText() == null ? 0 : b.getIconTextGap());
	   	
    	
    	paintIcon(b, g, iconRect);
    	
        if (text != null && text != ""){
        	if(b.getModel().isPressed()){
        		textRect.x += getTextShiftOffset();
        		textRect.y += getTextShiftOffset();
        	}
			paintText(b, textRect, text);
        }else{
        	getTopTextField(b).text = "";
        	getBottomTextField(b).text = "";
        }
    }
    
    /**
     * paint the text to specified button with specified bounds
     */
    private function paintText(b:AbstractButton, textRect:Rectangle, text:String):Void{
    	
    	var model:ButtonModel = b.getModel();
    	var font:ASFont = b.getFont();
    	if(model.isEnabled()){
    		paintTextField(b, textRect, getTopTextField(b), text, font, b.getForeground());
    		getBottomTextField(b)._visible = false;
    	}else{
    		getBottomTextField(b)._visible = true;
    		paintTextField(b, textRect, getTopTextField(b), text, font, b.getBackground().darker());
    		var rbRectangle:Rectangle = new Rectangle(textRect);
    		rbRectangle.x++;
    		rbRectangle.y++;
    		//return to false to make sure sencond text be applied the font
    		b.setFontValidated(false);
    		paintTextField(b, rbRectangle, getBottomTextField(b), text, font, b.getBackground().brighter());
    	}
    }
    
    private function paintTextField(b:AbstractButton, tRect:Rectangle, textField:TextField, text:String, font:ASFont, color:ASColor):Void{
		if(textField.text != text){
			textField.text = text;
		}
		if(!b.isFontValidated()){
			ASWingUtils.applyTextFont(textField, font);
			b.setFontValidated(true);
		}
    	ASWingUtils.applyTextColor(textField, color);
		textField._x = tRect.x;
		textField._y = tRect.y;
    }
    
    /**
     * paint the icon to specified button's mc with specified bounds
     */
    private function paintIcon(b:AbstractButton, g:Graphics, iconRect:Rectangle):Void{
        var model:ButtonModel = b.getModel();
        var icon:Icon = b.getIcon();
        var tmpIcon:Icon = null;
	    if(icon == null) {
			unistallLastPaintIcon(b, icon);
	    	return;
	    }

        if(!model.isEnabled()) {
			if(model.isSelected()) {
            	tmpIcon = b.getDisabledSelectedIcon();
			} else {
            	tmpIcon = b.getDisabledIcon();
			}
        }else if(model.isPressed()) {
            tmpIcon = b.getPressedIcon();
            if(tmpIcon != null) {
                // revert back to 0 offset
                //clearTextShiftOffset();
            }
        }else if(model.isRollOver() && b.isRollOverEnabled()) {
			if(model.isSelected()) {
            	tmpIcon = b.getRollOverSelectedIcon();
			} else {
            	tmpIcon = b.getRollOverIcon();
			}
        }else if(model.isSelected()) {
            tmpIcon = b.getSelectedIcon();
	    }
              
	    if(tmpIcon != null) {
	        icon = tmpIcon;
	    }
               
		unistallLastPaintIcon(b, icon);
		
        if(model.isPressed()) {
            icon.paintIcon(b, g, iconRect.x + getTextShiftOffset(),
                    iconRect.y + getTextShiftOffset());
        } else {
            icon.paintIcon(b, g, iconRect.x, iconRect.y);
        }
        setJustPaintedIcon(b, icon);
    }
    
	private function paintBackGround(com:Component, g:Graphics, b:Rectangle):Void{
		super.paintBackGround(com, g, b);
	}    
    
    private function unistallLastPaintIcon(b:AbstractButton, currentIcon:Icon):Void{
    	var lastPaintedIcon:Icon = Icon(lastPaintedIconMap.get(b.getID()));
        if(lastPaintedIcon != currentIcon){
        	lastPaintedIcon.uninstallIcon(b);
        }
    }
    
    private function setJustPaintedIcon(b:AbstractButton, ic:Icon):Void{
    	lastPaintedIconMap.put(b.getID(), ic);
    }
    
    private function clearTextShiftOffset():Void{
        shiftOffset = 0;
    }
    
    private function setTextShiftOffset():Void{
        shiftOffset = defaultTextShiftOffset;
    }
    
    private function getTextShiftOffset():Number{
    	return shiftOffset;
    }
    
    /**
     * Returns the a button's preferred size with specified icon and text.
     */
    private function getButtonPreferredSize(b:AbstractButton, icon:Icon, text:String):Dimension{
    	viewRect.setRect(0, 0, 100000, 100000);
    	textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        
        ASWingUtils.layoutCompoundLabel(
            b.getFont(), text, icon, 
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    	b.getText() == null ? 0 : b.getIconTextGap()
        );
        /* The preferred size of the button is the size of 
         * the text and icon rectangles plus the buttons insets.
         */
        var size:Dimension;
        if(icon == null){
        	size = textRect.getSize();
        }else if(b.getText()==null || b.getText()==""){
        	size = iconRect.getSize();
        }else{
        	var r:Rectangle = iconRect.union(textRect);
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
    private function getButtonMinimumSize(b:AbstractButton, icon:Icon, text:String):Dimension{
        var size:Dimension = b.getInsets().getOutsideSize();
		if(b.getMargin() != null)
        	size = b.getMargin().getOutsideSize(size);
		return size;
    }    
    
    public function getPreferredSize(c:Component):Dimension{
    	var b:AbstractButton = AbstractButton(c);
    	var icon:Icon = b.getIcon();
    	var text:String = b.getText();
    	return getButtonPreferredSize(b, icon, text);
    }

    public function getMinimumSize(c:Component):Dimension{
    	var b:AbstractButton = AbstractButton(c);
    	var icon:Icon = b.getIcon();
    	var text:String = b.getText();
    	return getButtonMinimumSize(b, icon, text);
    }

    public function getMaximumSize(c:Component):Dimension{
		return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
    }    
}
