package org.papervision3d.core.math
{
	import org.papervision3d.core.geom.renderables.Vertex3D;
	
	public class BoundingSphere
	{
		//The non squared maximum vertex distance.
		public var maxDistance:Number;
		
		//The squared maximum vertex distance.
		public var radius:Number;
		
		/**
		 * @Author Ralph Hauwert
		 */
		public function BoundingSphere(maxDistance:Number)
		{
			this.maxDistance = maxDistance;
			this.radius = Math.sqrt(maxDistance);
		}
		
		public static function getFromVertices(vertices:Array):BoundingSphere
		{
			var max :Number = 0;
			var d   :Number;
			var v:Vertex3D;
			for each(v in vertices )
			{
				d = v.x*v.x + v.y*v.y + v.z*v.z;
				max = (d > max)? d : max;
			}
			return new BoundingSphere(max);
		}

	}
}