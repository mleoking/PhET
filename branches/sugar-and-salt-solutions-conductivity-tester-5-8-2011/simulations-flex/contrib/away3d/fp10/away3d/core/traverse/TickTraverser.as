package away3d.core.traverse
{
	import away3d.core.base.*;
	
    /**
    * Traverser that fires a time-based method for all objects in scene
    */
    public class TickTraverser extends Traverser
    {
    	/**
    	 * Defines the current time in milliseconds from the start of the flash movie.
    	 */
        public var now:int;
		    	
		/**
		 * Creates a new <code>TickTraverser</code> object.
		 */
        public function TickTraverser()
        {
        }
        
		/**
		 * @inheritDoc
		 */
        public override function enter(node:Object3D):void
        {
            node.tick(now);
        }
    }
}
