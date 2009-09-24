package org.papervision3d.core.math.util
{
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.Number3D;
	import org.papervision3d.core.math.Plane3D;
	
	public class Intersection
	{
		public static const NONE:int = 0;
		public static const INTERSECTION:int=1;
		public static const PARALLEL:int = 2;
		
		public var point:Number3D;
		public var vert:Vertex3D;
		public var alpha:Number = 0;
		public var status:int;
		
		public function Intersection(point:Number3D = null, vert:Vertex3D = null)
		{
			if(point != null){
				this.point = point;
			}else{
				this.point = new Number3D();
			}
			if(vert != null){
				this.vert = vert;
			}else{
				this.vert = new Vertex3D();
			}
		}

		public static function linePlane(pA:Vertex3D, pB:Vertex3D, plane:Plane3D, e:Number=0.01, dst:Intersection = null):Intersection
		{
			if(dst == null){
				dst = new Intersection();
			}
			var a:Number = plane.normal.x;
			var b:Number = plane.normal.y;
			var c:Number = plane.normal.z;
			var d:Number = plane.d;
			var x1:Number = pA.x;
			var y1:Number = pA.y;
			var z1:Number = pA.z;
			var x2:Number = pB.x;
			var y2:Number = pB.y;
			var z2:Number = pB.z;
			
			var r0:Number = (a * x1) + (b * y1) + (c * z1) + d;
			var r1:Number = a*(x1-x2) + b*(y1-y2) + c*(z1-z2);
			var u:Number = r0 / r1;
			
			if( Math.abs(u) < e ) {
				dst.status = Intersection.PARALLEL;
			} else if( (u > 0 && u < 1 ) ) {
				dst.status = Intersection.INTERSECTION;
				var pt:Number3D = dst.point;
				pt.x = x2 - x1;
				pt.y = y2 - y1;
				pt.z = z2 - z1;
				pt.x *= u;
				pt.y *= u;
				pt.z *= u;
				pt.x += x1;
				pt.y += y1;
				pt.z += z1;
				
				dst.alpha = u;
				
				dst.vert.x = pt.x;
				dst.vert.y = pt.y;
				dst.vert.z = pt.z;
			}else{
				dst.status = Intersection.NONE;
			}
			
			return dst;
		}

	}
}