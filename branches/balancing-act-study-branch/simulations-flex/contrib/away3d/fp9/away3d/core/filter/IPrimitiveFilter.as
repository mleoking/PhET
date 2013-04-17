package away3d.core.filter
{
	import away3d.cameras.*;
    import away3d.containers.*;
    import away3d.core.clip.*;

    /**
    * Interface for filters that work on primitive arrays
    */
    public interface IPrimitiveFilter
    {
    	/**
    	 * Applies the filter to the primitive array.
    	 * 
    	 * @param	primitives	The primitives to be filtered.
    	 * @param	scene		The scene to which the primitives belongs.
    	 * @param	camera		The camera being used in the renderer for the primitives.
    	 * @param	clip		The clipping object used in the renderer for the primitive's view.
    	 * @return				The filtered array of primitives.
    	 */
        function filter(primitives:Array, scene:Scene3D, camera:Camera3D, clip:Clipping):Array;
    }
}
