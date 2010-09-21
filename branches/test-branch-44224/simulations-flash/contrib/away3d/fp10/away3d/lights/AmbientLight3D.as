package away3d.lights
{
	import away3d.arcane;
    import away3d.core.base.*;
    import away3d.core.light.*;
    import away3d.core.utils.*;
    import away3d.materials.*;
    import away3d.primitives.*;
	
	use namespace arcane;
	
    /**
    * Lightsource that colors all shaded materials evenly from any angle
    */
    public class AmbientLight3D extends Object3D implements ILightProvider, IClonable
    {
        private var _color:int;
        private var _red:int;
        private var _green:int;
        private var _blue:int;
        private var _ambient:Number;
		private var _colorDirty:Boolean;
    	private var _ambientDirty:Boolean;
		private var _ls:AmbientLight = new AmbientLight();
    	private var _debugPrimitive:Sphere;
        private var _debugMaterial:ColorMaterial;
        private var _debug:Boolean;
		
		/**
		 * Defines the color of the light object.
		 */
		public function get color():int
		{
			return _color;
		}
		
		public function set color(val:int):void
		{
			_color = val;
			_red = (_color & 0xFF0000) >> 16;
            _green = (_color & 0xFF00) >> 8;
            _blue  = (_color & 0xFF);
            _colorDirty = true;
		}
		
		/**
		 * Defines a coefficient for the ambient light intensity.
		 */
		public function get ambient():Number
		{
			return _ambient;
		}
    	
		public function set ambient(val:Number):void
		{
			_ambient = val;
            _ambientDirty = true;
		}
        
        /**
        * Toggles debug mode: light object is visualised in the scene.
        */
        public function get debug():Boolean
        {
        	return _debug;
        }
        
        public function set debug(val:Boolean):void
        {
        	_debug = val;
        }
        
		public function get debugPrimitive():Object3D
		{
			if (!_debugPrimitive) {
				_debugPrimitive = new Sphere();
				_scene.clearId(_id);
			}
			
			if (!_debugMaterial) {
				_debugMaterial = new ColorMaterial();
				_debugPrimitive.material = _debugMaterial;
			}
			
            _debugMaterial.color = color;
            
			return _debugPrimitive;
		}
		
		/**
		 * Creates a new <code>AmbientLight3D</code> object.
		 * 
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
        public function AmbientLight3D(init:Object = null)
        {
            super(init);
            
            color = ini.getColor("color", 0xFFFFFF);
            ambient = ini.getNumber("ambient", 0.5, {min:0, max:1});
            debug = ini.getBoolean("debug", false);
        }
        
		/**
		 * @inheritDoc
		 */
        public function light(consumer:ILightConsumer):void
        {
           //update color
			if (_colorDirty) {
				_ls.red = _red;
				_ls.green = _green;
				_ls.blue = _blue;
	            _colorDirty = false;
			}
        	
        	//update ambient
            if (_ambientDirty) {
        		_ambientDirty = false;
	        	_ls.updateAmbientBitmap(_ambient);
        	}
        	
            consumer.ambientLight(_ls);
        }
		
		/**
		 * Duplicates the light object's properties to another <code>AmbientLight3D</code> object
		 * 
		 * @param	object	[optional]	The new object instance into which all properties are copied
		 * @return						The new object instance with duplicated properties applied
		 */
        public override function clone(object:Object3D = null):Object3D
        {
            var light:AmbientLight3D = (object as AmbientLight3D) || new AmbientLight3D();
            super.clone(light);
            light.color = color;
            light.ambient = ambient;
            light.debug = debug;
            return light;
        }

    }
}
