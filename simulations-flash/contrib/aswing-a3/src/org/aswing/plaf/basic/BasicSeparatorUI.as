/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic
{
import org.aswing.*;
import org.aswing.error.ImpMissError;
import org.aswing.geom.IntDimension;
import org.aswing.geom.IntRectangle;
import org.aswing.graphics.Graphics2D;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.plaf.BaseComponentUI;
/**
 * A Basic L&F implementation of SeparatorUI.  This implementation 
 * is a "combined" view/controller.
 *
 * @author senkay
 * @private
 */
public class BasicSeparatorUI extends BaseComponentUI
{
		
	public function BasicSeparatorUI(){
		super();
	}
	
    protected function getPropertyPrefix():String {
        return "Separator.";
    }
	
	override public function installUI(c:Component):void{
		installDefaults(JSeparator(c));
	}
	
	override public function uninstallUI(c:Component):void{
		uninstallDefaults(JSeparator(c));
	}
	
	public function installDefaults(s:JSeparator):void{
		var pp:String = getPropertyPrefix();
		
		LookAndFeel.installColors(s, pp);
		LookAndFeel.installBasicProperties(s, pp);
		LookAndFeel.installBorderAndBFDecorators(s, pp);
		s.setAlignmentX(0.5);
		s.setAlignmentY(0.5);
	}
	
	public function uninstallDefaults(s:JSeparator):void{
		LookAndFeel.uninstallBorderAndBFDecorators(s);
	}

	override public function paint(c:Component, g:Graphics2D, b:IntRectangle):void{
    	super.paint(c, g, b); 	
		var sp:JSeparator = JSeparator(c);
		if (sp.getOrientation() == JSeparator.VERTICAL){
			var pen:Pen = new Pen(c.getBackground().darker(), 1);
			g.drawLine(pen, b.x+0.5, b.y, b.x+0.5, b.y+b.height);
			pen.setColor(c.getBackground().brighter());
			g.drawLine(pen, b.x+1.5, b.y, b.x+1.5, b.y+b.height);
		}else{
			var pen2:Pen = new Pen(c.getBackground().darker(), 1);
			g.drawLine(pen2, b.x, b.y+0.5, b.x+b.width, b.y+0.5);
			pen2.setColor(c.getBackground().brighter());
			g.drawLine(pen2, b.x, b.y+1.5, b.x+b.width, b.y+1.5);
		}
	}
	
	override public function getPreferredSize(c:Component):IntDimension{
		var sp:JSeparator = JSeparator(c);
		var insets:Insets = sp.getInsets();
		if (sp.getOrientation() == JSeparator.VERTICAL){
			return insets.getOutsideSize(new IntDimension(2, 0));
		}else{
			return insets.getOutsideSize(new IntDimension(0, 2));
		}
	}
    override public function getMaximumSize(c:Component):IntDimension{
		var sp:JSeparator = JSeparator(c);
		var insets:Insets = sp.getInsets();
		var size:IntDimension = insets.getOutsideSize();
		if (sp.getOrientation() == JSeparator.VERTICAL){
			return new IntDimension(2 + size.width, 100000);
		}else{
			return new IntDimension(100000, 2 + size.height);
		}
    }
    
	override public function getMinimumSize(c:Component):IntDimension
	{
		return getPreferredSize(c);
	}    
}
}