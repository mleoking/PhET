package org.papervision3d.core.utils
{
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.geom.TriangleMesh3D;
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.Plane3D;
	import org.papervision3d.core.math.util.ClassificationUtil;
	import org.papervision3d.core.math.util.TriangleUtil;
	import org.papervision3d.core.proto.GeometryObject3D;
	
	public class MeshUtil
	{
		public function MeshUtil()
		{
			
		}
		
		public static function cutTriangleMesh(mesh:TriangleMesh3D, cuttingPlane:Plane3D):Array
		{
			var geom:GeometryObject3D = mesh.geometry;
			var array:Array = new Array();
			if(geom.faces.length){
				var triangleBucketA:Array = new Array();
				var triangleBucketB:Array = new Array();
				var vertBucketA:Array= new Array();
				var vertBucketB:Array = new Array();
				var vertCacheA:Dictionary = new Dictionary(true);
				var vertCacheB:Dictionary = new Dictionary(true);
				var triangle:Triangle3D;
				var nTriangle:Triangle3D;
				var oTris:Array = geom.faces;
				var vert:Vertex3D;
				var nVert:Vertex3D;
				var triClass:int;
				var i:int;
				var nTris:Array = new Array();
				for each(triangle in oTris){
					var tris:Array = TriangleUtil.splitTriangleWithPlane(triangle, cuttingPlane);
					if(tris){
						for each(nTriangle in tris){
							nTris.push(nTriangle);
						}
					}else{
						nTris.push(triangle);
					}
				}
				
				for each(triangle in nTris){
					triClass = ClassificationUtil.classifyTriangle(triangle, cuttingPlane);
					if(triClass == ClassificationUtil.FRONT){
						triangleBucketA.push(triangle);
					}else if(triClass == ClassificationUtil.BACK){
						triangleBucketB.push(triangle);
					}
				}
				
				if(triangleBucketA.length > 0){
					for each(triangle in triangleBucketA){
						for(i=0; i<triangle.vertices.length; i++){
							vert = triangle.vertices[i];
							if(!(nVert = vertCacheA[vert])){
								nVert = vert.clone();
								vertCacheA[vert] = nVert;
							}
							
							vertBucketA.push(nVert);
							triangle.vertices[i] = nVert;
						}
						triangle.updateVertices();
					}
					var meshA:TriangleMesh3D = new TriangleMesh3D(mesh.material, vertBucketA, triangleBucketA);
					meshA.material = mesh.material;
					meshA.geometry.ready = true;
					array.push(meshA);
				}
				 
				if(triangleBucketB.length > 0){
					for each(triangle in triangleBucketB){
						for(i=0; i<triangle.vertices.length; i++){
							vert = triangle.vertices[i];
							if(!(nVert = vertCacheB[vert])){
								nVert = vert.clone();
								vertCacheB[vert] = nVert;
							}
							nVert = vert.clone();
							vertBucketB.push(nVert);
							triangle.vertices[i] = nVert;
						}
						triangle.updateVertices();
					}
					var meshB:TriangleMesh3D = new TriangleMesh3D(mesh.material, vertBucketB, triangleBucketB);
					meshB.material = mesh.material;
					meshB.geometry.ready = true;
					array.push(meshB);
				}
				
				return array;
			}else{
				throw new Error("source geometry empty");
			}
			return array;
		}

	}
}