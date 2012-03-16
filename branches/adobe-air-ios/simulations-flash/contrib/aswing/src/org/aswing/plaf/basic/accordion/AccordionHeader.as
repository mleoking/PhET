import org.aswing.Component;
import org.aswing.Icon;
import org.aswing.Insets;

/**
 * Accordion Header
 * @author iiley
 */
interface org.aswing.plaf.basic.accordion.AccordionHeader {
	
	/**
	 * Sets text and icon to the header
	 */
	public function setTextAndIcon(text:String, icon:Icon):Void;
	
	/**
	 * Sets whether it is selected
	 */
	public function setSelected(b:Boolean):Void;
	
    /**
     * Sets the vertical alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalAlignment(alignment:Number):Void;
    
    /**
     * Sets the horizontal alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalAlignment(alignment:Number):Void;
    
    /**
     * Sets the vertical position of the text relative to the icon.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalTextPosition(textPosition:Number):Void;
    
    /**
     * Sets the horizontal position of the text relative to the icon.
     * @param textPosition one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalTextPosition(textPosition:Number):Void;
    
    /**
     * If both the icon and text properties are set, this property
     * defines the space between them.  
     * <p>
     * The default value of this property is 4 pixels.
     * 
     * @see #getIconTextGap()
     */
    public function setIconTextGap(iconTextGap:Number):Void;
	
	/**
	 * Sets space for margin between the border and
     * the content.
     */
	public function setMargin(m:Insets):Void;
	/**
	 * The component represent the header and can fire the selection event 
	 * through ON_RELEASE event, and focused the accordion when ON_PRESS.<p>
	 * To ensure the accordion will be focused when the header component was 
	 * pressed, so the component will be set to not focusable.
	 */
	public function getComponent():Component;
}