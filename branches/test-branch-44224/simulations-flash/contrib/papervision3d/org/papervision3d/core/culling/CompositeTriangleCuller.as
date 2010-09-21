package org.papervision3d.core.culling {
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.geom.renderables.Vertex3DInstance;	

	public class CompositeTriangleCuller implements ITriangleCuller
	{
		
		private var cullers:Array;
		
		public function CompositeTriangleCuller()
		{
			init();
		}
		
		private function init():void
		{
			cullers = new Array();
		}
		
		public function addCuller(culler:ITriangleCuller):void
		{
			cullers.push(culler);
		}
		
		public function removeCuller(culler:ITriangleCuller):void
		{
			cullers.splice(cullers.indexOf(culler),1);
		}
		
		public function clearCullers():void
		{
			cullers = new Array();
		}
		
		public function testFace(face3D:Triangle3D, vertex0:Vertex3DInstance, vertex1:Vertex3DInstance, vertex2:Vertex3DInstance):Boolean
		{
			for each(var culler:ITriangleCuller in cullers)
			{
				//Add "modes here". Like inclusive or exclusive	
			}
			return true;
		}
		
	}
}