package org.papervision3d.core.math.util {
	import flash.geom.Rectangle;
	
	import org.papervision3d.core.math.Number2D;
	import org.papervision3d.core.math.Number3D;			/**	 * @author Seb Lee-Delisle
	 * 
	 * Some handy rectangle tools that are faster than the built in Rectangle methods. 
	 * 	 */	public class FastRectangleTools 
	{
		
		/* 
		 * 
		 * in my tests runs 1/3 faster the Rectangle.intersects
		 * Rectangle.intersects() Test: 71.4499
		 * Fast intersect Test: 54.199
		 * 
		 * @Author Seb Lee-Delisle
		 * 
		 * @param	rect1 
		 * @param	rect2  
		 * @return  true if the rectangles intersect. 
		 */ 
		 
		public static function intersects(rect1 : Rectangle, rect2 :Rectangle) : Boolean
		{
			
			if( (!((rect1.right<rect2.left)||(rect1.left>rect2.right))) && (!((rect1.bottom<rect2.top)||(rect1.top>rect2.bottom))) )
				return true; 
					
			return false; 
			
		}
			
		
		/* 
		 * 
		 * benchmarks compared to Rectangle.intersection : 
		 * Rectangle.intersects() Test			: 146.89999999999998
		 * this function (without targetrect)  	: 133.45
		 * this function (with targetrect)  	    : 72.55 
		 * @Author Seb Lee-Delisle
		 * 
		 * @param	rect1 
		 * @param	rect2  
		 * @param	targetrect  
		 * @return  a rectangle representing the intersection of the two source rectangles.  
		 */ 
		public static function intersection(rect1:Rectangle, rect2: Rectangle, targetrect:Rectangle = null) : Rectangle
		{
		
			if(!targetrect) targetrect = new Rectangle(); 
			if(!intersects(rect1, rect2)) 
			{
				targetrect.x = targetrect.y = targetrect.width = targetrect.height = 0;
				return targetrect; 
			}
			
			targetrect.left = (rect1.left>rect2.left) ? rect1.left : rect2.left; 
			targetrect.right = (rect1.right<rect2.right) ? rect1.right : rect2.right; 
			targetrect.top = (rect1.top>rect2.top) ? rect1.top : rect2.top; 
			targetrect.bottom = (rect1.bottom<rect2.bottom) ? rect1.bottom : rect2.bottom; 
			
			return targetrect; 
			
		}
		
		/* 
		 * 
		 * Returns a rectangle defining the bounds of a rotated rectangle. 
		 * @Author Seb Lee-Delisle
		 * 
		 * @param	rect 
		 * @param	angle
		 * @param	targetrect  
		 * @return  a rectangle representing the bounds of the source rectangle, rotated at the angle given. 
		 */ 
		public static function getRotatedBounds(rect : Rectangle, angle : Number, targetrect : Rectangle = null) : Rectangle
		{
			if(!targetrect) targetrect = new Rectangle(); 
			
			angle *=Number3D.toRADIANS; 
			
			var width : Number = rect.width; 
			var height : Number = rect.height; 
			
			var absSinA : Number = Math.abs(Math.sin(angle)); 
			var absCosA : Number = Math.abs(Math.cos(angle));
			
			targetrect.left = 	rect.x - 0.5 * ((width * absCosA) + (height * absSinA));
			targetrect.right =  rect.x + 0.5 * ((width * absCosA) + (height * absSinA));
			
			targetrect.top = 	rect.y - 0.5 * ((width * absSinA) + (height * absCosA));
			targetrect.bottom = rect.y + 0.5 * ((width * absSinA) + (height * absCosA));
			
			return targetrect;
			
		}
			}}