/*
 Copyright aswing.org, see the LICENCE.txt.
*/
package org.aswing.colorchooser { 
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.Insets;
import org.aswing.geom.*;

/**
 * @author iiley
 */
public class VerticalLayout extends EmptyLayout {
	
    /**
     * This value indicates that each row of components
     * should be left-justified.
     */
    public static const LEFT:int 	= 0;

    /**
     * This value indicates that each row of components
     * should be centered.
     */
    public static const CENTER:int 	= 1;

    /**
     * This value indicates that each row of components
     * should be right-justified.
     */
    public static const RIGHT:int 	= 2;	
	
	private var align:int;
	private var gap:int;
	
	public function VerticalLayout(align:int=LEFT, gap:int=0){
		this.align = align;
		this.gap   = gap;
	}
	
    
    /**
     * Sets new gap.
     * @param get new gap
     */	
    public function setGap(gap:int):void {
    	this.gap = gap;
    }
    
    /**
     * Gets gap.
     * @return gap
     */
    public function getGap():int {
    	return gap;	
    }
    
    /**
     * Sets new align. Must be one of:
     * <ul>
     *  <li>LEFT
     *  <li>RIGHT
     *  <li>CENTER
     *  <li>TOP
     *  <li>BOTTOM
     * </ul> Default is LEFT.
     * @param align new align
     */
    public function setAlign(align:int):void{
    	this.align = align;
    }
    
    /**
     * Returns the align.
     * @return the align
     */
    public function getAlign():int{
    	return align;
    }
   	
	/**
	 * Returns preferredLayoutSize;
	 */
    override public function preferredLayoutSize(target:Container):IntDimension{
    	var count:int = target.getComponentCount();
    	var width:int = 0;
    	var height:int = 0;
    	for(var i:int=0; i<count; i++){
    		var c:Component = target.getComponent(i);
    		if(c.isVisible()){
	    		var size:IntDimension = c.getPreferredSize();
	    		width = Math.max(width, size.width);
	    		var g:int = i > 0 ? gap : 0;
	    		height += (size.height + g);
    		}
    	}
    	
    	var dim:IntDimension = new IntDimension(width, height);
    	return dim;
    }
        
	/**
	 * Returns minimumLayoutSize;
	 */
    override public function minimumLayoutSize(target:Container):IntDimension{
    	return target.getInsets().getOutsideSize();
    }
    
    /**
     * do nothing
     */
    override public function layoutContainer(target:Container):void{
    	var count:Number = target.getComponentCount();
    	var size:IntDimension = target.getSize();
    	var insets:Insets = target.getInsets();
    	var rd:IntRectangle = insets.getInsideBounds(size.getBounds());
    	var cw:int = rd.width;
    	var x:int = rd.x;
    	var y:int = rd.y;
		var right:int = x + cw;
    	for(var i:int=0; i<count; i++){
    		var c:Component = target.getComponent(i);
    		if(c.isVisible()){
	    		var ps:IntDimension = c.getPreferredSize();
	    		if(align == RIGHT){
    				c.setBounds(new IntRectangle(right - ps.width, y, ps.width, ps.height));
	    		}else if(align == CENTER){
	    			c.setBounds(new IntRectangle(x + cw/2 - ps.width/2, y, ps.width, ps.height));
	    		}else{
	    			c.setBounds(new IntRectangle(x, y, ps.width, ps.height));
	    		}
    			y += ps.height + gap;
    		}
    	}
    }
    
	/**
	 * return 0.5
	 */
    override public function getLayoutAlignmentX(target:Container):Number{
    	return 0.5;
    }

	/**
	 * return 0.5
	 */
    override public function getLayoutAlignmentY(target:Container):Number{
    	return 0.5;
    }   
}
}