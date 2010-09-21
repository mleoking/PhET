package away3d.core.base
{
	import away3d.core.geom.*;
	
	public class VertexClassification
	{
		public var distance:Number;
		
		public var vertex:Vertex;
		
		public var plane:Plane3D;
		
		public function getDistance(val:Plane3D):Number
		{
			var d:Number = val.distance(vertex.position);
			
			if (d < 0 && !isNaN(d)) {
				plane = val;
				distance = d;
			}
			
			return d;
		}
	}
}