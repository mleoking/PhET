package org.papervision3d.core.controller
{
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.core.math.Number3D;
	import org.papervision3d.objects.DisplayObject3D;
	import org.papervision3d.objects.special.Skin3D;
	
	public class SkinController implements IObjectController
	{
		/** */
		public var poseMatrix:Matrix3D;
		
		/** */
		public var bindShapeMatrix:Matrix3D;
		
		/** */
		public var target:Skin3D;
		
		/** */
		public var joints:Array;
	
		/** */
		public var invBindMatrices:Array;
		
		/** */
		public var vertexWeights:Array;
		
		/** */
		public var input : MorphController;

		/**
		 * Constructor.
		 * 
		 * @param	target
		 */ 
		public function SkinController(target:Skin3D)
		{
			this.target = target;
			this.joints = new Array();
			this.invBindMatrices = new Array();
			this.vertexWeights = new Array();
		}

		/**
		 * Update.
		 */ 
		public function update():void
		{
			if(!joints.length || !bindShapeMatrix)
				return;
			
			if(!_cached)
				cacheVertices();
				
			if(invBindMatrices.length != this.joints.length)
				return;
				
			var vertices:Array = target.geometry.vertices;
			var i:int;
			
			// reset mesh's vertices to 0
			for(i = 0; i < vertices.length; i++)
				vertices[i].x = vertices[i].y = vertices[i].z = 0;
								
			// skin the mesh!
			for(i = 0; i < joints.length; i++)
				skinMesh(joints[i], this.vertexWeights[i], invBindMatrices[i], _cached, vertices);
		}
		
		/**
		 * Cache original vertices.
		 */
		private function cacheVertices():void
		{
			this.target.transformVertices(this.bindShapeMatrix);
			this.target.geometry.ready = true;
			
			var vertices:Array = this.target.geometry.vertices;

			_cached = new Array(vertices.length);
			
			for(var i:int = 0; i < vertices.length; i++)
				_cached[i] = new Number3D(vertices[i].x, vertices[i].y, vertices[i].z);
		}
		
		/**
		 * Skins a mesh.
		 * 
		 * @param	joint
		 * @param	meshVerts
		 * @param	skinnedVerts
		 */
		private function skinMesh(joint:DisplayObject3D, weights:Array, inverseBindMatrix:Matrix3D, meshVerts:Array, skinnedVerts:Array):void
		{
			var i:int;
			var pos:Number3D = new Number3D();
			var original:Number3D;
			var skinned:Vertex3D;

			var matrix:Matrix3D = Matrix3D.multiply(joint.world, inverseBindMatrix);
			
			for( i = 0; i < weights.length; i++ )
			{
				var weight:Number = weights[i].weight;
				var vertexIndex:int = weights[i].vertexIndex;

				if( weight <= 0.0001 || weight >= 1.0001) continue;
								
				original = meshVerts[ vertexIndex ];	
				skinned = skinnedVerts[ vertexIndex ];
				
				pos.x = original.x;
				pos.y = original.y;
				pos.z = original.z;
							
				// joint transform
				Matrix3D.multiplyVector(matrix, pos);	

				//update the vertex
				skinned.x += (pos.x * weight);
				skinned.y += (pos.y * weight);
				skinned.z += (pos.z * weight);
			}
		}
		
		private var _cached:Array;
	}
}