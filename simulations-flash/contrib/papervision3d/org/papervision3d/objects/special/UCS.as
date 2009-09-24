package org.papervision3d.objects.special
{
	import org.papervision3d.core.geom.Lines3D;
	import org.papervision3d.core.geom.renderables.Line3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.materials.special.LineMaterial;

	
	/**
	 * @author Tim Knip 
	 */
	public class UCS extends Lines3D
	{
		/**
		 * 
		 * @param	scale
		 * @param	name
		 * @return
		 */
		public function UCS( scale:Number = 500, name:String = null ):void
		{
			super(new LineMaterial(), name);
			
			var v0:Vertex3D = new Vertex3D();
			var v1:Vertex3D = new Vertex3D(scale, 0, 0);
			var v2:Vertex3D = new Vertex3D(0, scale, 0);
			var v3:Vertex3D = new Vertex3D(0, 0, scale);
			
			addLine( new Line3D(this, new LineMaterial(0xff0000), 0, v0, v1) );
			addLine( new Line3D(this, new LineMaterial(0x00ff00), 0, v0, v2) );
			addLine( new Line3D(this, new LineMaterial(0x0000ff), 0, v0, v3) );
		}
	}
}
