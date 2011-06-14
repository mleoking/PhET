package away3d.core.filter
{
	import away3d.cameras.*;
    import away3d.containers.*;
    import away3d.core.clip.*;

    /**
    * Sorts drawing primitives by z coordinate.
    */
    public class ZSortFilter implements IPrimitiveFilter
    {
        
		/**
		 * @inheritDoc
		 */
        public function filter(primitives:Array, scene:Scene3D, camera:Camera3D, clip:Clipping):Array
        {
            primitives.sortOn("screenZ", Array.DESCENDING | Array.NUMERIC);
            return primitives;
        }
		
		/**
		 * Used to trace the values of a filter.
		 * 
		 * @return A string representation of the filter object.
		 */
        public function toString():String
        {
            return "ZSort";
        }
    }
}
