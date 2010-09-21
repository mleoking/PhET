package org.papervision3d.core.render.command
{
	
	/**
	 * @Author Ralph Hauwert
	 */	
	import flash.geom.Point;
	
	import org.papervision3d.core.geom.renderables.AbstractRenderable;
	import org.papervision3d.core.render.data.QuadTreeNode;
	import org.papervision3d.core.render.data.RenderHitData;
	import org.papervision3d.objects.DisplayObject3D;
	
	public class RenderableListItem extends AbstractRenderListItem
	{
		public var renderable:Class;
		public var renderableInstance:AbstractRenderable;
		public var instance:DisplayObject3D;
		
		public var area:Number;
		
		public var minX:Number;
		
		/**
		 * Indicates the maximum x value of the drawing primitive.
		 */
        public var maxX:Number;
		
		/**
		 * Indicates the minimum y value of the drawing primitive.
		 */
        public var minY:Number;
		
		/**
		 * Indicates the maximum y value of the drawing primitive.
		 */
        public var maxY:Number;
        
        public var minZ:Number;
        public var maxZ:Number;
        
         public function getZ(x:Number, y:Number, focus:Number):Number
        {
            return screenZ;
        }
				
		/**
		 * Reference to the last quadrant used by the drawing primitive. Used in <code>QuadTree</code>
		 */
		public var quadrant:QuadTreeNode;
		
		public function RenderableListItem()
		{
			super();
		}
		
		public function hitTestPoint2D(point:Point, renderHitData:RenderHitData):RenderHitData
		{
			return renderHitData;
		}
		
		public function update():void{
			
        	
        
		}
		
		 public function quarter(focus:Number):Array{
		 	return []
		 }
		
	}
}