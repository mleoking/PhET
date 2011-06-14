package away3d.core.block
{
	import away3d.core.base.*;
	import away3d.core.math.*;
	

    /**
    * Interface for objects that provide blocker instances for occlusion culling in the renderer.
    */
    public interface IBlockerProvider
    {
    	/**
    	 * Called from the <code>BlockerTraverser</code> when passing <code>Blocker</code> objects to the blocker consumer object
    	 * 
    	 * @param	consumer	The consumer instance
    	 * 
    	 * @see	away3d.core.traverse.BlockerTraverser
    	 * @see	away3d.core.block.Blocker
    	 */
        function blockers(source:Object3D, viewTransform:MatrixAway3D, consumer:IBlockerConsumer):void;
    }
}
