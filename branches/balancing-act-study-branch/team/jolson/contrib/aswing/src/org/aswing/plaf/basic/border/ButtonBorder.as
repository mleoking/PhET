/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractButton;
import org.aswing.ASColor;
import org.aswing.border.Border;
import org.aswing.ButtonModel;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.Insets;
import org.aswing.plaf.basic.BasicGraphicsUtils;
import org.aswing.plaf.UIResource;
import org.aswing.UIDefaults;
import org.aswing.UIManager;

/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.border.ButtonBorder implements Border, UIResource{
	
    private var shadow:ASColor;
    private var darkShadow:ASColor;
    private var highlight:ASColor;
    private var lightHighlight:ASColor;
	
	private static var instance:ButtonBorder;
	/**
	 * this make shared instance and construct when use.
	 */	
	public static function createInstance():Border{
		if(instance == null){
			instance = new ButtonBorder();
		}else{
			instance.reloadColors();
		}
		return instance;
	}
	
	private function ButtonBorder(){
		super();
		reloadColors();
	}
	
	private function reloadColors():Void{
		var table:UIDefaults = UIManager.getLookAndFeelDefaults();
		shadow = table.getColor("Button.shadow");
		darkShadow = table.getColor("Button.darkShadow");
		highlight = table.getColor("Button.light");
		lightHighlight = table.getColor("Button.highlight");
	}
	
	/**
	 * paint the ButtonBorder content.
	 */
    public function paintBorder(c:Component, g:Graphics, bounds:Rectangle):Void{
    	var isPressed:Boolean = false;
		if(c instanceof AbstractButton){
			var model:ButtonModel = (AbstractButton(c)).getModel();
			isPressed = model.isPressed() || model.isSelected();
		}
		BasicGraphicsUtils.drawBezel(g, bounds, isPressed, shadow,
                                   darkShadow, highlight, lightHighlight);
    }
	
	public function getBorderInsets(c:Component, bounds:Rectangle):Insets{
    	return new Insets(2, 2, 2, 2);
    }
    
    public function uninstallBorder(c:Component):Void{
    }
	
}
