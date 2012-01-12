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
 * A layout manager that allows multiple components to be laid out either vertically or 
 * horizontally. The components will not wrap so, for example, a vertical arrangement 
 * of components will stay vertically arranged when the frame is resized.
 * @author iiley
 */
class org.aswing.BoxLayout extends EmptyLayout{
	
    /**
     * Specifies that components should be laid out left to right.
     */
    public static var X_AXIS:Number = 0;
    
    /**
     * Specifies that components should be laid out top to bottom.
     */
    public static var Y_AXIS:Number = 1;
    
    
    private var axis:Number;
    private var gap:Number;
    
    /**
     * <br>
     * BoxLayout(axis:Number, gap:Number)<br>
     * BoxLayout(axis:Number)<br>
     * @param axis (optional)the layout axis, default is X_AXIS
     * @param gap (optional)the gap between children, default is 0
     * @see #X_AXIS
     * @see #X_AXIS
     */
    public function BoxLayout(axis:Number, gap:Number){
    	setAxis(axis);
    	setGap(gap);
    }
    
    /**
     * Sets new axis.
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
	 * return target.getSize();
	 */
    public function preferredLayoutSize(target:Container):Dimension{
    	var count:Number = target.getComponentCount();
    	var insets:Insets = target.getInsets();
    	var width:Number = 0;
    	var height:Number = 0;
    	var amount:Number = 0;
    	for(var i:Number=0; i<count; i++){
    		var c:Component = target.getComponent(i);
    		if(c.isVisible()){
	    		var size:Dimension = c.getPreferredSize();
	    		width = Math.max(width, size.width);
	    		height = Math.max(height, size.height);
    			amount++;
    		}
    	}
    	if(axis == Y_AXIS){
    		height = height*amount;
    		if(amount > 0){
    			height += (amount-1)*gap;
    		}
    	}else{
    		width = width*amount;
    		if(amount > 0){
    			width += (amount-1)*gap;
    		}
    	}
    	
    	var dim:Dimension = new Dimension(width, height);
    	return insets.getOutsideSize(dim);
    }

	/**
	 * target.getSize();
	 */
    public function minimumLayoutSize(target:Container):Dimension{
    	return target.getInsets().getOutsideSize();
    }
    
	/**
	 * return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
	 */
    public function maximumLayoutSize(target:Container):Dimension{
    	var count:Number = target.getComponentCount();
    	var insets:Insets = target.getInsets();
    	var width:Number = 0;
    	var height:Number = 0;
    	var amount:Number = 0;
    	for(var i:Number=0; i<count; i++){
    		var c:Component = target.getComponent(i);
    		if(c.isVisible()){
	    		var size:Dimension = c.getMaximumSize();
	    		width = Math.max(width, size.width);
	    		height = Math.max(height, size.height);
	    		amount++;
    		}
    	}
    	if(axis == Y_AXIS){
    		height = height*amount;
    		if(amount > 0){
    			height += (amount-1)*gap;
    		}
    	}else{
    		width = width*amount;
    		if(amount > 0){
    			width += (amount-1)*gap;
    		}
    	}
    	var dim:Dimension = new Dimension(width, height);
    	return insets.getOutsideSize(dim);
    }    
    
    public function layoutContainer(target:Container):Void{
    	var count:Number = target.getComponentCount();
    	var amount:Number = 0;
    	for(var i:Number=0; i<count; i++){
    		var c:Component = target.getComponent(i);
    		if(c.isVisible()){
	    		amount ++;
    		}
    	}
    	var size:Dimension = target.getSize();
    	var insets:Insets = target.getInsets();
    	var rd:Rectangle = insets.getInsideBounds(size.getBounds());
    	var ch:Number;
    	var cw:Number;
    	if(axis == Y_AXIS){
    		ch = Math.floor((rd.height - (amount-1)*gap)/amount);
    		cw = rd.width;
    	}else{
    		ch = rd.height;
    		cw = Math.floor((rd.width - (amount-1)*gap)/amount);
    	}
    	var x:Number = rd.x;
    	var y:Number = rd.y;
    	var xAdd:Number = (axis == Y_AXIS ? 0 : cw+gap);
    	var yAdd:Number = (axis == Y_AXIS ? ch+gap : 0);
    	for(var i:Number=0; i<count; i++){
    		var c:Component = target.getComponent(i);
    		if(c.isVisible()){
	    		c.setBounds(x, y, cw, ch);
	    		x += xAdd;
	    		y += yAdd;
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
