/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;

/**
 * An Insets object is a representation of the borders of a container. 
 * It specifies the space that a container must leave at each of its edges. 
 * The space can be a border, a blank space, or a title. 
 * 
 * @author iiley
 */
class org.aswing.Insets{
	
	/**
	 * Creates new <code>Insets</code> instance with identic edges.
	 * 
	 * @param edge the edge value for insets.
	 * @return new insets instance.
	 */
	public static function createIdentic(edge:Number):Insets {
		return new Insets(edge,edge,edge,edge);	
	}
	
	public var bottom:Number;
	public var top:Number;
	public var left:Number;
	public var right:Number;
	
	/**
	 * Insets(top:Number, left:Number, bottom:Number, right:Number)<br>
	 * Insets()<br>
	 */
	public function Insets(top:Number, left:Number, bottom:Number, right:Number){
		if(top == undefined) top = 0;
		if(left == undefined) left = 0;
		if(bottom == undefined) bottom = 0;
		if(right == undefined) right = 0;
		
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}
	
	/**
	 * This insets add specified insets and return itself.
	 */
	public function addInsets(insets:Insets):Insets{
		this.top += insets.top;
		this.left += insets.left;
		this.bottom += insets.bottom;
		this.right += insets.right;
		return this;
	}
	
	public function getMarginWidth():Number{
		return left + right;
	}
	
	public function getMarginHeight():Number{
		return top + bottom;
	}
	
	public function getInsideBounds(bounds:Rectangle):Rectangle{
		var r:Rectangle = new Rectangle(bounds);
		r.x += left;
		r.y += top;
		r.width -= (left + right);
		r.height -= (top + bottom);
		return r;
	}
	
	public function getOutsideBounds(bounds:Rectangle):Rectangle{
		var r:Rectangle = new Rectangle(bounds);
		r.x -= left;
		r.y -= top;
		r.width += (left + right);
		r.height += (top + bottom);
		return r;
	}
	
	public function getOutsideSize(size:Dimension):Dimension{
		var s:Dimension = new Dimension(size);
		s.width += (left + right);
		s.height += (top + bottom);
		return s;
	}
	
	public function getInsideSize(size:Dimension):Dimension{
		var s:Dimension = new Dimension(size);
		s.width -= (left + right);
		s.height -= (top + bottom);
		return s;
	}
	
	public function equals(o:Object):Boolean{
		var i:Insets = Insets(o);
		if(i == null){
			return false;
		}else{
			return i.bottom == bottom && i.left == left && i.right == right && i.top == top;
		}
	}
	
	public function toString():String{
		return "Insets(top:"+top+", left:"+left+", bottom:"+bottom+", right:"+right+")";
	}
}
