package org.papervision3d.core.math
{
	import org.papervision3d.core.math.Number3D;
	
	public class Ray3D
	{
		public var x:Number;
		public var y:Number;
		public var z:Number;
		public var dx:Number;
		public var dy:Number;
		public var dz:Number;
		
		public function Ray3D(x:Number = 0, y:Number = 0, z:Number = 0, dx:Number=0, dy:Number=0, dz:Number=0)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;	
		}
		
		public function get o():Number3D{
			return new Number3D(x, y, z);
		}
		
		public function get d():Number3D{
			return new Number3D(dx, dy, dz);
		}
		
		

	}
}