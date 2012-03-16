package away3d.core.traverse
{
	import away3d.core.base.*;
	
    /**
    * Abstract class for all traverser that perform actions on the whole scene.
    */
    public class Traverser
    {
    	/**
    	 * Determines if the specified node is required to be traversed.
    	 * 
    	 * @param	node	The 3d object to be tested.
    	 * @return			The result of the test.
    	 */
        public function match(node:Object3D):Boolean
        {
            node;//TODO : FDT Warning
            return true;
        }
		
		/**
		 * Executed when the traverser enters the node.
		 */
        public function enter(node:Object3D):void
        {
        }
		
		/**
		 * Executed when the traverser is applied to the node.
		 */
        public function apply(node:Object3D):void
        {
        }
		
		/**
		 * Executed when the traverser leaves the node.
		 */
        public function leave(node:Object3D):void
        {
        }
    }
}

