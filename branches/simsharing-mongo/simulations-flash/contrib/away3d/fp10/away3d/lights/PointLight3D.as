package away3d.lights
{
	import away3d.arcane;
	import away3d.core.base.*;
	import away3d.core.light.*;
	import away3d.core.utils.*;
	import away3d.geom.Merge;
	import away3d.materials.ColorMaterial;
	import away3d.primitives.Sphere;    
	
	use namespace arcane;
	
    /**
    * Lightsource that colors all shaded materials proportional to the dot product of the distance vector with the normal vector.
    * The scalar value of the distance is used to calulate intensity using the inverse square law of attenuation.
    */
    public class PointLight3D extends Object3D implements ILightProvider, IClonable
    {
		private var _ls:PointLight = new PointLight();
        private var _debugPrimitive:Sphere;
        private var _debugMaterial:ColorMaterial;
        private var _debug:Boolean;
		 
		/**
		 * Defines a coefficient for the ambient light intensity.
		 */
        public var ambient:Number;
		
		/**
		 * Defines a coefficient for the diffuse light intensity.
		 */
        public var diffuse:Number;
		
		/**
		 * Defines a coefficient for the specular light intensity.
		 */
        public var specular:Number;
		
		/**
		 * Defines a coefficient for the overall light intensity.
		 */
        public var brightness:Number;
		 
		/**
		 * Defines the radius of the light at full intensity, infleunced object get within this range full color of the light
		 */
        private var _radius:Number = 200;
		
		   public function get radius():Number
        {
        	return _radius;
        }
        
        public function set radius(val:Number):void
        {
        	_radius = val;
			_falloff = (radius>_falloff)? radius+1 : _falloff;
			_debugPrimitive = null;
        }
		
		/**
		 * Defines the max length of the light rays, beyond this distance, light doesn't have influence
		 * the light values are from radius 100% to falloff 0%
		 */
        private var _falloff:Number = 1000;
		
        public function get fallOff():Number
        {
        	return _falloff;
        }
        
        public function set fallOff(val:Number):void
        {
        	_falloff = (radius>_falloff)? radius+1 : val;
			_debugPrimitive = null;
			_scene.clearId(_id);        }
		
		/**
		 * Defines the color of the light object.
		 */
        private var _color:uint;
		public function get color():uint
        {
        	return _color;
        }
        
        public function set color(val:uint):void
        {
        	_color = val;
			_ls.red = ((_color & 0xFF0000) >> 16)/255;
            _ls.green = ((_color & 0xFF00) >> 8)/255;
            _ls.blue  = (_color & 0xFF)/255;
			_debugPrimitive = null;
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
			if (!_debugPrimitive){
				_debugPrimitive = new Sphere({radius:radius});
			 	_scene.setId(_debugPrimitive);			 					_debugMaterial = new ColorMaterial();
				_debugPrimitive.material = _debugMaterial;
			  
				_debugMaterial.color = color;
				_debugMaterial.alpha = .15;
				
				var m:Merge = new Merge(false, true, false);
				var spherefalloff:Sphere = new Sphere({segmentsW:10, segmentsH:8,material:_debugMaterial, radius:_falloff});
				m.apply(_debugPrimitive, spherefalloff);
			}
            
			return _debugPrimitive;
		}
		
		/**
		 * Creates a new <code>PointLight3D</code> object.
		 * 
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
        public function PointLight3D(init:Object = null)
        {
            super(init);
            
            _color = ini.getColor("color", 0xFFFFFF);
            ambient = ini.getNumber("ambient", 1);
            diffuse = ini.getNumber("diffuse", 1);
            specular = ini.getNumber("specular", 1);
            brightness = ini.getNumber("brightness", 1000)*255;
            debug = ini.getBoolean("debug", false);
            
			_radius = ini.getNumber("radius", 50);
			_falloff = ini.getNumber("fallOff", 1000);
			 
            _ls.light = this;
        }
        
		/**
		 * @inheritDoc
		 */
        public function light(consumer:ILightConsumer):void
        {
            _ls.red = ((_color & 0xFF0000) >> 16)/255;
            _ls.green = ((_color & 0xFF00) >> 8)/255;
            _ls.blue  = (_color & 0xFF)/255;
            _ls.ambient = ambient*brightness;
            _ls.diffuse = diffuse*brightness;
            _ls.specular = specular*brightness;
			 
			_ls.radius = _radius;
            _ls.fallOff = _falloff;
			 
            consumer.pointLight(_ls);
        }
		
		/**
		 * Duplicates the light object's properties to another <code>PointLight3D</code> object
		 * 
		 * @param	object	[optional]	The new object instance into which all properties are copied
		 * @return						The new object instance with duplicated properties applied
		 */
        public override function clone(object:Object3D = null):Object3D
        {
            var light:PointLight3D = (object as PointLight3D) || new PointLight3D();
            super.clone(light);
            light.color = _color;
            light.ambient = ambient;
            light.diffuse = diffuse;
            light.specular = specular;
            light.debug = debug;
			  
			light.radius = _radius;
            light.fallOff = _falloff;
			 
            return light;
        }

    }
}
