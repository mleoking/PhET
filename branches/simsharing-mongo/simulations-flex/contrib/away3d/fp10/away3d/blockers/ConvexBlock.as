package away3d.blockers
{
    import away3d.core.base.*;
    import away3d.core.project.*;
    import away3d.core.utils.*;
    
    /**
    * Convex hull blocking all drawing primitives underneath.
    */
    public class ConvexBlock extends Object3D
    {
        /**
        * Toggles debug mode: blocker is visualised in the scene.
        */
        public var debug:Boolean;
        
        /**
        * Verticies to use for calculating the convex hull.
        */
        public var vertices:Array = [];
		
		/**
		 * Creates a new <code>ConvexBlock</code> object.
		 * 
		 * @param	verticies				An Array of vertices to use for calculating the convex hull.
		 * @param	init		[optional]	An initialisation object for specifying default instance properties.
		 */
        public function ConvexBlock(vertices:Array, init:Object = null)
        {
            super(init);
			
            this.vertices = vertices;
			
            debug = ini.getBoolean("debug", false);
            
            projectorType = ProjectorType.CONVEX_BLOCK;
        }
    }
}
