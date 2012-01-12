/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.ButtonModel;
import org.aswing.Component;
import org.aswing.graphics.GradientBrush;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
import org.aswing.JCheckBox;
import org.aswing.plaf.UIResource;
import org.aswing.UIDefaults;
import org.aswing.UIManager;

/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.icon.CheckBoxIcon implements Icon, UIResource{
    private var shadow:ASColor;
    private var darkShadow:ASColor;
    private var highlight:ASColor;
    private var lightHighlight:ASColor;

	private static var instance:CheckBoxIcon;
	/**
	 * this make shared instance and construct when use.
	 */	
	public static function createInstance():Icon{
		if(instance == null){
			instance = new CheckBoxIcon();
		}else{
			instance.reloadColors();
		}
		return instance;
	}

	private function CheckBoxIcon(){
		super();
		reloadColors();
	}
	
	private function reloadColors():Void{
		var table:UIDefaults = UIManager.getLookAndFeelDefaults();
		shadow = table.getColor("CheckBox.shadow");
		darkShadow = table.getColor("CheckBox.darkShadow");
		highlight = table.getColor("CheckBox.light");
		lightHighlight = table.getColor("CheckBox.highlight");
	}

	public function getIconWidth():Number{
		return 13;
	}

	public function getIconHeight():Number{
		return 13;
	}

	public function paintIcon(com:Component, g:Graphics, x:Number, y:Number):Void{
		var rb:JCheckBox = JCheckBox(com);
		var model:ButtonModel = rb.getModel();
		var drawDot:Boolean = model.isSelected();
				
		var periphery:ASColor = darkShadow;
		var middle:ASColor = darkShadow;
		var inner:ASColor = shadow;
		var dot:ASColor = rb.getForeground();
		
		// Set up colors per RadioButtonModel condition
		if (!model.isEnabled()) {
			periphery = middle = inner = rb.getBackground();
			dot = darkShadow;;
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
		g.fillRectangle(brush,x, y, w, h);
		       
        brush.setASColor(highlight);
        g.fillRectangle(brush,x+1, y+1, w-2, h-2);
       
        var colors:Array = [0xDCDBD8, 0xffffff];
		var alphas:Array = [100, 100];
		var ratios:Array = [0, 255];
		var matrix:Object = {matrixType:"box", x:x+2, y:y+2, w:w-3, h:h-3, r:(45/180)*Math.PI};        
	    var gbrush:GradientBrush=new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
	    g.fillRectangle(gbrush,x+2,y+2,w-4,h-4);
       		
		if(drawDot){
			var pen:Pen = new Pen(dot, 2);
			g.drawLine(pen, cx-w/2+3, cy, cx-w/2/3, cy+h/2-3);
			g.drawLine(pen, cx-w/2/3, cy+h/2-1, cx+w/2, cy-h/2+1);
		}
	}
	public function uninstallIcon(com:Component):Void{
	}
}
