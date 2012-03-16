package org.aswing.plaf.basic{
	
import org.aswing.*;
import org.aswing.graphics.Graphics2D;
import org.aswing.geom.IntRectangle;
import org.aswing.geom.IntDimension;
import org.aswing.Component;
import org.aswing.plaf.*;
import org.aswing.event.AWEvent;
import flash.text.*;
import flash.filters.BlurFilter;
import flash.utils.getTimer;
import flash.filters.BevelFilter;

/**
 * Label UI basic imp.
 * @author iiley
 * @private
 */
public class BasicLabelUI extends BaseComponentUI{
	
	protected var label:JLabel;
	protected var textField:TextField;
	
	public function BasicLabelUI(){
		super();
	}

    protected function getPropertyPrefix():String {
        return "Label.";
    }
    
	override public function installUI(c:Component):void{
		label = JLabel(c);
		installDefaults(label);
		installComponents(label);
		installListeners(label);
	}
    
	override public function uninstallUI(c:Component):void{
		label = JLabel(c);
		uninstallDefaults(label);
		uninstallComponents(label);
		uninstallListeners(label);
 	}
 	
 	protected function installDefaults(b:JLabel):void{
        var pp:String = getPropertyPrefix();
        
        LookAndFeel.installColorsAndFont(b, pp);
        LookAndFeel.installBorderAndBFDecorators(b, pp);
        LookAndFeel.installBasicProperties(b, pp);
 	}
	
 	protected function uninstallDefaults(b:JLabel):void{
 		LookAndFeel.uninstallBorderAndBFDecorators(b);
 	}
 	
 	protected function installComponents(b:JLabel):void{
 		textField = new TextField();
 		textField.autoSize = TextFieldAutoSize.LEFT;
 		textField.selectable = false;
 		textField.mouseEnabled = false;
 		textField.mouseWheelEnabled = false;
 		b.addChild(textField);
 		b.setFontValidated(false);
 	}
	
 	protected function uninstallComponents(b:JLabel):void{
 		b.removeChild(textField);
 	}
 	
 	protected function installListeners(b:JLabel):void{
 	}
	
 	protected function uninstallListeners(b:JLabel):void{
 	}
    
    //--------------------------------------------------
    
    /* These rectangles/insets are allocated once for all 
     * LabelUI.paint() calls.  Re-using rectangles rather than 
     * allocating them in each paint call substantially reduced the time
     * it took paint to run.  Obviously, this method can't be re-entered.
     */
	private static var viewRect:IntRectangle = new IntRectangle();
    private static var textRect:IntRectangle = new IntRectangle();
    private static var iconRect:IntRectangle = new IntRectangle();    

    override public function paint(c:Component, g:Graphics2D, r:IntRectangle):void{
    	super.paint(c, g, r);
    	var b:JLabel = JLabel(c);
    	
    	viewRect.setRect(r);
    	
    	textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        // layout the text and icon
        var text:String = AsWingUtils.layoutCompoundLabel(c, 
            c.getFont(), b.getText(), getIconToLayout(), 
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    	b.getText() == null ? 0 : b.getIconTextGap());
	   	
    	
    	paintIcon(b, g, iconRect);
    	
        if (text != null && text != ""){
        	textField.visible = true;
			paintText(b, textRect, text);
        }else{
        	textField.text = "";
        	textField.visible = false;
        }
        
        textField.selectable = b.isSelectable();
        textField.mouseEnabled = b.isSelectable();
    }
    
    protected function getIconToLayout():Icon{
    	return label.getIcon();
    }
        
    /**
     * paint the text to specified button with specified bounds
     */
    protected function paintText(b:JLabel, textRect:IntRectangle, text:String):void{
    	var font:ASFont = b.getFont();
    	
		if(textField.text != text){
			textField.text = text;
		}
		if(!b.isFontValidated()){
			AsWingUtils.applyTextFont(textField, font);
			b.setFontValidated(true);
		}
    	AsWingUtils.applyTextColor(textField, b.getForeground());
		textField.x = textRect.x;
		textField.y = textRect.y;
    	if(!b.isEnabled()){
    		b.filters = [new BlurFilter(2, 2, 2)];
    	}else{
    		b.filters = null;
    	}
    	textField.filters = label.getTextFilters();
    }
    
    /**
     * paint the icon to specified button's mc with specified bounds
     */
    protected function paintIcon(b:JLabel, g:Graphics2D, iconRect:IntRectangle):void{
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

		if(!b.isEnabled()) {
			tmpIcon = b.getDisabledIcon();
		}
              
		if(tmpIcon != null) {
			icon = tmpIcon;
		}
		
		setIconVisible(icon, true);
		icon.updateIcon(b, g, iconRect.x, iconRect.y);
    }
    
    private function setIconVisible(icon:Icon, visible:Boolean):void{
    	if(icon.getDisplay(label) != null){
    		icon.getDisplay(label).visible = visible;
    	}
    }
    
    protected function getIcons():Array{
    	var arr:Array = new Array();
    	if(label.getIcon() != null){
    		arr.push(label.getIcon());
    	}
    	if(label.getDisabledIcon() != null){
    		arr.push(label.getDisabledIcon());
    	}
    	return arr;
    }
    
      
    /**
     * Returns the a label's preferred size with specified icon and text.
     */
    protected function getLabelPreferredSize(b:JLabel, icon:Icon, text:String):IntDimension{
    	viewRect.setRectXYWH(0, 0, 100000, 100000);
    	textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        
        AsWingUtils.layoutCompoundLabel(b, 
            b.getFont(), text, icon, 
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    	b.getText() == null ? 0 : b.getIconTextGap()
        );
        /* The preferred size of the button is the size of 
         * the text and icon rectangles plus the buttons insets.
         */
        var size:IntDimension;
        if(icon == null){
        	size = textRect.getSize();
        }else if(b.getText()==null || b.getText()==""){
        	size = iconRect.getSize();
        }else{
        	var r:IntRectangle = iconRect.union(textRect);
        	size = r.getSize();
        }
        size = b.getInsets().getOutsideSize(size);
        return size;
    }    
    
    override public function getPreferredSize(c:Component):IntDimension{
    	var b:JLabel = JLabel(c);
    	return getLabelPreferredSize(b, getIconToLayout(), b.getText());
    }

    override public function getMinimumSize(c:Component):IntDimension{
    	return c.getInsets().getOutsideSize();
    }

    override public function getMaximumSize(c:Component):IntDimension{
		return IntDimension.createBigDimension();
    }
	
}
}