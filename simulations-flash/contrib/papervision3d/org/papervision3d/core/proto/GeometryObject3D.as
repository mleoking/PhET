package org.papervision3d.core.proto
{
	import flash.events.EventDispatcher;
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.AxisAlignedBoundingBox;
	import org.papervision3d.core.math.BoundingSphere;
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.objects.DisplayObject3D;

	/**
	* The GeometryObject3D class contains the mesh definition of an object.
	*/
	public class GeometryObject3D extends EventDispatcher
	{
		
		protected var _boundingSphere:BoundingSphere;
		protected var _boundingSphereDirty :Boolean = true;
		protected var _aabb:AxisAlignedBoundingBox;
		protected var _aabbDirty:Boolean = true;
		private var _numInstances:uint = 0; // TODO - pretty sure this is never used... obsolete? 
		
		/**
		 * 
		 */
		public var dirty:Boolean;
		
		/**
		* An array of Face3D objects for the faces of the mesh.
		*/
		public var faces:Array;
	
		/**
		* An array of vertices.
		*/
		public var vertices :Array;
		public var _ready:Boolean = false;
		
		public function GeometryObject3D( ):void
		{
			dirty = true;
		}
		
		public function transformVertices(transformation:Matrix3D ):void
		{
			var m11 :Number = transformation.n11,
			m12 :Number = transformation.n12,
			m13 :Number = transformation.n13,
			m21 :Number = transformation.n21,
			m22 :Number = transformation.n22,
			m23 :Number = transformation.n23,
			m31 :Number = transformation.n31,
			m32 :Number = transformation.n32,
			m33 :Number = transformation.n33,

			m14 :Number = transformation.n14,
			m24 :Number = transformation.n24,
			m34 :Number = transformation.n34,

			i        :int    = vertices.length,

			vertex   :org.papervision3d.core.geom.renderables.Vertex3D;


			while( vertex = vertices[--i] )
			{
				// Center position
				var vx :Number = vertex.x;
				var vy :Number = vertex.y;
				var vz :Number = vertex.z;

				var tx :Number = vx * m11 + vy * m12 + vz * m13 + m14;
				var ty :Number = vx * m21 + vy * m22 + vz * m23 + m24;
				var tz :Number = vx * m31 + vy * m32 + vz * m33 + m34;

				vertex.x = tx;
				vertex.y = ty;
				vertex.z = tz;
			}
		}
		
		
		
		private function createVertexNormals():void
		{
			var tempVertices:Dictionary = new Dictionary(true);
			var face:Triangle3D;
			var vertex3D:Vertex3D;
			for each(face in faces){
				face.v0.connectedFaces[face] = face;
				face.v1.connectedFaces[face] = face;
				face.v2.connectedFaces[face] = face;
				tempVertices[face.v0] = face.v0;
				tempVertices[face.v1] = face.v1;
				tempVertices[face.v2] = face.v2;
			}
			
			for each (vertex3D in tempVertices){
				vertex3D.calculateNormal();
			}
		}
		
		public function set ready(b:Boolean):void
		{
			if(b){
				createVertexNormals();
				this.dirty = false;
			}
			_ready = b;
		}
	
		public function get ready():Boolean
		{
			return _ready;
		}
		
		/**
		* Radius square of the mesh bounding sphere
		*/
		public function get boundingSphere():BoundingSphere
		{
			if( _boundingSphereDirty ){
				_boundingSphere = BoundingSphere.getFromVertices(vertices);
				_boundingSphereDirty = false;
			}
			return _boundingSphere;
		}
		
		/**
		 * Returns an axis aligned bounding box, not world oriented.
		 * 
		 * @Author Ralph Hauwert - Added as an initial test.
		 */
		public function get aabb():AxisAlignedBoundingBox
		{
			if(_aabbDirty){
				_aabb = AxisAlignedBoundingBox.createFromVertices(vertices);
				_aabbDirty = false;
			}
			return _aabb;
		}

		/**
		 * Clones this object.
		 * 
		 * @param	parent
		 * 
		 * @return	The cloned GeometryObject3D.
		 */ 
		public function clone(parent:DisplayObject3D = null):GeometryObject3D
		{
			var materials:Dictionary = new Dictionary(true);
			var verts:Dictionary = new Dictionary(true);
			var geom:GeometryObject3D = new GeometryObject3D();
			var i:int;
			
			geom.vertices = new Array();			
			geom.faces = new Array();

			// clone vertices
			for(i = 0; i < this.vertices.length; i++)
			{
				var v:Vertex3D = this.vertices[i];
				verts[ v ] = v.clone();
				geom.vertices.push(verts[v]);
			}
			
			// clone triangles
			for(i = 0; i < this.faces.length; i++)
			{
				var f:Triangle3D = this.faces[i];
			
				var v0:Vertex3D = verts[ f.v0 ];
				var v1:Vertex3D = verts[ f.v1 ];	
				var v2:Vertex3D = verts[ f.v2 ];
				
				geom.faces.push(new Triangle3D(parent, [v0, v1, v2], f.material, f.uv));
				
				materials[ f.material ] = f.material;
			}
			
			for each(var material:MaterialObject3D in materials)
			{
				if( material )
					material.registerObject(parent);
			}
				
			return geom;
		}
		
		/**
		 * Flips the winding of faces.
		 */ 
		public function flipFaces():void
		{
			for each(var f:Triangle3D in this.faces)
			{
				var tmp:Vertex3D = f.v0;
				f.v0 = f.v2;
				f.v2 = tmp;
				//f.uv = [f.uv2, f.uv1, f.uv0];
				f.createNormal();
			}
				
			this.ready = true;
		}
	}
}