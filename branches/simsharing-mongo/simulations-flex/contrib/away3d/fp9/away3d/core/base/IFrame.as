package away3d.core.base
{	
    /**
    * Interface for objects that store the vertex values for a single frame of animation
    */
    public interface IFrame
    {
		/**
		 * Adjusts the position of all vertex objects in the frame incrementally.
		 * 
		 * @param	k	The fraction by which to adjust the vertex values.
		 */
        function adjust(k:Number = 1):void;
    }
}
