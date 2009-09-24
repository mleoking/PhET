package org.papervision3d.core.math
{
	import org.papervision3d.core.geom.renderables.Vertex3D;
	
	public class AxisAlignedBoundingBox
	{
		public var minX:Number;
		public var minY:Number;
		public var minZ:Number;
		public var maxX:Number;
		public var maxY:Number;
		public var maxZ:Number;
		
		protected var _vertices:Array;
		
		/**
		 * @author Ralph Hauwert/Alex Clarke
		 */
		public function AxisAlignedBoundingBox(minX:Number, minY:Number, minZ:Number, maxX:Number, maxY:Number, maxZ:Number)
		{
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
			createBoxVertices();
		}
		
		protected function createBoxVertices():void
		{
			_vertices = new Array();
			_vertices.push(new Vertex3D(minX, minY, minZ));
			_vertices.push(new Vertex3D(minX, minY, maxZ));
			_vertices.push(new Vertex3D(minX, maxY, minZ));
			_vertices.push(new Vertex3D(minX, maxY, maxZ));
			_vertices.push(new Vertex3D(maxX, minY, minZ));
			_vertices.push(new Vertex3D(maxX, minY, maxZ));
			_vertices.push(new Vertex3D(maxX, maxY, minZ));
			_vertices.push(new Vertex3D(maxX, maxY, maxZ));
		}
		
		public function getBoxVertices():Array
		{
			return _vertices;
		}
		
		public function merge(bbox:AxisAlignedBoundingBox):void
		{
			this.minX = Math.min(this.minX, bbox.minX);
			this.minY = Math.min(this.minY, bbox.minY);
			this.minZ = Math.min(this.minZ, bbox.minZ);
			this.maxX = Math.max(this.maxX, bbox.maxX);
			this.maxY = Math.max(this.maxY, bbox.maxY);
			this.maxZ = Math.max(this.maxZ, bbox.maxZ);	
			createBoxVertices();
		}
		
		public static function createFromVertices(vertices:Array):AxisAlignedBoundingBox
		{
			var minX :Number = Number.MAX_VALUE;
			var minY :Number = Number.MAX_VALUE;
			var minZ :Number = Number.MAX_VALUE;
			var maxX :Number = -minX;
			var maxY :Number = -minY;
			var maxZ :Number = -minZ;
			var v	 :Vertex3D;
			
			for each( v in vertices )
			{
				minX = Math.min(minX, v.x);
				minY = Math.min(minY, v.y);
				minZ = Math.min(minZ, v.z);
				maxX = Math.max(maxX, v.x);
				maxY = Math.max(maxY, v.y);
				maxZ = Math.max(maxZ, v.z);
			}
			
			return new AxisAlignedBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
		}

	}
}