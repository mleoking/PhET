/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.tabbedpane{

import org.aswing.*;

/**
 * TabbedPane Tab
 * @author iiley
 * @private
 */
public interface Tab{
	
	/**
	 * Inits the tab
	 * @param owner the tab owner component
	 */
	function initTab(owner:Component):void;
	
	/**
	 * Sets text and icon to the header
	 */
	function setTextAndIcon(text:String, icon:Icon):void;
	
	/**
	 * Sets the font of the tab text
	 */
	function setFont(font:ASFont):void;
	
	/**
	 * Sets the text color of the tab
	 */
	function setForeground(color:ASColor):void;
	
	/**
	 * Sets whether it is selected
	 */
	function setSelected(b:Boolean):void;
	
    /**
     * Sets the vertical alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>AsWingConstants.CENTER (the default)</li>
     * <li>AsWingConstants.TOP</li>
     * <li>AsWingConstants.BOTTOM</li>
     * </ul>
     */
    function setVerticalAlignment(alignment:int):void;
    
    /**
     * Sets the horizontal alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>AsWingConstants.RIGHT (the default)</li>
     * <li>AsWingConstants.LEFT</li>
     * <li>AsWingConstants.CENTER</li>
     * </ul>
     */
    function setHorizontalAlignment(alignment:int):void;
    
    /**
     * Sets the vertical position of the text relative to the icon.
     * @param alignment  one of the following values:
     * <ul>
     * <li>AsWingConstants.CENTER (the default)</li>
     * <li>AsWingConstants.TOP</li>
     * <li>AsWingConstants.BOTTOM</li>
     * </ul>
     */
    function setVerticalTextPosition(textPosition:int):void;
    
    /**
     * Sets the horizontal position of the text relative to the icon.
     * @param textPosition one of the following values:
     * <ul>
     * <li>AsWingConstants.RIGHT (the default)</li>
     * <li>AsWingConstants.LEFT</li>
     * <li>AsWingConstants.CENTER</li>
     * </ul>
     */
    function setHorizontalTextPosition(textPosition:int):void;
    
    /**
     * If both the icon and text properties are set, this property
     * defines the space between them.  
     * <p>
     * The default value of this property is 4 pixels.
     * 
     * @see #getIconTextGap()
     */
    function setIconTextGap(iconTextGap:int):void;
	
	/**
	 * Sets space for margin between the border and
     * the content.
     */
	function setMargin(m:Insets):void;
	
	/**
	 * The component represent the header and can fire the selection event 
	 * through <code>MouseEvent.CLICK</code> event.
	 */
	function getTabComponent():Component;
	
}
}