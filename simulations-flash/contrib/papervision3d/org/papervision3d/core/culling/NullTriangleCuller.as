package org.papervision3d.core.culling
{
	import org.papervision3d.core.geom.renderables.Vertex3DInstance;
	import org.papervision3d.core.geom.renderables.Triangle3D;

	public class NullTriangleCuller implements ITriangleCuller
	{
		public function NullTriangleCuller()
		{
		}

		public function testFace(face3D:Triangle3D, vertex0:Vertex3DInstance, vertex1:Vertex3DInstance, vertex2:Vertex3DInstance):Boolean
		{
			return true;
		}
		
	}
}