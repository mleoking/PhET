/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.Insets;
import org.aswing.JScrollBar;
import org.aswing.JScrollPane;
import org.aswing.Viewportable;

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
class org.aswing.ScrollPaneLayout extends EmptyLayout{
	
    /**
     * scrollbar are place at top and left
     */
    public static var TOP_LEFT:Number = 3;
    /**
     * scrollbar are place at top and right
     */
    public static var TOP_RIGHT:Number = 2;
	
    /**
     * scrollbar are place at bottom and left
     */
    public static var BOTTOM_LEFT:Number = 1;
    /**
     * scrollbar are place at bottom and right
     */
    public static var BOTTOM_RIGHT:Number = 0;
    	
	private var style:Number;
	
	
	/**
	 * @param style how to place the scrollbars, default is BOTTOM_RIGHT
	 * @see #TOP_LEFT
	 * @see #TOP_RIGHT
	 * @see #BOTTOM_LEFT
	 * @see #BOTTOM_RIGHT
	 */
	public function ScrollPaneLayout(style:Number){
		this.style = (style == undefined ? BOTTOM_RIGHT : style);
	}
	
    public function minimumLayoutSize(target:Container):Dimension{
    	if(target instanceof JScrollPane){
    		var scrollPane:JScrollPane = JScrollPane(target);
    		var size:Dimension = getScrollBarsSize(scrollPane);
    		var i:Insets = scrollPane.getInsets();
    		size = size.increase(i.getOutsideSize());
    		var vsb:JScrollBar = scrollPane.getVerticalScrollBar();
	    	var viewport:Viewportable = scrollPane.getViewport();
	    	if(viewport != null){
	    		i = viewport.getViewportPane().getInsets();
	    		size.increase(i.getOutsideSize());
	    		size.increase(viewport.getViewportPane().getMinimumSize());
	    	}
	    	return size;
    	}else{
    		super.minimumLayoutSize(target);
    	}
    }
    
    private function getScrollBarsSize(scrollPane:JScrollPane):Dimension{
    	var vsb:JScrollBar = scrollPane.getVerticalScrollBar();
    	var hsb:JScrollBar = scrollPane.getHorizontalScrollBar();
    	var size:Dimension = new Dimension();
    	if(vsb != null && scrollPane.getVerticalScrollBarPolicy() == JScrollPane.SCROLLBAR_ALWAYS){
    		size.increase(vsb.getPreferredSize());
    	}
    	if(hsb != null && scrollPane.getHorizontalScrollBarPolicy() == JScrollPane.SCROLLBAR_ALWAYS){
    		size.increase(vsb.getPreferredSize());
    	}
    	return size;
    }
    
	/**
	 * return target.getSize();
	 */
    public function preferredLayoutSize(target:Container):Dimension{
    	if(target instanceof JScrollPane){
	    	var scrollPane:JScrollPane = JScrollPane(target);
	    	var i:Insets = scrollPane.getInsets();
	    	var size:Dimension = i.getOutsideSize();
	    	size.increase(getScrollBarsSize(scrollPane));
	    	
	    	var viewport:Viewportable = scrollPane.getViewport();
	    	if(viewport != null){
	    		size.increase(viewport.getViewportPane().getPreferredSize());
	    	}
	    	return size;
    	}else{
    		return super.preferredLayoutSize(target);
    	}
    }
	
    public function layoutContainer(target:Container):Void{
    	if(target instanceof JScrollPane){
    		var scrollPane:JScrollPane = JScrollPane(target);
    		var viewport:Viewportable = scrollPane.getViewport();
    		var vScrollBar:JScrollBar = scrollPane.getVerticalScrollBar();
    		var hScrollBar:JScrollBar = scrollPane.getHorizontalScrollBar();

    		var fcd:Dimension = scrollPane.getSize();
    		var insets:Insets = scrollPane.getInsets();
    		var cb:Rectangle = insets.getInsideBounds(fcd.getBounds());
			
    		var vPreferSize:Dimension = vScrollBar.getPreferredSize();
    		var hPreferSize:Dimension = hScrollBar.getPreferredSize();
    		var vx:Number, vy:Number, vh:Number, vw:Number;
    		var hx:Number, hy:Number, hw:Number, hh:Number;
    		
    		var vpPane:Component = viewport.getViewportPane();
    		var wdis:Number = 0;
    		var hdis:Number = 0;
    		if(scrollPane.getHorizontalScrollBarPolicy() == JScrollPane.SCROLLBAR_ALWAYS){
    			hdis = hPreferSize.height;
    		}
    		if(scrollPane.getVerticalScrollBarPolicy() == JScrollPane.SCROLLBAR_ALWAYS){
    			wdis = vPreferSize.width;
    		}
    		//inital bounds
    		//trace("------------------------------------------------------");
			//trace("----------setViewportTestSize : " + new Dimension(cb.width-wdis, cb.height-hdis));
    		viewport.setViewportTestSize(new Dimension(cb.width-wdis, cb.height-hdis));
    		var showSize:Dimension = viewport.getExtentSize();
    		var viewSize:Dimension = viewport.getViewSize();
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
    			}else{
    				hScrollBar.setVisible(true); 	
    			}
    		}else{
   				hScrollBar.setVisible(true);
    			if(!hScrollBar.isEnabled())
    				hScrollBar.setEnabled(true);
    		}
    		if(hh != hdis){
				//trace("----------Shown HScrollBar setViewportTestSize : " + new Dimension(cb.width, cb.height-hh));
    			viewport.setViewportTestSize(new Dimension(cb.width, cb.height-hh));
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
    			}else{
   					vScrollBar.setVisible(true);
    			}
    		}else{
   				vScrollBar.setVisible(true);
    			if(!vScrollBar.isEnabled())
    				vScrollBar.setEnabled(true);    
    		}
    		
    		if(vw != wdis){
				//trace("----------Shown VScrollBar setViewportTestSize : " + new Dimension(cb.width-vw, cb.height-hh));
    			viewport.setViewportTestSize(new Dimension(cb.width-vw, cb.height-hh));
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
					//trace("----------Shown HScrollBar setViewportTestSize : " + new Dimension(cb.width-vw, cb.height-hh));
    				viewport.setViewportTestSize(new Dimension(cb.width-vw, cb.height-hh));
	    			showSize = viewport.getExtentSize();
	    			viewSize = viewport.getViewSize();
	    			//trace("extentSize : " + showSize);
	    			//trace("viewSize : " + viewSize);
    			}
    		}
    		
    		
    		var viewPortX:Number = cb.x;
    		var viewPortY:Number = cb.y;
    		
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
    			vScrollBar.setBounds(vx, vy, vw, vh);
    		}
    		if(hScrollBar.isVisible()){
    			hScrollBar.setBounds(hx, hy, hw, hh);
    		}
    			
			//trace("----------setViewportTestSize final : " + new Dimension(cb.width - vw, cb.height - hh));
    		vpPane.setBounds(viewPortX, viewPortY, cb.width - vw, cb.height - hh);
    		    		
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
