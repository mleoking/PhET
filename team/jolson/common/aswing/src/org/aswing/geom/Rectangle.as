/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.geom.Dimension;
import org.aswing.geom.Point;

/**
 * A Rectangle with x, y coordinats and with, height ranges.
 * @author iiley
 */
class org.aswing.geom.Rectangle{
	public var x:Number;
	public var y:Number;
	public var width:Number;
	public var height:Number;
	
	/**
	 * <br>
	 * Rectangle(x:Number, y:Number, width:Number, height:Number)<br>
	 * Rectangle(rect:Rectangle)<br>
	 */
	public function Rectangle(x, y:Number, width:Number, height:Number){
		setRect(x, y, width, height);
	}
	
	/**
	 * <br>
	 * setRect(x:Number, y:Number, width:Number, height:Number)<br>
	 * setRect(rect:Rectangle)<br>
	 */
	public function setRect(x, y:Number, width:Number, height:Number):Void{
		if(x instanceof Rectangle){
			this.x = x.x;
			this.y = x.y;
			this.width = x.width;
			this.height = x.height;
		}else{
			if(x == undefined) x = 0;
			if(y == undefined) y = 0;		
			if(width == undefined) width = 0;
			if(height == undefined) height = 0;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}
	
	/**
	 * <br>
	 * setLocation(x:Number, y:Number)<br>
	 * setLocation(p:Point)<br>
	 */
	public function setLocation(x, y:Number):Void{
		if(x instanceof Point){
			this.x = x.x;
			this.y = x.y;
		}else{
			this.x = x;
			this.y = y;
		}
	}
	
	/**
	 * <br>
	 * setSize(width:Number, height:Number)<br>
	 * setSize(dim:Dimension)<br>
	 */
	public function setSize(width, height:Number):Void{
		if(width instanceof Dimension){
			this.width = width.width;
			this.height = width.height;
		}else{
			this.width = width;
			this.height = height;
		}
	}
	
	public function getSize():Dimension{
		return new Dimension(width, height);
	}
	
	public function getLocation():Point{
		return new Point(x, y);
	}
	
    /**
     * Computes the union of this <code>Rectangle</code> with the 
     * specified <code>Rectangle</code>. Returns a new 
     * <code>Rectangle</code> that 
     * represents the union of the two rectangles
     * @param r the specified <code>Rectangle</code>
     * @return    the smallest <code>Rectangle</code> containing both 
     *		  the specified <code>Rectangle</code> and this 
     *		  <code>Rectangle</code>.
     */
    public function union(r:Rectangle):Rectangle{
		var x1:Number = Math.min(x, r.x);
		var x2:Number = Math.max(x + width, r.x + r.width);
		var y1:Number = Math.min(y, r.y);
		var y2:Number = Math.max(y + height, r.y + r.height);
		return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }
    
    /**
     * Resizes the <code>Rectangle</code> both horizontally and vertically.
     * <p>
     * This method modifies the <code>Rectangle</code> so that it is 
     * <code>h</code> units larger on both the left and right side, 
     * and <code>v</code> units larger at both the top and bottom. 
     * <p>
     * The new <code>Rectangle</code> has (<code>x&nbsp;-&nbsp;h</code>, 
     * <code>y&nbsp;-&nbsp;v</code>) as its top-left corner, a 
     * width of 
     * <code>width</code>&nbsp;<code>+</code>&nbsp;<code>2h</code>, 
     * and a height of 
     * <code>height</code>&nbsp;<code>+</code>&nbsp;<code>2v</code>. 
     * <p>
     * If negative values are supplied for <code>h</code> and 
     * <code>v</code>, the size of the <code>Rectangle</code> 
     * decreases accordingly. 
     * The <code>grow</code> method does not check whether the resulting 
     * values of <code>width</code> and <code>height</code> are 
     * non-negative. 
     * @param h the horizontal expansion
     * @param v the vertical expansion
     */
    public function grow(h:Number, v:Number):Void {
		x -= h;
		y -= v;
		width += h * 2;
		height += v * 2;
    }
    
    public function move(dx:Number, dy:Number):Void{
    	if(dx == undefined) dx = 0;
    	if(dy == undefined) dy = 0;
    	x += dx;
    	y += dy;
    }

    public function resize(dwidth:Number, dheight:Number):Void{
    	if(dwidth == undefined) dwidth = 0;
    	if(dheight == undefined) dheight = 0;
    	width += dwidth;
    	height += dheight;
    }
	
	public function leftTop():Point{
		return new Point(x, y);
	}
	
	public function rightTop():Point{
		return new Point(x + width, y);
	}
	
	public function leftBottom():Point{
		return new Point(x, y + height);
	}
	
	public function rightBottom():Point{
		return new Point(x + width, y + height);
	}
	
	public function containsPoint(p:Point):Boolean{
		if(p.x < x || p.y < y || p.x > x+width || p.y > y+height){
			return false;
		}else{
			return true;
		}
	}
	
	public function equals(o:Object):Boolean{
		var r:Rectangle = Rectangle(o);
		return x===r.x && y===r.y && width===r.width && height===r.height;
	}
		
	/**
	 * Duplicates current instance.
	 * @return copy of the current instance.
	 */
	public function clone():Rectangle {
		return new Rectangle(x,y,width,height);
	}
		
	public function toString():String{
		return "Rectangle(x:"+x+",y:"+y+", width:"+width+",height:"+height+")";
	}
}
