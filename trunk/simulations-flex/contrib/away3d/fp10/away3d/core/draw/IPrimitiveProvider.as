package away3d.core.draw
{
    import away3d.core.base.*;
    import away3d.core.math.*;

    /**
    * Interface for objects that provide drawing primitives to the rendering process
    */
    public interface IPrimitiveProvider
    {
    	/**
    	 * Called from the <code>PrimitiveTraverser</code> when passing <code>DrawPrimitive</code> objects to the primitive consumer object
    	 * 
    	 * @see	away3d.core.traverse.PrimitiveTraverser
    	 * @see	away3d.core.draw.DrawPrimitive
    	 */
        function primitives(source:Object3D, viewTransform:MatrixAway3D, consumer:IPrimitiveConsumer):void;
    }
}
