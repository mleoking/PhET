/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.Insets;
import org.aswing.JViewport;
import org.aswing.ViewportLayout;

/**
 * <code>CenterViewportLayout</code> acts like usual <code>ViewportLayout</code>,
 * but justifying view at viewport's center if view's bounds is less
 * than viewport's.
 * 
 * @author Igor Sadovskiy
 */
class test.CenterViewportLayout extends ViewportLayout {

    public function layoutContainer(parent:Container):Void{
		var vp:JViewport = JViewport(parent);
		var view:Component = vp.getView();
	
		if (view == null) {
		    return;
		}
	
		var insets:Insets = vp.getInsets();
		var extentSize:Dimension = vp.toScreenCoordinatesSize(vp.getExtentSize());
		var showBounds:Rectangle = new Rectangle(insets.left, insets.top, extentSize.width, extentSize.height);

		var viewSize:Dimension = vp.getViewSize();
	
		var viewPosition:Point = vp.toScreenCoordinatesLocation(vp.getViewPosition());
		viewPosition.x = Math.round(viewPosition.x);
		viewPosition.y = Math.round(viewPosition.y);
	
		/* justify
		 * the view when the width of the view is smaller than the
		 * container.
		 */
	    if((viewPosition.x + extentSize.width) > viewSize.width){
			viewPosition.x = Math.max(0, viewSize.width - extentSize.width);
	    }
	
		/* If the new viewport size would leave empty space below the
		 * view, bottom justify the view or top justify the view when
		 * the height of the view is smaller than the container.
		 */
		if ((viewPosition.y + extentSize.height) > viewSize.height) {
		    viewPosition.y = Math.max(0, viewSize.height - extentSize.height);
		}
	
		vp.setViewPosition(vp.toViewCoordinatesLocation(viewPosition));
		view.setSize(viewSize);
		
		// center view if its bounds less than viewport's bounds
		var viewLocation:Point = view.getLocation();
		
		if (showBounds.width > viewSize.width)
		{
			viewLocation.x = showBounds.width/2 - viewSize.width/2;
		} 
		if (showBounds.height > viewSize.height)
		{
			viewLocation.y = showBounds.height/2 - viewSize.height/2;
		} 
		
		view.setLocationImmediately(viewLocation);
    }
}
