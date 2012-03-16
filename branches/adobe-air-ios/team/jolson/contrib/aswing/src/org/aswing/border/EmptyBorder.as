/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.border.Border;
import org.aswing.border.DecorateBorder;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.Insets;

/**
 * EmptyBorder not draw any graphics, only use to hold a blank space around component.
 * @author iiley
 */
class org.aswing.border.EmptyBorder extends DecorateBorder{
	private var insets:Insets;
	
	public function EmptyBorder(interior:Border, insets:Insets){
		super(interior);
		this.insets = new Insets();
		this.insets.addInsets(insets);
	}
	
    public function getBorderInsetsImp(c:Component, bounds:Rectangle):Insets{
    	return getInsets();
    }
    
	public function getInsets():Insets {
		return new Insets(insets.top, insets.left, insets.bottom, insets.right);
	}

	public function setInsets(insets:Insets):Void {
		this.insets = new Insets(insets.top, insets.left, insets.bottom, insets.right);
	}    
}
