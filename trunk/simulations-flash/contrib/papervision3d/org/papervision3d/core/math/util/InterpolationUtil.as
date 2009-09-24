package org.papervision3d.core.math.util
{
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.NumberUV;
	
	public class InterpolationUtil
	{
		public static function interpolatePoint( a:Vertex3D, b:Vertex3D, alpha:Number ):Vertex3D
		{
			var dst:Vertex3D = new Vertex3D();
			dst.x = a.x + alpha * (b.x - a.x);
			dst.y = a.y + alpha * (b.y - a.y);
			dst.z = a.z + alpha * (b.z - a.z);
			return dst;
		}
		
		public static function interpolatePointTo(a:Vertex3D, b:Vertex3D, alpha:Number, dst:Vertex3D):void
		{
			dst.x = a.x + alpha * (b.x - a.x);
			dst.y = a.y + alpha * (b.y - a.y);
			dst.z = a.z + alpha * (b.z - a.z);
		}
		
		public static function interpolateUV( a:NumberUV, b:NumberUV, alpha:Number ):NumberUV
		{
			var dst:NumberUV = new NumberUV();
			dst.u = a.u + alpha * (b.u - a.u);
			dst.v = a.v + alpha * (b.v - a.v);
			return dst;
		}
		
		public static function interpolateUVTo( a:NumberUV, b:NumberUV, alpha:Number, dst:NumberUV):NumberUV
		{
			dst.u = a.u + alpha * (b.u - a.u);
			dst.v = a.v + alpha * (b.v - a.v);
			return dst;
		}
		
	}
}