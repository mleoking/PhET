package org.papervision3d.core.math
{
	import org.papervision3d.core.math.Number3D;
	
	public class Sphere3D
	{
		public var x:Number;
		public var y:Number;
		public var z:Number;
		public var radius:Number;
		
		public function Sphere3D(r:Number = 100, x:Number=0, y:Number=0, z:Number=0)
		{
			this.radius = r;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public function get o():Number3D{
			return new Number3D(x, y, z);
		}
		
		public function get r2():Number{
			return radius*radius;
		}
		

		public function intersectRay(ray:Ray3D):Number{
			var dst:Number3D = Number3D.sub(ray.o, o);
			var b:Number = Number3D.dot(dst, ray.d);
			var c:Number = Number3D.dot(dst, dst)-r2;
			var d:Number = b*b-c;
			if(d >0)
				return -b-Math.sqrt(d);
			else 
				return -999999;
		}
	}
}