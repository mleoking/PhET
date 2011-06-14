package away3d.core.render
{
	import away3d.containers.*;
    

    /**
    * Interface for renderers capable of rendering a scene to a view
    */
    public interface IRenderer
    {   
    	/**
    	 * Executes the render pipe that resolves the 3d scene into the view.
    	 */
        function render(view:View3D):void;
        
		/**
		 * Used to trace the values of a renderer.
		 * 
		 * @return A string representation of the renderer object.
		 */
        function toString():String
    }
}
