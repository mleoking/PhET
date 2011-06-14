/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.Insets;
import org.aswing.overflow.JViewport;

/**
 * The default layout manager for <code>JViewport</code>. 
 * <code>ViewportLayout</code> defines
 * a policy for layout that should be useful for most applications.
 * The viewport makes its view the same size as the viewport,
 * however it will not make the view smaller than its minimum size.
 * As the viewport grows the view is kept bottom justified until
 * the entire view is visible, subsequently the view is kept top
 * justified.
 * 
 * @author iiley
 */
class org.aswing.overflow.ViewportLayout extends EmptyLayout {
    /**
     * Returns the preferred dimensions for this layout given the components
     * in the specified target container.
     * @param parent the component which needs to be laid out
     * @return a <code>Dimension</code> object containing the
     *		preferred dimensions
     * @see #minimumLayoutSize
     */
    public function preferredLayoutSize(parent:Container):Dimension {
		var vp:JViewport = JViewport(parent);
		var viewPreferSize:Dimension = null;
		if(vp.getView() != null){
			viewPreferSize = vp.getView().getPreferredSize();
		}else{
			viewPreferSize = new Dimension(0, 0);
		}
		return vp.getViewportPane().getInsets().getOutsideSize(viewPreferSize);
    }

    /**
     * Called by the AWT when the specified container needs to be laid out.
     *
     * @param parent  the container to lay out
     */
    public function layoutContainer(parent:Container):Void{
		var vp:JViewport = JViewport(parent);
		var view:Component = vp.getView();
	
		if (view == null) {
		    return;
		}
	
		/* All of the dimensions below are in view coordinates, except
		 * vpSize which we're converting.
		 */
	
		var insets:Insets = vp.getInsets();
		//var viewPrefSize:Dimension = view.getPreferredSize();
		var vpSize:Dimension = vp.getSize();
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
	
		/* If we haven't been advised about how the viewports size 
		 * should change wrt to the viewport.
		 */
//		var vo:Object = view;
//		if(vo.isTracksViewportHeight() == true){
//		    if ((viewPosition.y == 0)/* && (extentSize.height > viewPrefSize.height)*/) {
//		      	viewSize.height = extentSize.height;
//		    }
//		}
//		if(vo.isTracksViewportWidth() == true){
//		    if ((viewPosition.x == 0)/* && (extentSize.width > viewPrefSize.width)*/) {
//		       	viewSize.width = extentSize.width;
//		    }
//		}
		vp.setViewPosition(vp.toViewCoordinatesLocation(viewPosition));
		view.setSize(viewSize);
		//trace("set View Pos : " + viewPosition);
		//trace("set View Size : " + viewSize);
    }
}
