package away3d.core.traverse
{
	import away3d.core.base.*;
    

    /**
    * Traverser that gathers blocker primitives for occlusion culling.
    */
    public class SessionTraverser extends Traverser
    { 	
		/**
		 * Creates a new <code>SessionTraverser</code> object.
		 */
        public function SessionTraverser()
        {
        }
        
		/**
		 * @inheritDoc
		 */
		public override function match(node:Object3D):Boolean
        {
            return node.visible;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function apply(node:Object3D):void
        {
            node.updateSession();
        }

    }
}
