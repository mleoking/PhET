/*
 Copyright aswing.org, see the LICENCE.txt.
*/
package org.aswing
{
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.IntDimension;
import org.aswing.geom.IntRectangle;
import org.aswing.Insets;

/**
 * Simple <code>LayoutManager</code> aligned the single contained component by the container's center.
 * 
 * @author iiley
 * @author Igor Sadovskiy
 */
public class CenterLayout extends EmptyLayout
{
	public function CenterLayout(){
		super();
	}
	
	/**
	 * Calculates preferred layout size for the given container.
	 */
    override public function preferredLayoutSize(target:Container):IntDimension {
    	return ( (target.getComponentCount() > 0) ?
    		target.getInsets().getOutsideSize(target.getComponent(0).getPreferredSize()) :
    		target.getInsets().getOutsideSize());
    }

    /**
     * Layouts component by center inside the given container. 
     *
     * @param target the container to lay out
     */
    override public function layoutContainer(target:Container):void{
        if (target.getComponentCount() > 0) {
	        var size:IntDimension = target.getSize();
	        var insets:Insets = target.getInsets();
	        var rd:IntRectangle = insets.getInsideBounds(size.getBounds());
	        var c:Component = target.getComponent(0);
	        
	        var cd:IntRectangle = rd.clone();
	        var preferSize:IntDimension = c.getPreferredSize();
	        cd.setSize(preferSize);
	        
	        if (rd.width > preferSize.width) {
	        	cd.x += (rd.width - preferSize.width) / 2;
	        }
	        if (rd.height > preferSize.height) {
	        	cd.y += (rd.height - preferSize.height) / 2;
	        }
	     	cd.x = Math.round(cd.x);
	     	cd.y = Math.round(cd.y);
	     	c.setBounds(cd);   
        }
    }
}
}