package away3d.core.filter
{
	import away3d.cameras.*;
	import away3d.containers.*;
	import away3d.core.clip.*;
	import away3d.core.render.*;

    /**
    * Interface for filters that work on primitive quadrant trees
    */
    public interface IPrimitiveQuadrantFilter
    {
    	/**
    	 * Applies the filter to the quadrant tree.
    	 * 
    	 * @param	pritree	The quadrant tree to be filtered.
    	 * @param	scene	The scene to which the quadrant tree belongs.
    	 * @param	camera	The camera being used in the renderer for the quadrant tree
    	 * @param	clip	The clipping object used in the renderer for the quadrant tree's view.
    	 */
        function filter(pritree:QuadrantRenderer, scene:Scene3D, camera:Camera3D, clip:Clipping):void;
    }
}
