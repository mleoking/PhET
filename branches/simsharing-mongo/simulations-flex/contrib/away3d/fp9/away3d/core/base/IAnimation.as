package away3d.core.base
{
    /**
    * Interface for objects that can animate the vertex values in a mesh
    */
    public interface IAnimation
    {
		
		/**
		 * Updates the positions of vertex objects in the geometry to the current frame values
		 * 
		 * @see away3d.core.base.Frame
		 */
        function update():void;
    }
}
