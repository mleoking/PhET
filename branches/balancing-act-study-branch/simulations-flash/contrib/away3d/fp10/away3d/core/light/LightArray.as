package away3d.core.light
{

    /**
    * Array for storing light primitives.
    */
    public class LightArray implements ILightConsumer
    {
    	private var _ambients:Array;
    	private var _directionals:Array;
    	private var _points:Array;
    	private var _numLights:int;
    	
    	/**
    	 * The ambient light primitives stored in the consumer.
    	 */
        public function get ambients():Array
        {
        	return _ambients;
        }
        
    	/**
    	 * The directional light primitives stored in the consumer.
    	 */
        public function get directionals():Array
        {
        	return _directionals;
        }
        
    	/**
    	 * The point light primitives stored in the consumer.
    	 */
        public function get points():Array
        {
        	return _points;
        }
        
    	/**
    	 * The total number of light primitives stored in the consumer.
    	 */
		public function get numLights():int
		{
			return _numLights;
		}
        
		/**
		 * @inheritDoc
		 */
        public function ambientLight(ambient:AmbientLight):void
        {
            _ambients.push(ambient);
            _numLights++;
        }
        
		/**
		 * @inheritDoc
		 */
        public function directionalLight(directional:DirectionalLight):void
        {
            _directionals.push(directional);
            _numLights++;
        }
        
		/**
		 * @inheritDoc
		 */
        public function pointLight(point:PointLight):void
        {
            _points.push(point);
            _numLights++;
        }
        
        /**
        * Clears all light primitives from the consumer.
        */
        public function clear():void
        {
        	_ambients = [];
	        _directionals = [];
	        _points = [];
	        _numLights = 0;
        }
    }
}

