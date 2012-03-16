/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.geom
{

/**
 * The Dimension class encapsulates the width and height of a componentin a single object.<br>
 * Note this Class use <b>int</b> as its parameters on purpose to avoid problems that happended in aswing before.
 * @author iiley
 */
public class IntDimension{
	
	public var width:int = 0;
	public var height:int = 0;
	
	/**
	 * Creates a dimension.
	 */
	public function IntDimension(width:int=0, height:int=0){
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Sets the size as same as the dim.
	 */
	public function setSize(dim:IntDimension):void{
		this.width = dim.width;
		this.height = dim.height;
	}
	
	/**
	 * Sets the size with width and height.
	 */
	public function setSizeWH(width:int, height:int):void{
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Increases the size by s and return its self(<code>this</code>).
	 * @return <code>this</code>.
	 */
	public function increaseSize(s:IntDimension):IntDimension{
		width += s.width;
		height += s.height;
		return this;
	}
	
	/**
	 * Decreases the size by s and return its self(<code>this</code>).
	 * @return <code>this</code>.
	 */
	public function decreaseSize(s:IntDimension):IntDimension{
		width -= s.width;
		height -= s.height;
		return this;
	}
	
	/**
	 * modify the size and return itself. 
	 */
	public function change(deltaW:int, deltaH:int):IntDimension{
		width += deltaW;
		height += deltaH;
		return this;
	}
	
	/**
	 * return a new size with this size with a change.
	 */
	public function changedSize(deltaW:int, deltaH:int):IntDimension{
		var s:IntDimension = new IntDimension(deltaW, deltaH);
		return s;
	}
	
	/**
	 * Combines current and specified dimensions by getting max sizes
	 * and puts result into itself.
	 * @return the combined dimension itself.
	 */
	public function combine(d:IntDimension):IntDimension {
		this.width = Math.max(this.width, d.width);	
		this.height = Math.max(this.height, d.height);
		return this;
	}

	/**
	 * Combines current and specified dimensions by getting max sizes
	 * and returns new IntDimension object
	 * @return a new dimension with combined size.
	 */
	public function combineSize(d:IntDimension):IntDimension {
		return clone().combine(d);
	}
	
	/**
	 * return a new bounds with this size with a location.
	 * @param x the location x.
	 * @prame y the location y.
	 * @return the bounds.
	 */
	public function getBounds(x:int=0, y:int=0):IntRectangle{
		var p:IntPoint = new IntPoint(x, y);
		var r:IntRectangle = new IntRectangle();
		r.setLocation(p);
		r.setSize(this);
		return r;
	}
	
	/**
	 * Returns whether or not the passing o is an same value IntDimension.
	 */
	public function equals(o:Object):Boolean{
		var d:IntDimension = o as IntDimension;
		if(d == null) return false;
		return width===d.width && height===d.height;
	}

	/**
	 * Duplicates current instance.
	 * @return copy of the current instance.
	 */
	public function clone():IntDimension {
		return new IntDimension(width,height);
	}
	
	/**
	 * Create a big dimension for component.
	 * @return a IntDimension(100000, 100000)
	 */
	public static function createBigDimension():IntDimension{
		return new IntDimension(100000, 100000);
	}
	
	public function toString():String{
		return "IntDimension["+width+","+height+"]";
	}
}

}