package away3d.core.draw
{
    import away3d.core.base.*;
    
    /**
    * Volume block tree node
    */
    public class PrimitiveVolumeBlockNode
    {
    	/**
    	 * Reference to the 3d object represented by the volume block node.
    	 */
        public var source:Object3D;
        
        /**
        * The list of drawing primitives inside the volume block.
        */
        public var list:Array;
        
    	/**
    	 * Returns the minimum z value of the drawing primitives contained in the volume block node.
    	 */
        public var minZ:Number = Infinity;
        
    	/**
    	 * Returns the maximum z value of the drawing primitives contained in the volume block node.
    	 */
        public var maxZ:Number = -Infinity;
        
    	/**
    	 * Returns the minimum x value of the drawing primitives contained in the volume block node.
    	 */
        public var minX:Number = Infinity;
        
    	/**
    	 * Returns the maximum x value of the drawing primitives contained in the volume block node.
    	 */
        public var maxX:Number = -Infinity;
        
    	/**
    	 * Returns the minimum y value of the drawing primitives contained in the volume block node.
    	 */
        public var minY:Number = Infinity;
        
    	/**
    	 * Returns the maximum y value of the drawing primitives contained in the volume block node.
    	 */
        public var maxY:Number = -Infinity;
        
		
		/**
		 * Creates a new <code>PrimitiveQuadrantTreeNode</code> object.
		 * 
		 * @param	source	A reference to the 3d object represented by the volume block node.
		 */
        public function PrimitiveVolumeBlockNode(source:Object3D)
        {
            this.source = source;
            this.list = [];
        }
		
		/**
		 * Adds a primitive to the volume block
		 */
        public function push(pri:DrawPrimitive):void
        {
            if (minZ > pri.minZ)
                minZ = pri.minZ;
            if (maxZ < pri.maxZ)
                maxZ = pri.maxZ;
            if (minX > pri.minX)
                minX = pri.minX;
            if (maxX < pri.maxX)
                maxX = pri.maxX;
            if (minY > pri.minY)
                minY = pri.minY;
            if (maxY < pri.maxY)
                maxY = pri.maxY;
            list.push(pri);
        }
		
		/**
		 * Removes a primitive from the volume block
		 */
        public function remove(pri:DrawPrimitive):void
        {
            var index:int = list.indexOf(pri);
            if (index == -1)
                throw new Error("Can't remove");
            list.splice(index, 1);
        }
    }
}
