/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASWingConstants;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.Insets;

/**
 * SoftBoxLayout layout the components to a list as same width/height but their preffered height/width.
 * 
 * @see BoxLayout
 * @author iiley
 */
class org.aswing.SoftBoxLayout extends EmptyLayout{
	
    /**
     * Specifies that components should be laid out left to right.
     */
    public static var X_AXIS:Number = 0;
    
    /**
     * Specifies that components should be laid out top to bottom.
     */
    public static var Y_AXIS:Number = 1;
    
    
    /**
     * This value indicates that each row of components
     * should be left-justified(X_AXIS)/top-justified(Y_AXIS).
     */
    public static var LEFT:Number 	= ASWingConstants.LEFT;

    /**
     * This value indicates that each row of components
     * should be centered.
     */
    public static var CENTER:Number = ASWingConstants.CENTER;

    /**
     * This value indicates that each row of components
     * should be right-justified(X_AXIS)/bottom-justified(Y_AXIS).
     */
    public static var RIGHT:Number 	= ASWingConstants.RIGHT;
    
    /**
     * This value indicates that each row of components
     * should be left-justified(X_AXIS)/top-justified(Y_AXIS).
     */
    public static var TOP:Number 	= ASWingConstants.TOP;

    /**
     * This value indicates that each row of components
     * should be right-justified(X_AXIS)/bottom-justified(Y_AXIS).
     */
    public static var BOTTOM:Number = ASWingConstants.BOTTOM;
    
    
    private var axis:Number;
    private var gap:Number;
    private var align:Number;
    
    /**
     * <br>
     * BoxLayout(axis:Number)<br>
     * BoxLayout(axis:Number)<br>
     * @param axis the layout axis, default X_AXIS
     * @param gap (optional)the gap between each component, default 0
     * @param align (optional)the alignment value, default is LEFT
     * @see #X_AXIS
     * @see #Y_AXIS
     */
    public function SoftBoxLayout(axis:Number, gap:Number, align:Number){
    	setAxis(axis);
    	setGap(gap);
    	setAlign(align);
    }
    	
    /**
     * Sets new axis. Must be one of:
     * <ul>
     *  <li>X_AXIS
     *  <li>Y_AXIS
     * </ul> Default is X_AXIS.
     * @param axis new axis
     */
    public function setAxis(axis:Number):Void {
    	this.axis = (axis == undefined ? X_AXIS : axis);
    }
    
    /**
     * Gets axis.
     * @return axis
     */
    public function getAxis():Number {
    	return axis;	
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
    	var wTotal:Number = 0;
    	var hTotal:Number = 0;
    	for(var i:Number=0; i<count; i++){
    		var c:Component = target.getComponent(i);
    		if(c.isVisible()){
	    		var size:Dimension = c.getPreferredSize();
	    		width = Math.max(width, size.width);
	    		height = Math.max(height, size.height);
	    		var g:Number = i > 0 ? gap : 0;
	    		wTotal += (size.width + g);
	    		hTotal += (size.height + g);
    		}
    	}
    	if(axis == Y_AXIS){
    		height = hTotal;
    	}else{
    		width = wTotal;
    	}
    	
    	var dim:Dimension = new Dimension(width, height);
    	return insets.getOutsideSize(dim);;
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
    	if(align == RIGHT || align == BOTTOM){
    		if(axis == Y_AXIS){
    			y = y + ch;
    		}else{
    			x = x + cw;
    		}
	    	for(var i:Number=count-1; i>=0; i--){
	    		var c:Component = target.getComponent(i);
	    		if(c.isVisible()){
		    		var ps:Dimension = c.getPreferredSize();
		    		if(axis == Y_AXIS){
		    			y -= ps.height;
		    			c.setBounds(x, y, cw, ps.height);
		    			y -= gap;
		    		}else{
		    			x -= ps.width;
		    			c.setBounds(x, y, ps.width, ch);
		    			x -= gap;
		    		}
	    		}
	    	}
    		
    	}else{//left or top or center
	    	if(align == CENTER){
	    		var prefferedSize:Dimension = insets.getInsideSize(target.getPreferredSize());
	    		if(axis == Y_AXIS){
	    			y = Math.round(y + (ch - prefferedSize.height)/2);
	    		}else{
	    			x = Math.round(x + (cw - prefferedSize.width)/2);
	    		}
	    	}
	    	for(var i:Number=0; i<count; i++){
	    		var c:Component = target.getComponent(i);
	    		if(c.isVisible()){
		    		var ps:Dimension = c.getPreferredSize();
		    		if(axis == Y_AXIS){
		    			c.setBounds(x, y, cw, ps.height);
		    			y += (ps.height + gap);
		    		}else{
		    			c.setBounds(x, y, ps.width, ch);
		    			x += (ps.width + gap);
		    		}
	    		}
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
