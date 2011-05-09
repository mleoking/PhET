/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing
{
	
import org.aswing.plaf.*;
import org.aswing.error.*;
	
/**
 * Reserved for look and feel implementation.
 */	
public class LookAndFeel
{
	/**
	 * Should override in sub-class to return a defaults.
	 */
	public function getDefaults():UIDefaults{
		throw new ImpMissError();
		return null;
	}
	
    /**
     * Convenience method for initializing a component's basic properties
     *  values from the current defaults table.  
     * 
     * @param c the target component for installing default color properties
     * @param componentUIPrefix the key for the default component UI Prefix
     * @see org.aswing.Component#setOpaque()
     * @see org.aswing.Component#setOpaqueSet()
     */	
	public static function installBasicProperties(c:Component, componentUIPrefix:String,
		defaultOpaquerName:String="opaque", defaultFocusableName:String="focusable"):void{
		if(!c.isOpaqueSet()){
			c.setOpaque(c.getUI().getBoolean(componentUIPrefix + defaultOpaquerName));
			c.setOpaqueSet(false);
		}
		if(!c.isFocusableSet()){
			c.setFocusable(c.getUI().getBoolean(componentUIPrefix + defaultFocusableName));
			c.setFocusableSet(false);
		}
	}
	
    /**
     * Convenience method for initializing a component's foreground
     * and background color properties with values from the current
     * defaults table.  The properties are only set if the current
     * value is either null or a UIResource.
     * 
     * @param c the target component for installing default color properties
     * @param defaultBgName the key for the default background
     * @param defaultFgName the key for the default foreground
     * 
     * @see UIManager#getColor()
     */
    public static function installColors(c:Component, componentUIPrefix:String,
    	defaultBgName:String="background", defaultFgName:String="foreground"):void{
        var bg:ASColor = c.getBackground();
		if (bg == null || bg is UIResource) {
	    	c.setBackground(c.getUI().getColor(componentUIPrefix + defaultBgName));
		}

        var fg:ASColor = c.getForeground();
		if (fg == null || fg is UIResource) {
	    	c.setForeground(c.getUI().getColor(componentUIPrefix + defaultFgName));
		}
    }
    
    /**
     * Convenience method for initializing a component's font with value from 
     * the current defaults table.  The property are only set if the current
     * value is either null or a UIResource.
     * 
     * @param c the target component for installing default font property
     * @param defaultFontName the key for the default font
     * 
     * @see UIManager#getFont()
     */    
    public static function installFont(c:Component, componentUIPrefix:String, 
    	defaultFontName:String="font"):void{
    	var f:ASFont = c.getFont();
		if (f == null || f is UIResource) {
			//trace(defaultFontName + " : " + UIManager.getFont(defaultFontName));
	    	c.setFont(c.getUI().getFont(componentUIPrefix + defaultFontName));
		}
    }
    
    /**
     * @see #installColors()
     * @see #installFont()
     */
    public static function installColorsAndFont(c:Component, componentUIPrefix:String,
    	defaultBgName:String="background", defaultFgName:String="foreground", defaultFontName:String="font"):void{
    	installColors(c, componentUIPrefix, defaultBgName, defaultFgName);
    	installFont(c, componentUIPrefix, defaultFontName);
    }
	
    /**
     * Convenience method for installing a component's default Border , background decorator and foreground decorator
     * object on the specified component if either the border is 
     * currently null or already an instance of UIResource.
     * @param c the target component for installing default border
     * @param defaultBorderName the key specifying the default border
     */
    public static function installBorderAndBFDecorators(c:Component, componentUIPrefix:String,
   		 defaultBorderName:String="border", defaultBGDName:String="bg", defaultFGDName:String="fg"):void{
        var b:Border = c.getBorder();
        if (b is UIResource) {
            c.setBorder(c.getUI().getBorder(componentUIPrefix + defaultBorderName));
        }
        var bg:GroundDecorator = c.getBackgroundDecorator();
        if(bg is UIResource){
        	c.setBackgroundDecorator(c.getUI().getGroundDecorator(componentUIPrefix + defaultBGDName));
        }
        var fg:GroundDecorator = c.getForegroundDecorator();
        if(fg is UIResource){
        	c.setForegroundDecorator(c.getUI().getGroundDecorator(componentUIPrefix + defaultFGDName));
        }
    }

    /**
     * Convenience method for un-installing a component's default 
     * border, background decorator and foreground decorator on the specified component if the border is 
     * currently an instance of UIResource.
     * @param c the target component for uninstalling default border
     */
    public static function uninstallBorderAndBFDecorators(c:Component):void{
        if (c.getBorder() is UIResource) {
            c.setBorder(DefaultEmptyDecoraterResource.INSTANCE);
        }
        if (c.getBackgroundDecorator() is UIResource) {
            c.setBackgroundDecorator(DefaultEmptyDecoraterResource.INSTANCE);
        }
        if (c.getForegroundDecorator() is UIResource) {
            c.setForegroundDecorator(DefaultEmptyDecoraterResource.INSTANCE);
        }                
    }
}

}