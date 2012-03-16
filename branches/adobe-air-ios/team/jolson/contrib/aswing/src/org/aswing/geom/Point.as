/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * A point with x and y coordinates.
 * @author iiley
 */
class org.aswing.geom.Point{
	
	public var x:Number = 0;
	public var y:Number = 0;
	
	/**
	 * Constructor
	 * <br>
	 * Point(x:Number, y:Number)<br>
	 * Point(p:Point)<br>
	 * Point()<br>
	 */
	public function Point(x, y:Number){
		setLocation(x, y);
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
			if(x == undefined) x = 0;
			if(y == undefined) y = 0;
			this.x = x;
			this.y = y;		
		}
	}
	
	public function move(dx:Number, dy:Number):Point{
		x += dx;
		y += dy;
		return this;
	}
	
	public function moveRadians(angle:Number, distance:Number):Point{
		x += Math.cos(angle)*distance;
		y += Math.sin(angle)*distance;
		return this;
	}
	
	public function nextPoint(angle:Number, distance:Number):Point{
		return new Point(x+Math.cos(angle)*distance, y+Math.sin(angle)*distance);
	}
	
	/**
	 * <br>
	 * distanceSq(x:Number, y:Number)<br>
	 * distanceSq(p:Point)<br>
	 *
	 * @return the distance square from this to p
	 */
	public function distanceSq(tx, ty:Number):Number{
		var xx:Number;
		var yy:Number;
		if(tx instanceof Point){
			xx = tx.x;
			yy = tx.y;
		}else{
			xx = tx;
			yy = ty;
		}
		return ((x-xx)*(x-xx)+(y-yy)*(y-yy));	
	}

	/**
	 * <br>
	 * distance(x:Number, y:Number)<br>
	 * distance(p:Point)<br>
	 *
	 * @return the distance from this to p
	 */
	public function distance(tx, ty:Number):Number{
		return Math.sqrt(distanceSq(tx, ty));
	}
    
	public function equals(o:Object):Boolean{
		var p:Point = Point(o);
		return x===p.x && y===p.y;
	}    

	/**
	 * Duplicates current instance.
	 * @return copy of the current instance.
	 */
	public function clone():Point {
		return new Point(x,y);
	}
    
	public function toString():String{
		return "Point("+x+","+y+")";
	}	
}
