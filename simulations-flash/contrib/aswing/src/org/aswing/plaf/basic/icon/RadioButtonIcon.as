/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.ButtonModel;
import org.aswing.Component;
import org.aswing.graphics.GradientBrush;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
import org.aswing.JRadioButton;
import org.aswing.plaf.UIResource;
import org.aswing.UIDefaults;
import org.aswing.UIManager;
 
/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.icon.RadioButtonIcon implements Icon, UIResource{
    private var shadow:ASColor;
    private var darkShadow:ASColor;
    private var highlight:ASColor;
    private var lightHighlight:ASColor;

	private static var instance:RadioButtonIcon;
	/**
	 * this make shared instance and construct when use.
	 */	
	public static function createInstance():Icon{
		if(instance == null){
			instance = new RadioButtonIcon();
		}else{
			instance.reloadColors();
		}
		return instance;
	}

	private function RadioButtonIcon(){
		super();
		reloadColors();
	}
	
	private function reloadColors():Void{
		var table:UIDefaults = UIManager.getLookAndFeelDefaults();
		shadow = table.getColor("RadioButton.shadow");
		darkShadow = table.getColor("RadioButton.darkShadow");
		highlight = table.getColor("RadioButton.light");
		lightHighlight = table.getColor("RadioButton.highlight");
	}

	public function getIconWidth():Number{
		return 13;
	}

	public function getIconHeight():Number{
		return 13;
	}

	public function paintIcon(com:Component, g:Graphics, x:Number, y:Number):Void{
		var rb:JRadioButton = JRadioButton(com);
		var model:ButtonModel = rb.getModel();
		var drawDot:Boolean = model.isSelected();
		
		var periphery:ASColor = darkShadow;
		var middle:ASColor = highlight;
		var inner:ASColor = shadow;
		var dot:ASColor = rb.getForeground();
		
		// Set up colors per RadioButtonModel condition
		if (!model.isEnabled()) {
			periphery = middle = inner = rb.getBackground();
			dot = darkShadow;
		} else if (model.isPressed()) {
			periphery = shadow;
			inner = darkShadow;
		}
		
		var w:Number = getIconWidth();
		var h:Number = getIconHeight();
		var cx:Number = x + w/2;
		var cy:Number = y + h/2;
		var xr:Number = w/2;
		var yr:Number = h/2;
		
		var brush:SolidBrush=new SolidBrush(darkShadow);
		g.fillEllipse(brush, x, y, w, h);
		brush.setASColor(highlight);
		g.fillEllipse(brush, x+1, y+1, w-2, h-2);
        
        var colors:Array = [0xDCDBD8, 0xffffff];
		var alphas:Array = [100, 100];
		var ratios:Array = [0, 255];
		var matrix:Object = {matrixType:"box", x:x+2, y:y+2, w:w-3, h:h-3, r:(45/180)*Math.PI};        
	    var gbrush:GradientBrush=new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
	    g.fillEllipse(gbrush, x+2, y+2, w-4, h-4);
		
		if(drawDot){
			xr = w/5;
			yr = h/5;
			brush = new SolidBrush(dot);
			g.fillEllipse(brush, cx-xr, cy-yr, xr*2, yr*2);			
		}
	}
	public function uninstallIcon(com:Component):Void{
	}
}
