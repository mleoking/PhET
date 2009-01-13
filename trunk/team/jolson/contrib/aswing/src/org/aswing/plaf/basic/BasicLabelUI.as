/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.ASWingUtils;
import org.aswing.border.Border;
import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.Icon;
import org.aswing.JLabel;
import org.aswing.LookAndFeel;
import org.aswing.plaf.ComponentUI;
import org.aswing.plaf.LabelUI;
import org.aswing.util.HashMap;

/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.BasicLabelUI extends LabelUI {
    // Shared UI object
    private static var labelUI:BasicLabelUI;
        
   	//map to recored the buttons -> last painted icon
   	private var lastPaintedIconMap:HashMap;
   	//map to recored the buttons -> topTextField
   	private var topTextFieldMap:HashMap;
   	//map to recored the buttons -> bottomTextField
   	private var bottomTextFieldMap:HashMap;

    private static var propertyPrefix:String = "Label" + ".";

    // ********************************
    //          Create PLAF
    // ********************************
    public static function createInstance(c:Component):ComponentUI {
    	if(labelUI == null){
    		labelUI = new BasicLabelUI();
    	}
        return labelUI;
    }

    private function getPropertyPrefix():String {
        return propertyPrefix;
    }
    	
    public function BasicLabelUI() {
    	super();
    	lastPaintedIconMap = new HashMap();
    	topTextFieldMap = new HashMap();
    	bottomTextFieldMap = new HashMap();
    	checkRectsForCountLayout();
    }

    public function installUI(c:Component):Void{
    	var b:JLabel = JLabel(c);
        installDefaults(b);
        installListeners(b);
    }
    
    private var defaultForeground:ASColor;
    private var defaultBackground:ASColor;
    private var defaultTextFormat:TextFormat;
    private var defaultBorder:Border;    

	private function installDefaults(b:JLabel):Void{
        // load shared instance defaults
        var pp:String = getPropertyPrefix();

        LookAndFeel.installColorsAndFont(b, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(b, pp + "border");
        LookAndFeel.installBasicProperties(b, pp);
	}
	
	private function installListeners(b:JLabel):Void{
	}
		
	
    public function uninstallUI(c:Component):Void{
    	var b:JLabel = JLabel(c);
        uninstallDefaults(b);
        uninstallListeners(b);
        removeMCs(b);
    	lastPaintedIconMap.remove(c.getID());
    	topTextFieldMap.remove(c.getID());
    	bottomTextFieldMap.remove(c.getID());
    }
    
    private function uninstallDefaults(b:JLabel):Void{
    	LookAndFeel.uninstallBorder(b);
    }
    
    private function uninstallListeners(b:JLabel):Void{
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
     * LabelUI.paint() calls.  Re-using rectangles rather than 
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
    	var b:JLabel = JLabel(c);
    	    	
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
			paintText(b, textRect, text);
        }else{
        	getTopTextField(b).text = "";
        	getBottomTextField(b).text = "";
        }
    }
    
    /**
     * paint the text to specified button with specified bounds
     */
    private function paintText(b:JLabel, textRect:Rectangle, text:String):Void{
    	var font:ASFont = b.getFont();
    	if(b.isEnabled()){
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
    
    private function paintTextField(b:JLabel, tRect:Rectangle, textField:TextField, text:String, font:ASFont, color:ASColor):Void{
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
    private function paintIcon(b:JLabel, g:Graphics, iconRect:Rectangle):Void{
		var icon:Icon = b.getIcon();
	    if(icon == null) {
			unistallLastPaintIcon(b, icon);
			return;
	    }

        if(!b.isEnabled()) {
        	if(b.getDisabledIcon() != null){
           		icon = b.getDisabledIcon();
        	}
        }
               
        unistallLastPaintIcon(b, icon);
        icon.paintIcon(b, g, iconRect.x, iconRect.y);
        setJustPaintedIcon(b, icon);
    }
    
    private function unistallLastPaintIcon(b:JLabel, currentIcon:Icon):Void{
    	var lastPaintedIcon:Icon = Icon(lastPaintedIconMap.get(b.getID()));
        if(lastPaintedIcon != currentIcon){
        	lastPaintedIcon.uninstallIcon(b);
        }
    }
    
    private function setJustPaintedIcon(b:JLabel, ic:Icon):Void{
    	lastPaintedIconMap.put(b.getID(), ic);
    }
    
    /**
     * Returns the a label's preferred size with specified icon and text.
     */
    private function getLabelPreferredSize(b:JLabel, icon:Icon, text:String):Dimension{
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
        /* The preferred size is the size of 
         * the text and icon rectangles plus the buttons insets.
         */
        var r:Rectangle = iconRect.union(textRect);
        var size:Dimension = r.getSize();
        size = b.getInsets().getOutsideSize(size);
        return size;
    }
 
    public function getPreferredSize(c:Component):Dimension{
    	var b:JLabel = JLabel(c);
    	var icon:Icon = b.getIcon();
    	var text:String = b.getText();
    	return getLabelPreferredSize(b, icon, text);
    }
	
    public function getMinimumSize(c:Component):Dimension{
		return new Dimension(0, 0);
    }

    public function getMaximumSize(c:Component):Dimension{
		return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
    }    
}
