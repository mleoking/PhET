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
 * @author iiley
 */
class org.aswing.colorchooser.VerticalLayout extends EmptyLayout {
	
    /**
     * This value indicates that each row of components
     * should be left-justified.
     */
    public static var LEFT:Number 	= 0;

    /**
     * This value indicates that each row of components
     * should be centered.
     */
    public static var CENTER:Number 	= 1;

    /**
     * This value indicates that each row of components
     * should be right-justified.
     */
    public static var RIGHT:Number 	= 2;	
	
	private var align:Number;
	private var gap:Number;
	
	public function VerticalLayout(align:Number, gap:Number){
		this.align = (align == undefined ? LEFT : align);
		this.gap   = (gap == undefined ? 0 : gap);
	}
	
    
    /**
     * Sets new gap.
     * @param get new gap
     */	
    public function setGap(gap:Number):Void {
    	this.gap = (gap == undefined ? 0 : gap);
    }
    
    /**
     * Gets gap.
     * @return gap
     */
    public function getGap():Number {
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
    public function setAlign(align:Number):Void{
    	this.align = (align == undefined ? LEFT : align);
    }
    
    /**
     * Returns the align.
     * @return the align
     */
    public function getAlign():Number{
    	return align;
    }
   	
	/**
	 * Returns preferredLayoutSize;
	 */
    public function preferredLayoutSize(target:Container):Dimension{
    	var count:Number = target.getComponentCount();
    	var insets:Insets = target.getInsets();
    	var width:Number = 0;
    	var height:Number = 0;
    	for(var i:Number=0; i<count; i++){
    		var c:Component = target.getComponent(i);
    		if(c.isVisible()){
	    		var size:Dimension = c.getPreferredSize();
	    		width = Math.max(width, size.width);
	    		var g:Number = i > 0 ? gap : 0;
	    		height += (size.height + g);
    		}
    	}
    	
    	var dim:Dimension = new Dimension(width, height);
    	return dim;
    }
        
	/**
	 * Returns minimumLayoutSize;
	 */
    public function minimumLayoutSize(target:Container):Dimension{
    	return target.getInsets().getOutsideSize();
    }
    
    /**
     * do nothing
     */
    public function layoutContainer(target:Container):Void{
    	var count:Number = target.getComponentCount();
    	var size:Dimension = target.getSize();
    	var insets:Insets = target.getInsets();
    	var rd:Rectangle = insets.getInsideBounds(size.getBounds());
    	var ch:Number = rd.height;
    	var cw:Number = rd.width;
    	var x:Number = rd.x;
    	var y:Number = rd.y;
		var right:Number = x + cw;
    	for(var i:Number=0; i<count; i++){
    		var c:Component = target.getComponent(i);
    		if(c.isVisible()){
	    		var ps:Dimension = c.getPreferredSize();
	    		if(align == RIGHT){
    				c.setBounds(right - ps.width, y, ps.width, ps.height);
	    		}else if(align == CENTER){
	    			c.setBounds(x + cw/2 - ps.width/2, y, ps.width, ps.height);
	    		}else{
	    			c.setBounds(x, y, ps.width, ps.height);
	    		}
    			y += ps.height + gap;
    		}
    	}
    }
    
	/**
	 * return 0.5
	 */
    public function getLayoutAlignmentX(target:Container):Number{
    	return 0.5;
    }

	/**
	 * return 0.5
	 */
    public function getLayoutAlignmentY(target:Container):Number{
    	return 0.5;
    }   
}