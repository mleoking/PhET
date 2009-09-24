package org.papervision3d.core.dyn
{
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.NumberUV;
	import org.papervision3d.core.proto.MaterialObject3D;
	import org.papervision3d.materials.BitmapMaterial;
	import org.papervision3d.materials.special.CompositeMaterial;
	import org.papervision3d.objects.DisplayObject3D;
	
	public class DynamicTriangles
	{
		private static const GROW_SIZE: int = 300;
		private static const INIT_SIZE: int = 100;

		private static var triangleCounter: int;
		private static var trianglePool: Array;
		
		public function DynamicTriangles()
		{
			init();
		}
		
		private static function init(): void
		{
			trianglePool = new Array( INIT_SIZE );
			var i: int = INIT_SIZE;
			while( --i > -1 ){
				trianglePool[ i ] = new Triangle3D(null, null, null, null);
			}
			triangleCounter = INIT_SIZE;
		}
		
		public function getTriangle(object:DisplayObject3D=null, m:MaterialObject3D=null,v0:Vertex3D = null,v1:Vertex3D = null,v2:Vertex3D = null,uv0:NumberUV = null,uv1:NumberUV = null, uv2:NumberUV = null) : Triangle3D
		{
			if( triangleCounter == 0 ){
				var i: int = GROW_SIZE;
				while( --i > -1 ) {
					trianglePool.unshift(new Triangle3D(null,null,null,null));
				}
				triangleCounter = GROW_SIZE;
				return getTriangle(object, m,v0,v1,v2,uv0,uv1,uv2);
			}else{
				var triangle:Triangle3D = Triangle3D(trianglePool[--triangleCounter]);
				if(triangle.material) {
					
					if(triangle.material is BitmapMaterial && BitmapMaterial(triangle.material).uvMatrices)
					{
						BitmapMaterial(triangle.material).uvMatrices[triangle.renderCommand] = null;
					}
					
					if(triangle.material is CompositeMaterial)
					{
						for each(var mat:MaterialObject3D in CompositeMaterial(triangle.material).materials)
						{
							if(mat is BitmapMaterial && BitmapMaterial(mat).uvMatrices)
							{
								BitmapMaterial(mat).uvMatrices[triangle.renderCommand] = null;
							}
						}
					}
				}

				triangle.instance = object;
				triangle.vertices = [v0, v1, v2];
				triangle.uv = [uv0, uv1, uv2];
				triangle.updateVertices();
				triangle.createNormal();
				triangle.material = m;

				return triangle;
			}
		}
		
		public function releaseAll():void
		{
			returnAllTriangles();
		}
		
		public function returnTriangle(triangle : Triangle3D):void
		{
			trianglePool[triangleCounter++] = triangle;
		}
		
		public function returnAllTriangles():void
		{
			triangleCounter = trianglePool.length;
		}
	}
}