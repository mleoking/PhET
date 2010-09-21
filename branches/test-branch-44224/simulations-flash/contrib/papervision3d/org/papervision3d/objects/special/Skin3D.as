package org.papervision3d.objects.special
{
	import org.papervision3d.core.geom.TriangleMesh3D;
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.core.proto.MaterialObject3D;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.objects.DisplayObject3D;

	/**
	 * @author Tim Knip
	 */ 
	public class Skin3D extends TriangleMesh3D
	{
		public function Skin3D(material:MaterialObject3D, vertices:Array, faces:Array, name:String=null)
		{
			super(material, vertices, faces, name);
		}
		
		public override function project(parent:DisplayObject3D, renderSessionData:RenderSessionData):Number
		{
			// skins are already transformed into world-space by the skinning algorithm!
			// so we need to set its #transform to the parent#transform and invert...
			this.transform.copy(parent.world);
			this.transform.invert();
			
			return super.project(parent, renderSessionData);
		}

		public override function clone():DisplayObject3D
		{
			var object:DisplayObject3D = super.clone();
			var mesh:Skin3D = new Skin3D(this.material, [], [], object.name);
			
			if(this.materials)
			{
				mesh.materials = this.materials.clone();
			}
				
			if(object.geometry)
			{
				mesh.geometry = object.geometry.clone(mesh);
			}
				
			mesh.copyTransform(this);
			
			return mesh;
		}
	}
}