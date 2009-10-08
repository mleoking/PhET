package away3d.core.light
{

    /**
    * Interface for containers capable of storing lighting info
    */
    public interface ILightConsumer
    {
    	function get ambients():Array;
    	function get directionals():Array;
    	function get points():Array;
    	function get numLights():int;
    	
    	/**
    	 * Adds an ambient light primitive to the light consumer.
    	 *
		 * @param	ambient			The light primitive to add.
		 */
        function ambientLight(ambient:AmbientLight):void;
        
    	/**
    	 * Adds an directional light primitive to the light consumer.
    	 *
		 * @param	directional		The light primitive to add.
		 */
        function directionalLight(directional:DirectionalLight):void;
        
    	/**
    	 * Adds an point light primitive to the light consumer.
    	 *
		 * @param	point			The light primitive to add.
		 */
        function pointLight(point:PointLight):void;
        
        function clear():void;
    }
}
