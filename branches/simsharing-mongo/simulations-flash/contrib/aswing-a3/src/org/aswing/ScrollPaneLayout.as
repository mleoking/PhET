/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing
{

import org.aswing.geom.*;

/**
 * The layout manager used by <code>JScrollPane</code>.  
 * <code>JScrollPaneLayout</code> is
 * responsible for three components: a viewportable's pane, two scrollbars.
 * <p>
 * @see JScrollPane
 * @see Viewportable
 * 
 * @author iiley
 */
public class ScrollPaneLayout extends EmptyLayout{
	
    /**
     * scrollbar are place at top and left
     */
    public static const TOP_LEFT:int = 3;
    /**
     * scrollbar are place at top and right
     */
    public static const TOP_RIGHT:int = 2;
	
    /**
     * scrollbar are place at bottom and left
     */
    public static const BOTTOM_LEFT:int = 1;
    /**
     * scrollbar are place at bottom and right
     */
    public static const BOTTOM_RIGHT:int = 0;
    	
	private var style:int;
	
	
	/**
	 * @param style how to place the scrollbars, default is BOTTOM_RIGHT
	 * @see #TOP_LEFT
	 * @see #TOP_RIGHT
	 * @see #BOTTOM_LEFT
	 * @see #BOTTOM_RIGHT
	 */
	public function ScrollPaneLayout(style:int=BOTTOM_RIGHT){
		this.style = style;
	}
	
    override public function minimumLayoutSize(target:Container):IntDimension{
    	if(target is JScrollPane){
    		var scrollPane:JScrollPane = JScrollPane(target);
    		var size:IntDimension = getScrollBarsSize(scrollPane);
    		var i:Insets = scrollPane.getInsets();
    		size = size.increaseSize(i.getOutsideSize());
	    	var viewport:Viewportable = scrollPane.getViewport();
	    	if(viewport != null){
	    		i = viewport.getViewportPane().getInsets();
	    		size.increaseSize(i.getOutsideSize());
	    		size.increaseSize(viewport.getViewportPane().getMinimumSize());
	    	}
	    	return size;
    	}else{
    		return super.minimumLayoutSize(target);
    	}
    }
    
    private function getScrollBarsSize(scrollPane:JScrollPane):IntDimension{
    	var vsb:JScrollBar = scrollPane.getVerticalScrollBar();
    	var hsb:JScrollBar = scrollPane.getHorizontalScrollBar();
    	var size:IntDimension = new IntDimension();
    	if(vsb != null && scrollPane.getVerticalScrollBarPolicy() == JScrollPane.SCROLLBAR_ALWAYS){
    		size.increaseSize(vsb.getPreferredSize());
    	}
    	if(hsb != null && scrollPane.getHorizontalScrollBarPolicy() == JScrollPane.SCROLLBAR_ALWAYS){
    		size.increaseSize(vsb.getPreferredSize());
    	}
    	return size;
    }
    
	/**
	 * return target.getSize();
	 */
    override public function preferredLayoutSize(target:Container):IntDimension{
    	if(target is JScrollPane){
	    	var scrollPane:JScrollPane = JScrollPane(target);
	    	var i:Insets = scrollPane.getInsets();
	    	var size:IntDimension = i.getOutsideSize();
	    	size.increaseSize(getScrollBarsSize(scrollPane));
	    	
	    	var viewport:Viewportable = scrollPane.getViewport();
	    	if(viewport != null){
	    		size.increaseSize(viewport.getViewportPane().getPreferredSize());
	    	}
	    	return size;
    	}else{
    		return super.preferredLayoutSize(target);
    	}
    }
	
    override public function layoutContainer(target:Container):void{
    	if(target is JScrollPane){
    		var scrollPane:JScrollPane = JScrollPane(target);
    		var viewport:Viewportable = scrollPane.getViewport();
    		var vScrollBar:JScrollBar = scrollPane.getVerticalScrollBar();
    		var hScrollBar:JScrollBar = scrollPane.getHorizontalScrollBar();

    		var fcd:IntDimension = scrollPane.getSize();
    		var insets:Insets = scrollPane.getInsets();
    		var cb:IntRectangle = insets.getInsideBounds(fcd.getBounds());
			
    		var vPreferSize:IntDimension = vScrollBar.getPreferredSize();
    		var hPreferSize:IntDimension = hScrollBar.getPreferredSize();
    		var vx:int, vy:int, vh:int, vw:int;
    		var hx:int, hy:int, hw:int, hh:int;
    		
    		var vpPane:Component = viewport.getViewportPane();
    		var wdis:int = 0;
    		var hdis:int = 0;
    		if(scrollPane.getHorizontalScrollBarPolicy() == JScrollPane.SCROLLBAR_ALWAYS){
    			hdis = hPreferSize.height;
    		}
    		if(scrollPane.getVerticalScrollBarPolicy() == JScrollPane.SCROLLBAR_ALWAYS){
    			wdis = vPreferSize.width;
    		}
    		//inital bounds
    		//trace("------------------------------------------------------");
			//trace("----------setViewportTestSize : " + new IntDimension(cb.width-wdis, cb.height-hdis));
    		viewport.setViewportTestSize(new IntDimension(cb.width-wdis, cb.height-hdis));
    		var showSize:IntDimension = viewport.getExtentSize();
    		var viewSize:IntDimension = viewport.getViewSize();
    		//trace("extentSize : " + showSize);
    		//trace("viewSize : " + viewSize);
    		vw = vPreferSize.width;
    		hh = hPreferSize.height;
    		if(scrollPane.getHorizontalScrollBarPolicy() == JScrollPane.SCROLLBAR_NEVER){
   				hScrollBar.setVisible(false);
    			hh = 0;
    		}else if(viewSize.width <= showSize.width){
    			if(hScrollBar.isEnabled())
    				hScrollBar.setEnabled(false);
    			if(scrollPane.getHorizontalScrollBarPolicy() != JScrollPane.SCROLLBAR_ALWAYS){
   					hScrollBar.setVisible(false);
    				hh = 0;
    				viewport.setViewPosition(new IntPoint(0, viewport.getViewPosition().y));    				
    			}else{
    				hScrollBar.setVisible(true); 	
    			}
    		}else{
   				hScrollBar.setVisible(true);
    			if(!hScrollBar.isEnabled())
    				hScrollBar.setEnabled(true);
    		}
    		if(hh != hdis){
				//trace("----------Shown HScrollBar setViewportTestSize : " + new IntDimension(cb.width, cb.height-hh));
    			viewport.setViewportTestSize(new IntDimension(cb.width, cb.height-hh));
    			showSize = viewport.getExtentSize();
    			viewSize = viewport.getViewSize();
	    		//trace("extentSize : " + showSize);
	    		//trace("viewSize : " + viewSize);
    		}
    		if(scrollPane.getVerticalScrollBarPolicy() == JScrollPane.SCROLLBAR_NEVER){
   				vScrollBar.setVisible(false);
    			vw = 0;
    		}else if(viewSize.height <= showSize.height){
    			vScrollBar.setEnabled(false);
    			if(scrollPane.getVerticalScrollBarPolicy() != JScrollPane.SCROLLBAR_ALWAYS){
   					vScrollBar.setVisible(false);
    				vw = 0;
    				viewport.setViewPosition(new IntPoint(viewport.getViewPosition().x, 0));  
    			}else{
   					vScrollBar.setVisible(true);
    			}
    		}else{
   				vScrollBar.setVisible(true);
    			if(!vScrollBar.isEnabled())
    				vScrollBar.setEnabled(true);    
    		}
    		
    		if(vw != wdis){
				//trace("----------Shown VScrollBar setViewportTestSize : " + new IntDimension(cb.width-vw, cb.height-hh));
    			viewport.setViewportTestSize(new IntDimension(cb.width-vw, cb.height-hh));
    			showSize = viewport.getExtentSize();
    			viewSize = viewport.getViewSize();
    			//trace("extentSize : " + showSize);
    			//trace("viewSize : " + viewSize);
    		}
    		if(viewSize.width > showSize.width && scrollPane.getHorizontalScrollBarPolicy() == JScrollPane.SCROLLBAR_AS_NEEDED){
    			if(!hScrollBar.isVisible()){
    				hScrollBar.setEnabled(true);
    				hScrollBar.setVisible(true);
    				hh = hPreferSize.height;
					//trace("----------Shown HScrollBar setViewportTestSize : " + new IntDimension(cb.width-vw, cb.height-hh));
    				viewport.setViewportTestSize(new IntDimension(cb.width-vw, cb.height-hh));
	    			showSize = viewport.getExtentSize();
	    			viewSize = viewport.getViewSize();
	    			//trace("extentSize : " + showSize);
	    			//trace("viewSize : " + viewSize);
    			}
    		}
    		
    		
    		var viewPortX:int = cb.x;
    		var viewPortY:int = cb.y;
    		
    		if(style == TOP_LEFT){
    			vx = cb.x;
    			vy = cb.y + hh;
    			vh = cb.height - hh;
    			
    			hx = cb.x + vw;
    			hy = cb.y;
    			hw = cb.width - vw;
    			
    			viewPortY += hh;
    			viewPortX += vw;
    		}else if(style == TOP_RIGHT){
    			vx = cb.x + cb.width - vw;
    			vy = cb.y + hh;
    			vh = cb.height - hh;
    			
    			hx = cb.x;
    			hy = cb.y;
    			hw = cb.width - vw;
    			
    			viewPortY += hh;
    		}else if(style == BOTTOM_LEFT){
    			vx = cb.x;
    			vy = cb.y;
    			vh = cb.height - hh;
    			
    			hx = cb.x + vw;
    			hy = cb.y + cb.height - hh;
    			hw = cb.width - vw;
    			
    			viewPortX += vw;
    		}else{
    			vx = cb.x + cb.width - vw;
    			vy = cb.y;
    			vh = cb.height - hh;
    			
    			hx = cb.x;
    			hy = cb.y + cb.height - hh;
    			hw = cb.width - vw;
    		}
    		if(vScrollBar.isVisible()){
    			vScrollBar.setComBoundsXYWH(vx, vy, vw, vh);
    		}
    		if(hScrollBar.isVisible()){
    			hScrollBar.setComBoundsXYWH(hx, hy, hw, hh);
    		}
    			
			//trace("----------setViewportTestSize final : " + new IntDimension(cb.width - vw, cb.height - hh));
    		vpPane.setComBoundsXYWH(viewPortX, viewPortY, cb.width - vw, cb.height - hh);
    		    		
			if(hScrollBar.isVisible()){
    			hScrollBar.setValues(Math.max(Math.min(hScrollBar.getValue(), viewSize.width - showSize.width), 0), showSize.width, 0, viewSize.width);
    			hScrollBar.setUnitIncrement(viewport.getHorizontalUnitIncrement());
    			hScrollBar.setBlockIncrement(viewport.getHorizontalBlockIncrement());
			}
			if(vScrollBar.isVisible()){
    			vScrollBar.setValues(Math.max(Math.min(vScrollBar.getValue(), viewSize.height - showSize.height), 0), showSize.height, 0, viewSize.height);
    			vScrollBar.setUnitIncrement(viewport.getVerticalUnitIncrement());
    			vScrollBar.setBlockIncrement(viewport.getVerticalBlockIncrement());
			}
    	}
    }
	
}

}