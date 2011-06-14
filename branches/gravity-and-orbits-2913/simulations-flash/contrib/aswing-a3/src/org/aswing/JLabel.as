/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.plaf.basic.BasicLabelUI;
import flash.display.DisplayObject;
	
/**
 * A display area for a short text string or an image, 
 * or both.
 * A label does not react to input events.
 * As a result, it cannot get the keyboard focus.
 * A label can, however, display a keyboard alternative
 * as a convenience for a nearby component
 * that has a keyboard alternative but can't display it.
 * <p>
 * A <code>JLabel</code> object can display
 * either text, an image, or both.
 * You can specify where in the label's display area
 * the label's contents are aligned
 * by setting the vertical and horizontal alignment.
 * By default, labels are vertically centered 
 * in their display area.
 * Text-only labels are leading edge aligned, by default;
 * image-only labels are horizontally centered, by default.
 * </p>
 * <p>
 * You can also specify the position of the text
 * relative to the image.
 * By default, text is on the trailing edge of the image,
 * with the text and image vertically aligned.
 * </p>
 * <p>
 * Finally, you can use the <code>setIconTextGap</code> method
 * to specify how many pixels
 * should appear between the text and the image.
 * The default is 4 pixels.
 * </p>
 * @author iiley
 */
public class JLabel extends Component{
	
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
	public static var CENTER:int  = AsWingConstants.CENTER;
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
	public static var TOP:int     = AsWingConstants.TOP;
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
    public static var LEFT:int    = AsWingConstants.LEFT;
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
    public static var BOTTOM:int  = AsWingConstants.BOTTOM;
 	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
    public static var RIGHT:int   = AsWingConstants.RIGHT;
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */        
	public static var HORIZONTAL:int = AsWingConstants.HORIZONTAL;
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
	public static var VERTICAL:int   = AsWingConstants.VERTICAL;
	
	
	private var icon:Icon;
	private var text:String;
	private var disabledIcon:Icon;
	
	// Icon/Label Alignment
    private var verticalAlignment:int;
    private var horizontalAlignment:int;
    
    private var verticalTextPosition:int;
    private var horizontalTextPosition:int;

    private var iconTextGap:int;
    private var selectable:Boolean;
    private var textFilters:Array = null;
    
    /**
     * Creates a label.
     * @param text the text
     * @param icon the icon
     * @param horizontalAlignment the horizontal alignment, default is <code>CENTER</code>
     */
	public function JLabel(text:String="", icon:Icon=null, horizontalAlignment:int=0) {
		super();
		setName("JLabel");
		//default
    	this.verticalAlignment = CENTER;
    	this.verticalTextPosition = CENTER;
    	this.horizontalTextPosition = RIGHT;
    	
    	this.text = text;
    	this.icon = icon;
    	installIcon(icon);
    	this.horizontalAlignment = horizontalAlignment;
    	
    	iconTextGap = 4;
    	selectable = false;
		
		updateUI();
	}
	
    override public function updateUI():void{
    	setUI(UIManager.getUI(this));
    }
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicLabelUI;
    }
	
	override public function getUIClassID():String{
		return "LabelUI";
	}

	protected function installIcon(icon:Icon):void{
		if(icon != null && icon.getDisplay(this) != null){
			addChild(icon.getDisplay(this));
		}
	}
	
	protected function uninstallIcon(icon:Icon):void{
		var iconDis:DisplayObject = (icon == null ? null : icon.getDisplay(this));
		if(iconDis != null && isChild(iconDis)){
			removeChild(icon.getDisplay(this));
		}
	}
	
	public function setText(text:String):void{
		if(this.text != text){
			this.text = text;
			repaint();
			invalidate();
		}
	}
	
	public function getText():String{
		return text;
	}
	
	public function setSelectable(b:Boolean):void{
		selectable = b;
	}
	
	public function isSelectable():Boolean{
		return selectable;
	}
	
	public function setTextFilters(fs:Array):void{
		if(textFilters != fs){
			textFilters = fs;
			repaint();
		}
	}
	
	public function getTextFilters():Array{
		return textFilters;
	}
	
	public function setIcon(icon:Icon):void{
		if(this.icon != icon){
			uninstallIcon(this.icon);
			this.icon = icon;
			installIcon(this.icon);
			repaint();
			invalidate();
		}
	}

	public function getIcon():Icon{
		return icon;
	}

    /**
     * Returns the icon used by the label when it's disabled.
     * If no disabled icon has been set, the button constructs
     * one from the default icon if defalut icon setted. otherwish 
     * return null; 
     * <p>
     * The disabled icon really should be created 
     * (if necessary) by the L&F.-->
     *
     * @return the <code>disabledIcon</code> property
     * @see #setDisabledIcon()
     * @see #getEnabled()
     */
    public function getDisabledIcon():Icon {
        if(disabledIcon == null) {
            if(icon != null) {
            	//TODO imp
                //disabledIcon = new GrayFilteredIcon(icon);
            }
        }
        return disabledIcon;
    }
    
    /**
     * Sets the disabled icon for the label.
     * @param disabledIcon the icon used as the disabled image
     * @see #getDisabledIcon()
     * @see #setEnabled()
     */
    public function setDisabledIcon(disabledIcon:Icon):void {
        var oldValue:Icon = this.disabledIcon;
        this.disabledIcon = disabledIcon;
        if (disabledIcon != oldValue) {
        	uninstallIcon(oldValue);
        	installIcon(disabledIcon);
            if (!isEnabled()) {
                repaint();
				invalidate();
            }
        }
    }

    /**
     * Returns the vertical alignment of the text and icon.
     *
     * @return the <code>verticalAlignment</code> property, one of the
     *		following values: 
     * <ul>
     * <li>AsWingConstants.CENTER (the default)
     * <li>AsWingConstants.TOP
     * <li>AsWingConstants.BOTTOM
     * </ul>
     */
    public function getVerticalAlignment():Number {
        return verticalAlignment;
    }
    
    /**
     * Sets the vertical alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>AsWingConstants.CENTER (the default)
     * <li>AsWingConstants.TOP
     * <li>AsWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalAlignment(alignment:Number):void {
        if (alignment == verticalAlignment){
        	return;
        }else{
        	verticalAlignment = alignment;
        	repaint();
        }
    }
    
    /**
     * Returns the horizontal alignment of the icon and text.
     * @return the <code>horizontalAlignment</code> property,
     *		one of the following values:
     * <ul>
     * <li>AsWingConstants.RIGHT (the default)
     * <li>AsWingConstants.LEFT
     * <li>AsWingConstants.CENTER
     * </ul>
     */
    public function getHorizontalAlignment():Number{
        return horizontalAlignment;
    }
    
    /**
     * Sets the horizontal alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>AsWingConstants.RIGHT (the default)
     * <li>AsWingConstants.LEFT
     * <li>AsWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalAlignment(alignment:Number):void {
        if (alignment == horizontalAlignment){
        	return;
        }else{
        	horizontalAlignment = alignment;     
        	repaint();
        }
    }

    
    /**
     * Returns the vertical position of the text relative to the icon.
     * @return the <code>verticalTextPosition</code> property, 
     *		one of the following values:
     * <ul>
     * <li>AsWingConstants.CENTER  (the default)
     * <li>AsWingConstants.TOP
     * <li>AsWingConstants.BOTTOM
     * </ul>
     */
    public function getVerticalTextPosition():Number{
        return verticalTextPosition;
    }
    
    /**
     * Sets the vertical position of the text relative to the icon.
     * @param alignment  one of the following values:
     * <ul>
     * <li>AsWingConstants.CENTER (the default)
     * <li>AsWingConstants.TOP
     * <li>AsWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalTextPosition(textPosition:Number):void {
        if (textPosition == verticalTextPosition){
	        return;
        }else{
        	verticalTextPosition = textPosition;
        	repaint();
        	revalidate();
        }
    }
    
    /**
     * Returns the horizontal position of the text relative to the icon.
     * @return the <code>horizontalTextPosition</code> property, 
     * 		one of the following values:
     * <ul>
     * <li>AsWingConstants.RIGHT (the default)
     * <li>AsWingConstants.LEFT
     * <li>AsWingConstants.CENTER
     * </ul>
     */
    public function getHorizontalTextPosition():Number {
        return horizontalTextPosition;
    }
    
    /**
     * Sets the horizontal position of the text relative to the icon.
     * @param textPosition one of the following values:
     * <ul>
     * <li>AsWingConstants.RIGHT (the default)
     * <li>AsWingConstants.LEFT
     * <li>AsWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalTextPosition(textPosition:Number):void {
        if (textPosition == horizontalTextPosition){
        	return;
        }else{
        	horizontalTextPosition = textPosition;
        	repaint();
        	revalidate();
        }
    }
    
    /**
     * Returns the amount of space between the text and the icon
     * displayed in this button.
     *
     * @return an int equal to the number of pixels between the text
     *         and the icon.
     * @see #setIconTextGap()
     */
    public function getIconTextGap():Number {
        return iconTextGap;
    }

    /**
     * If both the icon and text properties are set, this property
     * defines the space between them.  
     * <p>
     * The default value of this property is 4 pixels.
     * 
     * @see #getIconTextGap()
     */
    public function setIconTextGap(iconTextGap:Number):void {
        var oldValue:Number = this.iconTextGap;
        this.iconTextGap = iconTextGap;
        if (iconTextGap != oldValue) {
            revalidate();
            repaint();
        }
    }
	
}

}