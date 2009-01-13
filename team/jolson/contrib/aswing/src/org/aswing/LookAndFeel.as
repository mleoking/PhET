/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.border.Border;
import org.aswing.Component;
import org.aswing.plaf.UIResource;
import org.aswing.UIDefaults;
import org.aswing.UIManager;

/**
 * Reserved for look and feel implementation.
 */
class org.aswing.LookAndFeel{
	
	/**
	 * Should override in sub-class to return a defaults.
	 */
	public function getDefaults():UIDefaults{
		return null;
	}
	
    /**
     * Convenience method for initializing a component's basic properties
     *  values from the current defaults table.  
     * The properties are only set for uiProperties means that they will be 
     * only used when user are not set them by call component.setXxxx() method.
     * 
     * @param c the target component for installing default color properties
     * @param componentUIPrefix the key for the default component UI Prefix
     */	
	public static function installBasicProperties(c:Component, componentUIPrefix:String):Void{
		c.setUIProperty("opaque", UIManager.get(componentUIPrefix + "opaque"));
		c.setUIProperty("focusable", UIManager.get(componentUIPrefix + "focusable"));
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
     * @see UIManager#getColor
     */
    public static function installColors(c:Component, defaultBgName:String, defaultFgName:String):Void{
        var bg:ASColor = c.getBackground();
		if (bg === undefined || bg instanceof UIResource) {
	    	c.setBackground(UIManager.getColor(defaultBgName));
		}

        var fg:ASColor = c.getForeground();
		if (fg === undefined || fg instanceof UIResource) {
	    	c.setForeground(UIManager.getColor(defaultFgName));
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
     * @see UIManager#getFont
     */    
    public static function installFont(c:Component, defaultFontName:String):Void{
    	var f:ASFont = c.getFont();
		if (f === undefined || f instanceof UIResource) {
			//trace(defaultFontName + " : " + UIManager.getFont(defaultFontName));
	    	c.setFont(UIManager.getFont(defaultFontName));
		}
    }
    
    /**
     * @see #installColors
     * @see #installFont
     */
    public static function installColorsAndFont(c:Component, defaultBgName:String, defaultFgName:String, defaultFontName:String):Void{
    	installColors(c, defaultBgName, defaultFgName);
    	installFont(c, defaultFontName);
    }
	
    /**
     * Convenience method for installing a component's default Border 
     * object on the specified component if either the border is 
     * currently null or already an instance of UIResource.
     * @param c the target component for installing default border
     * @param defaultBorderName the key specifying the default border
     */
    public static function installBorder(c:Component, defaultBorderName:String):Void{
        var b:Border = c.getBorder();
        if (b === undefined || b instanceof UIResource) {
            c.setBorder(UIManager.getBorder(defaultBorderName));
        }
    }

    /**
     * Convenience method for un-installing a component's default 
     * border on the specified component if the border is 
     * currently an instance of UIResource.
     * @param c the target component for uninstalling default border
     */
    public static function uninstallBorder(c:Component):Void{
        if (c.getBorder() instanceof UIResource) {
            c.setBorder(undefined);
        }
    }	
		
}
