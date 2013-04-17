/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.Insets;

/**
 * Simple <code>LayoutManager</code> aligned the single contained
 * component by the container's center.
 * 
 * @author iiley
 * @author Igor Sadovskiy
 */
class org.aswing.CenterLayout extends EmptyLayout {
	
	public function CenterLayout(){
		super();
	}
	
	/**
	 * Calculates preferred layout size for the given container.
	 */
    public function preferredLayoutSize(target:Container):Dimension {
    	return ( (target.getComponentCount() > 0) ?
    		target.getInsets().getOutsideSize(target.getComponent(0).getPreferredSize()) :
    		target.getInsets().getOutsideSize() );
    }

    /**
     * Layouts component by center inside the given container. 
     *
     * @param target the container to lay out
     */
    public function layoutContainer(target:Container):Void{
        if (target.getComponentCount() > 0) {
	        var size:Dimension = target.getSize();
	        var insets:Insets = target.getInsets();
	        var rd:Rectangle = insets.getInsideBounds(size.getBounds());
	        
	        var c:Component = target.getComponent(0);
	        var preferSize:Dimension = c.getPreferredSize();
	        
	        var cd:Rectangle = new Rectangle();
	        cd.setLocation(rd.getLocation());
	        cd.setSize(rd.getSize().intersect(preferSize));
	        
	        if (rd.width > preferSize.width) {
	        	cd.x += (rd.width - preferSize.width) / 2;
	        }
	        if (rd.height > preferSize.height) {
	        	cd.y += (rd.height - preferSize.height) / 2;
	        }
	        
	     	c.setBounds(cd);   
        }
    }
}
