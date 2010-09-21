/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.FlowLayout;
import org.aswing.geom.Dimension;
import org.aswing.Insets;
import org.aswing.LayoutManager;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.frame.TitleBarLayout extends FlowLayout {
	
	private static var ICON_TITLE_WIDTH:Number = 50;
	private static var ICON_TITLE_HEIGHT:Number = 20;
	
	//shared instance
	private static var instance:LayoutManager;
	public static function createInstance():LayoutManager{
		if(instance == null){
			instance = new TitleBarLayout();
		}
		return instance;
	}
	
	public function TitleBarLayout() {
		super(FlowLayout.RIGHT, 4, 4);
	}
	
	public function getHorizontalGap():Number{
		return getHgap();
	}
	
	private function fitSize(size:Dimension):Dimension{
    	size.change(ICON_TITLE_WIDTH, 0);
    	size.height = Math.max(size.height, ICON_TITLE_HEIGHT);
    	return size;
	}
    
    public function preferredLayoutSize(target:Container):Dimension {
		var dim:Dimension = new Dimension(0, 0);
		var nmembers:Number = target.getComponentCount();

		var counted:Number = 0;
		for (var i:Number = 0 ; i < nmembers ; i++) {
	    	var m:Component = target.getComponent(i);
			var d:Dimension = m.getPreferredSize();
			dim.height = Math.max(dim.height, d.height);
	    	if (m.isVisible()) {
                if (counted > 0) {
                    dim.width += hgap;
                }
				dim.width += d.width;
				counted ++;
	    	}
		}
		var insets:Insets = target.getInsets();
		dim.width += insets.left + insets.right + hgap*2;
		dim.height += insets.top + insets.bottom + vgap*2;
		return fitSize(dim);
    }

    public function minimumLayoutSize(target:Container):Dimension {
		return preferredLayoutSize(target);
    }    
}
