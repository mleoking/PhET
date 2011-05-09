/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASWingConstants;
import org.aswing.Component;
import org.aswing.overflow.GrayFilteredIcon;
import org.aswing.Icon;
import org.aswing.plaf.LabelUI;
import org.aswing.UIManager;
import org.aswing.util.StringUtils;

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
class org.aswing.JLabel extends Component {
	
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
	public static var CENTER:Number  = ASWingConstants.CENTER;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
	public static var TOP:Number     = ASWingConstants.TOP;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var LEFT:Number    = ASWingConstants.LEFT;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var BOTTOM:Number  = ASWingConstants.BOTTOM;
 	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var RIGHT:Number   = ASWingConstants.RIGHT;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */        
	public static var HORIZONTAL:Number = ASWingConstants.HORIZONTAL;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
	public static var VERTICAL:Number   = ASWingConstants.VERTICAL;
	
	
	private var icon:Icon;
	private var text:String;
	private var disabledIcon:Icon;
	
	// Icon/Label Alignment
    private var verticalAlignment:Number;
    private var horizontalAlignment:Number;
    
    private var verticalTextPosition:Number;
    private var horizontalTextPosition:Number;

    private var iconTextGap:Number;
    
    /**
     * JLabel(text:String, icon:Icon, horizontalAlignment:Number)<br>
     * JLabel(text:String, icon:Icon)<br>
     * JLabel(text:String, horizontalAlignment:Number)<br>
     * JLabel(text:String)<br>
     * JLabel(icon:Icon, horizontalAlignment:Number)<br>
     * JLabel(icon:Icon)<br>
     * JLabel()<br>
	 * <p>
     */
	public function JLabel(text, icon, horizontalAlignment:Number) {
		super();
		setName("JLabel");
		//default
    	this.verticalAlignment = CENTER;
    	this.horizontalAlignment = CENTER;
    	this.verticalTextPosition = CENTER;
    	this.horizontalTextPosition = RIGHT;
    	
    	iconTextGap = 4;
    	
    	if(text == null && icon != null){
			this.icon = Icon(icon);
			if(horizontalAlignment != undefined){
				this.horizontalAlignment = horizontalAlignment;
			}
    	}else if(horizontalAlignment != undefined){
			this.horizontalAlignment = horizontalAlignment;
			this.text = StringUtils.castString(text);
			this.icon = Icon(icon);
		}else if(text instanceof Icon){
			this.text = null;
			this.icon = Icon(text);
			if(icon != undefined){
				this.horizontalAlignment = Number(icon);
			}
		}else{
			this.text = StringUtils.castString(text);
			if(icon != undefined){
				if(icon instanceof Icon){
					this.icon = Icon(icon);
				}else{
					this.horizontalAlignment = Number(icon);
				}
			}
		}
		
		updateUI();
	}
	
    public function updateUI():Void{
    	setUI(LabelUI(UIManager.getUI(this)));
    }
    
    public function setUI(newUI:LabelUI):Void{
    	super.setUI(newUI);
    }
	
	public function getUIClassID():String{
		return "LabelUI";
	}	
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicLabelUI;
    }

	/**
	 * Sets text and icon at one method here.
	 * @param text the text for the label
	 * @param icon the default icon for the label
	 * @see #setText()
	 * @see #setIcon()
	 */	
	public function setContent(text:String, icon:Icon):Void{
		if(this.text != text){
			this.text = text;
		}
		if(this.icon != icon){
			uninstallIconWhenNextPaint(this.icon);
			this.icon = icon;
		}
		repaint();
		invalidate();
	}
	
	public function setText(text:String):Void{
		if(this.text != text){
			this.text = text;
			repaint();
			invalidate();
		}
	}
	
	public function getText():String{
		return text;
	}
	
	public function setIcon(icon:Icon):Void{
		if(this.icon != icon){
			uninstallIconWhenNextPaint(this.icon);
			this.icon = icon;
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
                disabledIcon = new GrayFilteredIcon(icon);
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
    public function setDisabledIcon(disabledIcon:Icon):Void {
        var oldValue:Icon = this.disabledIcon;
        this.disabledIcon = disabledIcon;
        if (disabledIcon != oldValue) {
        	uninstallIconWhenNextPaint(oldValue);
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
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function getVerticalAlignment():Number {
        return verticalAlignment;
    }
    
    /**
     * Sets the vertical alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalAlignment(alignment:Number):Void {
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
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function getHorizontalAlignment():Number{
        return horizontalAlignment;
    }
    
    /**
     * Sets the horizontal alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalAlignment(alignment:Number):Void {
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
     * <li>ASWingConstants.CENTER  (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function getVerticalTextPosition():Number{
        return verticalTextPosition;
    }
    
    /**
     * Sets the vertical position of the text relative to the icon.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalTextPosition(textPosition:Number):Void {
        if (textPosition == verticalTextPosition){
	        return;
        }else{
        	verticalTextPosition = textPosition;
        	repaint();
        }
    }
    
    /**
     * Returns the horizontal position of the text relative to the icon.
     * @return the <code>horizontalTextPosition</code> property, 
     * 		one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function getHorizontalTextPosition():Number {
        return horizontalTextPosition;
    }
    
    /**
     * Sets the horizontal position of the text relative to the icon.
     * @param textPosition one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalTextPosition(textPosition:Number):Void {
        if (textPosition == horizontalTextPosition){
        	return;
        }else{
        	horizontalTextPosition = textPosition;
        	repaint();
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
    public function setIconTextGap(iconTextGap:Number):Void {
        var oldValue:Number = this.iconTextGap;
        this.iconTextGap = iconTextGap;
        if (iconTextGap != oldValue) {
            revalidate();
            repaint();
        }
    }

}
