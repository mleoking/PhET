package org.papervision3d.core.render.filter
{
	import org.papervision3d.cameras.Camera3D;
	import org.papervision3d.core.clipping.draw.Clipping;
	import org.papervision3d.core.render.data.QuadTree;
	import org.papervision3d.scenes.Scene3D;
	
	public class AbstractQuadrantFilter
	{
		public function AbstractQuadrantFilter()
		{
		}
		/**
		 * Runs a quadrant filter
		 */
		 public function filterTree(tree:QuadTree, scene:Scene3D, camera:Camera3D, clip:Clipping):void{
		 	
		 }

	}
}