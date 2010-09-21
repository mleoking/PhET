package org.papervision3d.materials
{
	import org.papervision3d.core.render.draw.ITriangleDrawer;
	import org.papervision3d.view.BitmapViewport3D;

	public class BitmapViewportMaterial extends BitmapMaterial implements ITriangleDrawer
	{
		public function BitmapViewportMaterial(bitmapViewport:BitmapViewport3D, precise:Boolean=false)
		{
			super(bitmapViewport.bitmapData, precise);
		}
		
	}
}