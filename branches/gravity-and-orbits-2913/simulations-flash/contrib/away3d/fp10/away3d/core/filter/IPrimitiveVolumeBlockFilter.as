package away3d.core.filter
{
	import away3d.cameras.*;
    import away3d.containers.*;
    import away3d.core.clip.*;
    import away3d.core.draw.*;

    /**
    * Interface for filters that work on primitive volume blocks
    */
    public interface IPrimitiveVolumeBlockFilter
    {
    	/**
    	 * Applies the filter to the volume block.
    	 * 
    	 * @param	blocklist	The volume block tree to be filtered.
    	 * @param	scene		The scene to which the volume block tree belongs.
    	 * @param	camera		The camera being used in the renderer for the volume block tree
    	 * @param	clip		The clipping object used in the renderer for the volume block tree's view.
    	 */
        function filter(blocklist:PrimitiveVolumeBlock, scene:Scene3D, camera:Camera3D, clip:Clipping):void;
    }
}
